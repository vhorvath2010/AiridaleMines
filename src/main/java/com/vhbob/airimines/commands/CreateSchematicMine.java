package com.vhbob.airimines.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.SessionOwner;
import com.vhbob.airimines.AiridaleMines;
import com.vhbob.airimines.mines.SchematicMine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateSchematicMine implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("CreateMine")) {
            if (strings.length == 1) {
                if (commandSender instanceof Player) {
                    Player p = (Player) commandSender;
                    if (WorldEdit.getInstance().getSessionManager().contains((SessionOwner) p)) {
                        try {
                            CuboidRegion region = WorldEdit.getInstance().getSessionManager().get((SessionOwner) p)
                                    .getSelection(BukkitAdapter.adapt(p.getWorld())).getBoundingBox();
                            SchematicMine mine = new SchematicMine(region, new BlockArrayClipboard(region));
                            AiridaleMines.getPlugin().registerMine(mine);
                            p.sendMessage(ChatColor.GREEN + "Schematic mine created!");
                        } catch (IncompleteRegionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }
}
