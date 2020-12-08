package com.vhbob.airimines;

import com.vhbob.airimines.commands.*;
import com.vhbob.airimines.mines.ListMines;
import com.vhbob.airimines.mines.Mine;
import com.vhbob.airimines.mines.PercentMine;
import com.vhbob.airimines.mines.SchematicMine;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AiridaleMines extends JavaPlugin {

    private static AiridaleMines plugin;
    private ArrayList<Mine> activeMines;

    @Override
    public void onEnable() {
        plugin = this;
        getCommand("CreateSchematicMine").setExecutor(new CreateSchematicMine());
        getCommand("CreatePercentMine").setExecutor(new CreatePercentMine());
        getCommand("SetNotifyArea").setExecutor(new SetNotifyArea());
        getCommand("SetMineChance").setExecutor(new SetMineChance());
        getCommand("ListMines").setExecutor(new ListMines());
        getCommand("DeleteMine").setExecutor(new DeleteMine());
        // Setup data files
        saveDefaultConfig();
        // Load mines
        activeMines = new ArrayList<>();
        File minesFile = new File(getDataFolder(), "mines");
        if (minesFile.exists()) {
            for (String fileName : minesFile.list()) {
                if (fileName.contains(".schem")) {
                    continue;
                }
                String mineName = fileName.replace(".yml", "");
                // See if it is a schematic mine
                File schFile = new File(getDataFolder() + File.separator  + "mines", mineName + ".schem");
                if (schFile.exists() && schFile.isFile()) {
                    try {
                        activeMines.add(new SchematicMine(mineName));
                    } catch (IOException | InvalidConfigurationException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        activeMines.add(new PercentMine(mineName));
                    } catch (IOException | InvalidConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Save mine data
        File minesFile = new File(getDataFolder(), "mines");
        if (minesFile.exists()) {
            for (File file : minesFile.listFiles()) {
                file.delete();
            }
        }
        for (Mine mine : activeMines) {
            try {
                mine.saveMine();
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public static AiridaleMines getPlugin() {
        return plugin;
    }

    public void registerMine(Mine mine) {
        activeMines.add(mine);
    }

    // Get a mine by name
    public Mine getMine(String name) {
        for (Mine mine : activeMines) {
            if (mine.getName().equalsIgnoreCase(name))
                return mine;
        }
        return null;
    }

    // Deactivate a mine
    public boolean deleteMine(String name) {
        Mine toDel = null;
        for (Mine mine : activeMines) {
            if (mine.getName().equalsIgnoreCase(name)) {
                toDel = mine;
                break;
            }
        }
        if (toDel == null)
            return false;
        toDel.endTask();
        activeMines.remove(toDel);
        return true;
    }

    public int numActiveMines() {
        return activeMines.size();
    }

    public ArrayList<Mine> getActiveMines() {
        return activeMines;
    }
}
