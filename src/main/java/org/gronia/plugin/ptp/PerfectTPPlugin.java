package org.gronia.plugin.ptp;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;
import org.gronia.utils.DeletableYAMLConfiguration;
import org.gronia.utils.GroniaMysqlConfiguration;

public class PerfectTPPlugin extends SubPlugin<PerfectTPPlugin> {
    private DeletableYAMLConfiguration configuration;

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
    public DeletableYAMLConfiguration getConfig() {
        if (this.configuration == null) {
            this.configuration = GroniaMysqlConfiguration.loadConfiguration(DeletableYAMLConfiguration.class, this.getName());
        }

        return this.configuration;
    }
}
