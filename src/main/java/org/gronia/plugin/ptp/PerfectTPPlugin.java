package org.gronia.plugin.ptp;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;
import org.gronia.utils.configuration.DeletableYAMLMysqlConfiguration;
import org.gronia.utils.configuration.MysqlConfiguration;

public class PerfectTPPlugin extends SubPlugin<PerfectTPPlugin> {
    private DeletableYAMLMysqlConfiguration configuration;

    public PerfectTPPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "ptp";
    }

    @Override
    public SubListener<PerfectTPPlugin> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<PerfectTPPlugin> getExecutor() {
        return new PerfectTPCommand(this);
    }

    @Override
    public SubTabCompleter<PerfectTPPlugin> getTabCompleter() {
        return new PerfectTPTabCompleter(this);
    }

    @Override
    public DeletableYAMLMysqlConfiguration getConfig() {
        if (this.configuration == null) {
            this.configuration = MysqlConfiguration.loadConfiguration(DeletableYAMLMysqlConfiguration.class, this.getName());
        }

        return this.configuration;
    }
}
