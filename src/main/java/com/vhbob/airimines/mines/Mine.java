package com.vhbob.airimines.mines;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.vhbob.airimines.AiridaleMines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public abstract class Mine {

    Region mineBlocks;
    Region notificationRegion;
    String name;
    int resetTaskID;
    int placeholderTaskID;
    long nextReset;

    // This method will begin to reset the mine
    public void reset() {
        // Notify players
        for (Player p : AiridaleMines.getPlugin().getServer().getOnlinePlayers()) {
            if (notificationRegion != null && notificationRegion.contains(BukkitAdapter.asBlockVector(p.getLocation()))) {
                p.sendMessage(ChatColor.RED + "The mine you are in is resetting!");
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setNotificationRegion(Region notificationRegion) {
        this.notificationRegion = notificationRegion.clone();
    }

    public abstract void saveMine() throws IOException, InvalidConfigurationException;

    public void activateTasks() {
        // Setup mine resets
        final long delay = (long) (20 * AiridaleMines.getPlugin().getConfig().getDouble("reset-interval"));
        final long offset = (long) 100 * AiridaleMines.getPlugin().numActiveMines();
        resetTaskID = new BukkitRunnable() {
            @Override
            public void run() {
                reset();
            }
        }.runTaskTimer(AiridaleMines.getPlugin(), delay + offset, delay).getTaskId();
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

    public void endTask() {
        Bukkit.getScheduler().cancelTask(resetTaskID);
        Bukkit.getScheduler().cancelTask(placeholderTaskID);
    }

    public String getUntilReset() {
        return String.format("%d:%02d", nextReset / 60, nextReset % 60);
    }

}