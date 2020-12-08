package com.vhbob.airimines.commands;

import com.vhbob.airimines.AiridaleMines;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteMine implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("DeleteMine")) {
            if (strings.length == 1) {
                if (AiridaleMines.getPlugin().deleteMine(strings[0])) {
                    commandSender.sendMessage(ChatColor.RED + strings[0] + " has been deleted!");
                    return true;
                }
            }
        }
        return false;
    }
}
