package com.vhbob.airimines.commands;

import com.vhbob.airimines.AiridaleMines;
import com.vhbob.airimines.mines.Mine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetMine implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("ResetMine")) {
            if (strings.length == 1) {
                Mine mine = AiridaleMines.getPlugin().getMine(strings[0]);
                if (mine != null) {
                    mine.reset();
                    commandSender.sendMessage(ChatColor.GREEN + "Reset " + mine.getName());
                }
            }
        }
        return false;
    }
}
