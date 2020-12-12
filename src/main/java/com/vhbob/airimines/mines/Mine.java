package com.vhbob.airimines.mines;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.vhbob.airimines.AiridaleMines;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public abstract class Mine {

    Region mineBlocks;
    String name;
    int resetTaskID;
    int placeholderTaskID;
    long nextReset;
    Region notificationRegion;

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

    public abstract void activateTasks();

    public void endTask() {
        Bukkit.getScheduler().cancelTask(resetTaskID);
        Bukkit.getScheduler().cancelTask(placeholderTaskID);
    }

    public String getUntilReset() {
        return String.format("%d:%02d", nextReset / 60, nextReset % 60);
    }

}