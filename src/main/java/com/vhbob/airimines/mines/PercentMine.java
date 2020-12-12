package com.vhbob.airimines.mines;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.vhbob.airimines.AiridaleMines;
import com.vhbob.airimines.util.RandomCollection;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.NavigableMap;

public class PercentMine extends Mine {

    private RandomCollection<Material> chances;
    private Location locTp;

    public PercentMine(String name, Region mineRegion, Location locTp) {
        chances = new RandomCollection<Material>();
        this.name = name;
        this.mineBlocks = mineRegion.clone();
        this.locTp = locTp;
        activateTasks();
    }

    public PercentMine(String name) throws IOException, InvalidConfigurationException {
        // Grab config file
        File minesFile = new File(AiridaleMines.getPlugin().getDataFolder() + File.separator  + "mines", name + ".yml");
        YamlConfiguration minesConfig = new YamlConfiguration();
        minesConfig.load(minesFile);
        // Load basic data
        this.name = minesConfig.getString("name");
        this.locTp = (Location) minesConfig.get("loc");
        // Load regions
        BlockVector3 bMin = BlockVector3.at(minesConfig.getInt("blocks.min.x"),
                minesConfig.getInt("blocks.min.y"),minesConfig.getInt("blocks.min.z"));
        BlockVector3 bMax = BlockVector3.at(minesConfig.getInt("blocks.max.x"),
                minesConfig.getInt("blocks.max.y"),minesConfig.getInt("blocks.max.z"));
        this.mineBlocks = new CuboidRegion(BukkitAdapter.adapt(locTp.getWorld()), bMin, bMax);
        BlockVector3 nMin = BlockVector3.at(minesConfig.getInt("notify.min.x"),
                minesConfig.getInt("notify.min.y"),minesConfig.getInt("notify.min.z"));
        BlockVector3 nMax = BlockVector3.at(minesConfig.getInt("notify.max.x"),
                minesConfig.getInt("notify.max.y"),minesConfig.getInt("notify.max.z"));
        this.notificationRegion = new CuboidRegion(BukkitAdapter.adapt(locTp.getWorld()), nMin, nMax);
        this.chances = new RandomCollection<Material>();
        for (String typeName : minesConfig.getConfigurationSection("chance").getKeys(false)) {
            Material type = Material.valueOf(typeName);
            this.chances.add(Double.parseDouble(minesConfig.getString("chance." + typeName)), type);
        }
        activateTasks();
    }

    @Override
    public void reset() {
        if (chances.next() == null)
            return;
        super.reset();
        // TP at proper time
        BlockVector3 min = mineBlocks.getMinimumPoint();
        BlockVector3 max = mineBlocks.getMaximumPoint();
        int configDelay = AiridaleMines.getPlugin().getConfig().getInt("layer-delay");
        final World world = Bukkit.getWorld(mineBlocks.getWorld().getName());
        for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
            int delay = configDelay * (y - min.getBlockY());
            int finalY = y;
            new BukkitRunnable() {
                @Override
                public void run() {
                    // TP players out of this layer
                    BlockVector3 layerMin = BlockVector3.at(min.getX(), finalY, min.getZ());
                    BlockVector3 layerMax = BlockVector3.at(max.getX(), finalY, max.getZ());
                    ProtectedRegion region = new ProtectedCuboidRegion("test", layerMin, layerMax);
                    for (Player p : AiridaleMines.getPlugin().getServer().getOnlinePlayers()) {
                        if (region.contains(BukkitAdapter.asBlockVector(p.getLocation()))) {
                            p.teleport(locTp);
                        }
                    }
                    // Set blocks
                    for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
                        for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
                            Location loc = new Location(world, x, finalY, z);
                            loc.getBlock().setType(chances.next());
                        }
                    }
                }
            }.runTaskLater(AiridaleMines.getPlugin(), delay);
        }
    }

    public void addBlock(Material type, int weight) {
        chances.add(weight, type);
    }

    @Override
    public void saveMine() throws IOException, InvalidConfigurationException {
        File minesFile = new File(AiridaleMines.getPlugin().getDataFolder() + File.separator  + "mines", name + ".yml");
        YamlConfiguration minesConfig = new YamlConfiguration();
        // Save data
        minesConfig.set("name", name);
        minesConfig.set("loc", locTp);
        // Save blocks region
        minesConfig.set("blocks.min.x", mineBlocks.getMinimumPoint().getBlockX());
        minesConfig.set("blocks.min.y", mineBlocks.getMinimumPoint().getBlockY());
        minesConfig.set("blocks.min.z", mineBlocks.getMinimumPoint().getBlockZ());
        minesConfig.set("blocks.max.x", mineBlocks.getMaximumPoint().getBlockX());
        minesConfig.set("blocks.max.y", mineBlocks.getMaximumPoint().getBlockY());
        minesConfig.set("blocks.max.z", mineBlocks.getMaximumPoint().getBlockZ());
        // Save notify region
        minesConfig.set("notify.min.x", notificationRegion.getMinimumPoint().getBlockX());
        minesConfig.set("notify.min.y", notificationRegion.getMinimumPoint().getBlockY());
        minesConfig.set("notify.min.z", notificationRegion.getMinimumPoint().getBlockZ());
        minesConfig.set("notify.max.x", notificationRegion.getMaximumPoint().getBlockX());
        minesConfig.set("notify.max.y", notificationRegion.getMaximumPoint().getBlockY());
        minesConfig.set("notify.max.z", notificationRegion.getMaximumPoint().getBlockZ());
        NavigableMap<Double, Material> chanceMap = chances.values();
        double cumulativeChance = 0;
        for (Double chance : chanceMap.keySet()) {
            minesConfig.set("chance." + chanceMap.get(chance), chance - cumulativeChance);
            cumulativeChance += chance;
        }
        // Save file
        minesConfig.save(minesFile);
    }
}
