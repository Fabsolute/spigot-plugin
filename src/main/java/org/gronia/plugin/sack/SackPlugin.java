package org.gronia.plugin.sack;

import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubTabCompleter;
import org.gronia.plugin.SubUtilPlugin;
import org.gronia.utils.configuration.MysqlConfiguration;
import org.gronia.utils.configuration.PlayerMemoryConfiguration;
import org.gronia.utils.configuration.PlayerMysqlConfiguration;

import java.sql.SQLException;

public class SackPlugin extends SubUtilPlugin<SackPlugin, SackUtil> {
    public int PER_COUNT = 512;

    private PlayerMysqlConfiguration configuration;
    private PlayerMysqlConfiguration lockConfiguration;

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
    public PlayerMysqlConfiguration getConfig() {
        if (this.configuration == null) {
            this.configuration = MysqlConfiguration.loadConfiguration(PlayerMysqlConfiguration.class, this.getName());
        }

        return this.configuration;
    }

    public PlayerMysqlConfiguration getLockConfig() {
        if (this.lockConfiguration == null) {
            this.lockConfiguration = MysqlConfiguration.loadConfiguration(PlayerMysqlConfiguration.class, this.getName() + "_lock", PlayerMysqlConfiguration.Type.BOOLEAN);
        }

        return this.lockConfiguration;
    }

    public PlayerMemoryConfiguration createSackConfiguration(String name) {
        return this.getConfig().createConfiguration(name);
    }

    public void executeFlushCommand(HumanEntity player, boolean isFree) {
        this.getServer().dispatchCommand(player, "sack flush " + this.getPassword() + (isFree ? " free" : " nope"));
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        if (this.lockConfiguration != null) {
            try {
                this.getLockConfig().save();
            } catch (SQLException ignored) {
            }
        }
    }
}
