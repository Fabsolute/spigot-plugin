package org.gronia.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.chunk.ChunkPlugin;
import org.gronia.plugin.craft.Craft;
import org.gronia.plugin.repair.RepairPlugin;
import org.gronia.plugin.storage.StoragePlugin;

public class Gronia extends JavaPlugin {
    private final SubPlugin<?>[] plugins = new SubPlugin[]{
            new StoragePlugin(this),
            new ChunkPlugin(this),
            new Craft(this),
            new RepairPlugin(this)
    };

    @Override
    public void onEnable() {
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
