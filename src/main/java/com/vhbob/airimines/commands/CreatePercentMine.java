package com.vhbob.airimines.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionOwner;
import com.vhbob.airimines.AiridaleMines;
import com.vhbob.airimines.mines.PercentMine;
import com.vhbob.airimines.mines.SchematicMine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreatePercentMine implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("CreatePercentMine")) {
            if (strings.length == 1) {
                if (commandSender instanceof Player) {
                    Player p = (Player) commandSender;
                    if (AiridaleMines.getPlugin().getMine(strings[0]) == null) {
                        try {
                            Region region = WorldEdit.getInstance().getSessionManager().get((SessionOwner) BukkitAdapter.adapt(p))
                                    .getSelection(BukkitAdapter.adapt(p.getWorld()));
                            PercentMine mine = new PercentMine(strings[0], region, p.getLocation());
                            AiridaleMines.getPlugin().registerMine(mine);
                            p.sendMessage(ChatColor.GREEN + "Percentage mine created! Remember to set the notification area and add block rates!");
                            return true;
                        } catch (IncompleteRegionException e) {
                            return false;
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "A mine with that name already exists!");
                    }
                }
            }
        }
        return false;
    }
}
