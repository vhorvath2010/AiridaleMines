package com.vhbob.airimines.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.SessionOwner;
import com.vhbob.airimines.AiridaleMines;
import com.vhbob.airimines.mines.Mine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetNotifyArea implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("SetNotifyArea")) {
            if (strings.length == 1) {
                Mine mine = AiridaleMines.getPlugin().getMine(strings[0]);
                if (mine != null) {
                    Player p = (Player) commandSender;
                    try {
                        CuboidRegion region = WorldEdit.getInstance().getSessionManager().get((SessionOwner) BukkitAdapter.adapt(p))
                                .getSelection(BukkitAdapter.adapt(p.getWorld())).getBoundingBox();
                        mine.setNotificationRegion(region);
                        p.sendMessage(ChatColor.GREEN + "Notification area updated!");
                    } catch (IncompleteRegionException e) {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
