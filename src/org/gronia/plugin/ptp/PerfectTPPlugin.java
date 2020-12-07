package org.gronia.plugin.ptp;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class PerfectTPPlugin extends SubPlugin<PerfectTPPlugin> {
    public PerfectTPPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "ptp";
    }

    @Override
    public SubListener<PerfectTPPlugin> getListener() {
        return new PerfectTPListener(this);
    }

    @Override
    public SubCommandExecutor<PerfectTPPlugin> getExecutor() {
        return new PerfectTPCommand(this);
    }

    @Override
    public SubTabCompleter<PerfectTPPlugin> getTabCompleter() {
        return new PerfectTPTabCompleter(this);
    }
}
