package com.vhbob.airimines.mines;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.vhbob.airimines.AiridaleMines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        try (EditSession session = WorldEdit.getInstance().newEditSession(schematic.getRegion().getWorld())) {
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

}