package com.vhbob.airimines.mines;

import com.vhbob.airimines.AiridaleMines;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ListMines implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("ListMines")) {
            commandSender.sendMessage(ChatColor.GREEN + "[ ACTIVE MINES ]");
            for (Mine mine : AiridaleMines.getPlugin().getActiveMines())
                commandSender.sendMessage(mine.getName() + ": " + ChatColor.GREEN + "ACTIVE");
        }
        return false;
    }
}
