package org.gronia.disabled.chunk;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

import java.util.List;
import java.util.Set;

public class ChunkPlugin extends SubPlugin<ChunkPlugin> {
    public ChunkPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "chunk";
    }

    @Override
    public SubListener<ChunkPlugin> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<ChunkPlugin> getExecutor() {
        return new ChunkCommand(this);
    }

    @Override
    public SubTabCompleter<ChunkPlugin> getTabCompleter() {
        return new ChunkTabCompleter(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Set<String> worlds = this.getConfig().getKeys(false);
        for (String worldName : worlds) {
            World world = this.getPlugin().getServer().getWorld(worldName);
            assert world != null;

            for (Chunk chunk : world.getForceLoadedChunks()) {
                chunk.setForceLoaded(false);
            }

            ConfigurationSection subConfig = this.getConfig().getConfigurationSection(worldName);
            assert subConfig != null;

            Set<String> chunks = subConfig.getKeys(false);
            for (String chunkName : chunks) {
                List<Integer> list = subConfig.getIntegerList(chunkName);
                Chunk chunk = world.getChunkAt(list.get(0), list.get(1));
                chunk.setForceLoaded(true);
                chunk.load();
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Set<String> worlds = this.getConfig().getKeys(false);
        for (String worldName : worlds) {
            World world = this.getPlugin().getServer().getWorld(worldName);

            assert world != null;
            for (Chunk chunk : world.getForceLoadedChunks()) {
                chunk.setForceLoaded(false);
            }
        }
    }
}
