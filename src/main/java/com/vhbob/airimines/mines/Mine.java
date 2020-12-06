package com.vhbob.airimines.mines;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.vhbob.airimines.AiridaleMines;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class Mine {

    Region mineBlocks;
    Region notificationRegion;

    // This method will begin to reset the mine
    public void reset() {
        // Notify players
        for (Player p : AiridaleMines.getPlugin().getServer().getOnlinePlayers()) {
            if (notificationRegion.contains(BukkitAdapter.asBlockVector(p.getLocation()))) {
                p.sendMessage(ChatColor.RED + "The mine you are in is resetting!");
            }
        }
    }

}
