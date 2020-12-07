package com.vhbob.airimines.mines;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.vhbob.airimines.AiridaleMines;
import com.vhbob.airimines.util.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PercentMine extends Mine {

    private RandomCollection<Material> chances;
    private int taskID;
    private Location locTp;

    public PercentMine(String name, Region mineRegion, Location locTp) {
        chances = new RandomCollection<Material>();
        this.name = name;
        this.mineBlocks = mineRegion;
        this.locTp = locTp;
        long delay = (long) (20 * AiridaleMines.getPlugin().getConfig().getDouble("reset-interval"));
        long offset = (long) 100 * AiridaleMines.getPlugin().numActiveMines();
        taskID = new BukkitRunnable() {
            @Override
            public void run() {
                reset();
            }
        }.runTaskTimer(AiridaleMines.getPlugin(), delay + offset, delay).getTaskId();
    }

    @Override
    public void reset() {
        super.reset();
        BlockVector3 min = mineBlocks.getMinimumPoint();
        BlockVector3 max = mineBlocks.getMaximumPoint();
        int configDelay = AiridaleMines.getPlugin().getConfig().getInt("layer-delay");
        final World world = Bukkit.getWorld(mineBlocks.getWorld().getName());
        for (Player p : AiridaleMines.getPlugin().getServer().getOnlinePlayers()) {
            if (notificationRegion.contains(BukkitAdapter.asBlockVector(p.getLocation()))) {
                p.teleport(locTp);
            }
        }
        for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
            int delay = configDelay * (y - min.getBlockY());
            int finalY = y;
            new BukkitRunnable() {
                @Override
                public void run() {
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

    public void endTask() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

}
