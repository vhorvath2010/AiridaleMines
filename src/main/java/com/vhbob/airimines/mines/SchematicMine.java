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
import com.vhbob.airimines.AiridaleMines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;

public class SchematicMine extends Mine {

    private BlockArrayClipboard schematic;
    private int taskID;
    private Location tpLoc;

    public SchematicMine(String name, Region mineBlocks, BlockArrayClipboard schematic, Location tpLoc) {
        this.name = name;
        this.tpLoc = tpLoc;
        this.mineBlocks = mineBlocks;
        this.schematic = schematic;
        // Calculate offset for mine reset
        int mines = AiridaleMines.getPlugin().numActiveMines();
        long offset = (long) 100 * mines;
        // Schedule resets
        long delay = (long) (20 * AiridaleMines.getPlugin().getConfig().getDouble("reset-interval"));
        taskID = new BukkitRunnable() {
            @Override
            public void run() {
                reset();
            }
        }.runTaskTimer(AiridaleMines.getPlugin(), delay + offset, delay).getTaskId();
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
        BlockVector3 nMin = BlockVector3.at(minesConfig.getInt("notify.min.x"),
                minesConfig.getInt("notify.min.y"),minesConfig.getInt("notify.min.z"));
        BlockVector3 nMax = BlockVector3.at(minesConfig.getInt("notify.max.x"),
                minesConfig.getInt("notify.max.y"),minesConfig.getInt("notify.max.z"));
        this.notificationRegion = new CuboidRegion(BukkitAdapter.adapt(tpLoc.getWorld()), nMin, nMax);
        // Load schematic
        File file = new File(AiridaleMines.getPlugin().getDataFolder() + File.separator + "mines", name + ".schem");
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        Clipboard loadedBoard;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            loadedBoard = reader.read();
        }
        this.schematic = (BlockArrayClipboard) loadedBoard;
        int mines = AiridaleMines.getPlugin().numActiveMines();
        long offset = (long) 100 * mines;
        // Schedule resets
        long delay = (long) (20 * AiridaleMines.getPlugin().getConfig().getDouble("reset-interval"));
        taskID = new BukkitRunnable() {
            @Override
            public void run() {
                reset();
            }
        }.runTaskTimer(AiridaleMines.getPlugin(), delay + offset, delay).getTaskId();
    }

    @Override
    public void reset() {
        super.reset();
        // Tp out of mine
        for (Player p : AiridaleMines.getPlugin().getServer().getOnlinePlayers()) {
            if (notificationRegion.contains(BukkitAdapter.asBlockVector(p.getLocation()))) {
                p.teleport(tpLoc);
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

    public void endTask() {
        Bukkit.getScheduler().cancelTask(taskID);
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
        // Save notify region
        minesConfig.set("notify.min.x", notificationRegion.getMinimumPoint().getBlockX());
        minesConfig.set("notify.min.y", notificationRegion.getMinimumPoint().getBlockY());
        minesConfig.set("notify.min.z", notificationRegion.getMinimumPoint().getBlockZ());
        minesConfig.set("notify.max.x", notificationRegion.getMaximumPoint().getBlockX());
        minesConfig.set("notify.max.y", notificationRegion.getMaximumPoint().getBlockY());
        minesConfig.set("notify.max.z", notificationRegion.getMaximumPoint().getBlockZ());
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