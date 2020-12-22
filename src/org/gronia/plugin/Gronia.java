package org.gronia.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.chunk.ChunkPlugin;
import org.gronia.plugin.craft.CraftPlugin;
import org.gronia.plugin.dancer.DancerPlugin;
import org.gronia.plugin.griefing.GriefingPlugin;
import org.gronia.plugin.planter.PlanterPlugin;
import org.gronia.plugin.ptp.PerfectTPPlugin;
import org.gronia.plugin.repair.RepairPlugin;
import org.gronia.plugin.storage.StoragePlugin;
import org.gronia.plugin.swap.SwapPlugin;

public class Gronia extends JavaPlugin {
    private final SubPlugin<?>[] plugins = new SubPlugin[]{
            new StoragePlugin(this),
            new ChunkPlugin(this),
            new CraftPlugin(this),
            new RepairPlugin(this),
            new GriefingPlugin(this),
            new PerfectTPPlugin(this),
            new DancerPlugin(this),
            new PlanterPlugin(this),
            new SwapPlugin(this)
    };

    @Override
    public void onEnable() {
        SubPlugin<SwapPlugin>[] plugins = new SubPlugin[10];
        
        for (SubPlugin<?> plugin : this.plugins) {
            plugin.onEnable();
        }
    }

    @Override
    public void onDisable() {
        for (SubPlugin<?> plugin : this.plugins) {
            plugin.onDisable();
        }
    }

    public <T extends SubPlugin<T>> T getSubPlugin(Class<T> clazz) {
        for (SubPlugin<?> plugin : this.plugins) {
            if (clazz.isInstance(plugin)) {
                return (T) plugin;
            }
        }

        return null;
    }
}
