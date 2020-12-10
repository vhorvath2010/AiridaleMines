package com.vhbob.airimines.mines;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.vhbob.airimines.AiridaleMines;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;

public abstract class Mine {

    Region mineBlocks;
    Region notificationRegion;
    String name;
    int taskID;
    // The system time in milliseconds that the mine first reset
    long firstReset;
    // The frequency in milliseconds that the mine will reset
    long resetFrequency;

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

    public abstract void endTask();

    public abstract void saveMine() throws IOException, InvalidConfigurationException;

    public String getUntilReset() {
        String until = "N/A";

    }

}