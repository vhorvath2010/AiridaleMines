package com.vhbob.airimines.commands;

import com.vhbob.airimines.AiridaleMines;
import com.vhbob.airimines.mines.Mine;
import com.vhbob.airimines.mines.PercentMine;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetMineChance implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("SetMineChance")) {
            if (strings.length == 3) {
                Mine mine = AiridaleMines.getPlugin().getMine(strings[0]);
                Material type = Material.valueOf(strings[1]);
                int chance = Integer.parseInt(strings[2]);
                if (mine instanceof PercentMine) {
                    ((PercentMine) mine).addBlock(type, chance);
                    commandSender.sendMessage(ChatColor.GREEN + "Set the weighting for " + type.toString() + " to " + chance);
                    return true;
                }
            }
        }
        return false;
    }
}
