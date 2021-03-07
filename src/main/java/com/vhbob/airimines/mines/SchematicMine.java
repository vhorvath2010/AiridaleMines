package com.vhbob.airimines.mines;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.vhbob.airimines.AiridaleMines;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;

public class SchematicMine extends Mine {

    private BlockArrayClipboard schematic;

    public SchematicMine(String name, Region mineBlocks, BlockArrayClipboard schematic, Location tpLoc) {
        this.name = name;
        this.tpLoc = tpLoc;
        this.mineBlocks = mineBlocks.clone();
        this.schematic = schematic;
        activateTasks();
    }

    public SchematicMine(String name) throws IOException, InvalidConfigurationException {
        // Load name and everything
        File minesFile = new File(AiridaleMines.getPlugin().getDataFolder() + File.separator  + "mines", name + ".yml");
        YamlConfiguration minesConfig = new YamlConfiguration();
        minesConfig.load(minesFile);
        this.name = minesConfig.getString("name");
        tpLoc = (Location) minesConfig.get("loc");
        // Load regions
        BlockVector3 bMin = BlockVector3.at(minesConfig.getInt("blocks.min.x"),
                minesConfig.getInt("blocks.min.y"),minesConfig.getInt("blocks.min.z"));
        BlockVector3 bMax = BlockVector3.at(minesConfig.getInt("blocks.max.x"),
                minesConfig.getInt("blocks.max.y"),minesConfig.getInt("blocks.max.z"));
        this.mineBlocks = new CuboidRegion(BukkitAdapter.adapt(tpLoc.getWorld()), bMin, bMax);
        // Load schematic
        File file = new File(AiridaleMines.getPlugin().getDataFolder() + File.separator + "mines", name + ".schem");
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        Clipboard loadedBoard;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            loadedBoard = reader.read();
        }
        this.schematic = (BlockArrayClipboard) loadedBoard;
        activateTasks();
    }

    @Override
    public void activateTasks()  {
        // Setup mine resets
        final long delay = (long) (20 * AiridaleMines.getPlugin().getConfig().getDouble("reset-interval"));
        final long offset = (long) 100 * AiridaleMines.getPlugin().numActiveMines();
        final ProtectedRegion insideMine = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(tpLoc.getWorld())).getRegion("mine-" + name);
        resetTaskID = new BukkitRunnable() {
            @Override
            public void run() {
                // Run countdown
                final int[] countdown = {10};
                final int countdownID = new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Send countdown to players
                        for (Player p : AiridaleMines.getPlugin().getServer().getOnlinePlayers()) {
                            // Ensure they're in the same world
                            if (!p.getLocation().getWorld().equals(tpLoc.getWorld())) {
                                continue;
                            }
                            if (insideMine != null && insideMine.contains(BukkitAdapter.asBlockVector(p.getLocation()))) {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                        TextComponent.fromLegacyText(ChatColor.RED + ""
                                                + ChatColor.BOLD + "Mine resetting in " + countdown[0] + "!"));
                            }
                        }
                        countdown[0] = countdown[0] - 1;
                    }
                }.runTaskTimer(AiridaleMines.getPlugin(), 0, 20).getTaskId();
                // Reset mine
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        reset();
                        Bukkit.getScheduler().cancelTask(countdownID);
                    }
                }.runTaskLater(AiridaleMines.getPlugin(), 200);
            }
        }.runTaskTimer(AiridaleMines.getPlugin(), delay + offset - 200, delay).getTaskId();
        // Schedule placeholder updates
        nextReset = (delay + offset) / 20;
        placeholderTaskID = new BukkitRunnable() {
            @Override
            public void run() {
                nextReset -= 1;
                if (nextReset == 0) {
                    nextReset = delay / 20;
                }
            }
        }.runTaskTimer(AiridaleMines.getPlugin(), 0, 20).getTaskId();
    }

    @Override
    public void reset() {
        // Tp out of mine
        ProtectedRegion insideMine = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(tpLoc.getWorld())).getRegion("mine-" + name);
        for (Player p : AiridaleMines.getPlugin().getServer().getOnlinePlayers()) {
            // Ensure they're in the same world
            if (!p.getLocation().getWorld().equals(tpLoc.getWorld())) {
                continue;
            }
            if (insideMine.contains(BukkitAdapter.asBlockVector(p.getLocation()))) {
                p.teleport(tpLoc);
                p.sendMessage(ChatColor.RED + "The mine you were in is resetting!");
            }
        }
        // Load blocks
        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(tpLoc.getWorld()))) {
            Operation operation = new ClipboardHolder(schematic)
                    .createPaste(session)
                    .to(mineBlocks.getMinimumPoint())
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveMine() throws IOException, InvalidConfigurationException {
        // Save schematic
        saveSchematic();
        // Save other data
        File minesFile = new File(AiridaleMines.getPlugin().getDataFolder() + File.separator  + "mines", name + ".yml");
        YamlConfiguration minesConfig = new YamlConfiguration();
        minesConfig.set("name", name);
        minesConfig.set("loc", tpLoc);
        // Save blocks region
        minesConfig.set("blocks.min.x", mineBlocks.getMinimumPoint().getBlockX());
        minesConfig.set("blocks.min.y", mineBlocks.getMinimumPoint().getBlockY());
        minesConfig.set("blocks.min.z", mineBlocks.getMinimumPoint().getBlockZ());
        minesConfig.set("blocks.max.x", mineBlocks.getMaximumPoint().getBlockX());
        minesConfig.set("blocks.max.y", mineBlocks.getMaximumPoint().getBlockY());
        minesConfig.set("blocks.max.z", mineBlocks.getMaximumPoint().getBlockZ());
        // Save file
        minesConfig.save(minesFile);
    }

    private void saveSchematic() throws IOException {
        // Generate new file
        File file = new File(AiridaleMines.getPlugin().getDataFolder() + File.separator + "mines", name + ".schem");
        // Save clipboard
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(schematic);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to save schematic for: " + name);
        }
    }
}