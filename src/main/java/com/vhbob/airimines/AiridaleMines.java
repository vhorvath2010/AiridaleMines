package com.vhbob.airimines;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class AiridaleMines extends JavaPlugin {

    private AiridaleMines plugin;
    private File minesFile;
    private FileConfiguration minesConfig;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
    }

    public AiridaleMines getPlugin() {
        return plugin;
    }

    public FileConfiguration getMinesConfig() {
        return minesConfig;
    }

}
