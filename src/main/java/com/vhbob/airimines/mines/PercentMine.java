package com.vhbob.airimines.mines;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;

import java.util.HashMap;

public class PercentMine extends Mine {

    private HashMap<Material, Integer> chances;

    public PercentMine(String name, Region mineRegion) {
        this.name = name;
        this.mineBlocks = mineRegion;
    }

    @Override
    public void reset() {
        super.reset();

    }
}
