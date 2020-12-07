package com.vhbob.airimines;

import com.vhbob.airimines.commands.CreatePercentMine;
import com.vhbob.airimines.commands.CreateSchematicMine;
import com.vhbob.airimines.commands.SetMineChance;
import com.vhbob.airimines.commands.SetNotifyArea;
import com.vhbob.airimines.mines.Mine;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
        getCommand("CreatePercentMine").setExecutor(new CreatePercentMine());
        getCommand("SetNotifyArea").setExecutor(new SetNotifyArea());
        getCommand("SetMineChance").setExecutor(new SetMineChance());
        // Setup data files
        saveDefaultConfig();
        createMineData();
        // Load mines
        activeMines = new ArrayList<>();
    }

    private void createMineData() {
        minesFile = new File(plugin.getDataFolder(), "mines.yml");
        if (!minesFile.exists()) {
            minesFile.getParentFile().mkdirs();
            saveResource("mines.yml", false);
        }

        minesConfig = new YamlConfiguration();
        try {
            minesConfig.load(minesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
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

    // Get a mine by name
    public Mine getMine(String name) {
        for (Mine mine : activeMines)
            if (mine.getName().equalsIgnoreCase(name))
                return mine;
        return null;
    }

    public int numActiveMines() {
        return activeMines.size();
    }

}
