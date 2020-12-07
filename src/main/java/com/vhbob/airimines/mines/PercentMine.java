package com.vhbob.airimines.mines;

import com.sk89q.worldedit.regions.Region;
import com.vhbob.airimines.AiridaleMines;
import com.vhbob.airimines.util.RandomCollection;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class PercentMine extends Mine {

    private RandomCollection<Material> chances;

    public PercentMine(String name, Region mineRegion) {
        this.name = name;
        this.mineBlocks = mineRegion;
        long delay = (long) (20 * AiridaleMines.getPlugin().getConfig().getDouble("reset-interval"));
        long offset = (long) 100 * AiridaleMines.getPlugin().numActiveMines();
        new BukkitRunnable() {
            @Override
            public void run() {
                reset();
            }
        }.runTaskTimer(AiridaleMines.getPlugin(), offset, delay);
    }

    @Override
    public void reset() {
        super.reset();
    }

    public void addBlock(Material type, int weight) {
        chances.add(weight, type);
    }

}
