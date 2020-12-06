package com.vhbob.airimines;

import com.vhbob.airimines.commands.CreateSchematicMine;
import com.vhbob.airimines.mines.Mine;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AiridaleMines extends JavaPlugin {

    private static AiridaleMines plugin;
    private File minesFile;
    private FileConfiguration minesConfig;
    private ArrayList<Mine> activeMines;

    @Override
    public void onEnable() {
        plugin = this;
        getCommand("CreateSchematicMine").setExecutor(new CreateSchematicMine());
        // Setup data files
        saveDefaultConfig();
        minesFile = new File(plugin.getDataFolder(), "mines.yml");
        // Load mines
        activeMines = new ArrayList<>();
    }

    @Override
    public void onDisable() {
        // Save mine data
        try {
            minesConfig.save(minesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AiridaleMines getPlugin() {
        return plugin;
    }

    public FileConfiguration getMinesConfig() {
        return minesConfig;
    }

    public void registerMine(Mine mine) {
        activeMines.add(mine);
    }

}
