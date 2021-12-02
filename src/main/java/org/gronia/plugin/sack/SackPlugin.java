package org.gronia.plugin.sack;

import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubTabCompleter;
import org.gronia.plugin.SubUtilPlugin;
import org.gronia.utils.GroniaMysqlConfiguration;
import org.gronia.utils.PlayerMysqlConfiguration;

public class SackPlugin extends SubUtilPlugin<SackPlugin, SackUtil> {
    public int PER_COUNT = 512;

    private GroniaMysqlConfiguration configuration;

    public SackPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public SackUtil getUtils() {
        return new SackUtil(this);
    }

    @Override
    public String getName() {
        return "sack";
    }

    @Override
    public SubListener<SackPlugin> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<SackPlugin> getExecutor() {
        return new SackCommand(this);
    }

    @Override
    public SubTabCompleter<SackPlugin> getTabCompleter() {
        return null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this.getPlugin(), this::saveConfig, 80, 80);
    }

    @Override
    public GroniaMysqlConfiguration getConfig() {
        if (this.configuration == null) {
            this.configuration = GroniaMysqlConfiguration.loadConfiguration(PlayerMysqlConfiguration.class, this.getName());
        }

        return this.configuration;
    }

    public PlayerMysqlConfiguration.PlayerMemoryConfiguration createSackConfiguration(String name) {
        return ((PlayerMysqlConfiguration) this.getConfig()).createConfiguration(name);
    }

    public void executeFlushCommand(HumanEntity player, boolean isFree) {
        this.getServer().dispatchCommand(player, "sack flush " + this.getPassword() + (isFree ? " free" : " nope"));
    }
}
