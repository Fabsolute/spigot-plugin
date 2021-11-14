package org.gronia.disabled.dancer;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class DancerPlugin extends SubPlugin<DancerPlugin> {
    public DancerPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "dancer";
    }

    @Override
    public SubListener<DancerPlugin> getListener() {
        return new DancerListener(this);
    }

    @Override
    public SubCommandExecutor<DancerPlugin> getExecutor() {
        return new DancerCommand(this);
    }

    @Override
    public SubTabCompleter<DancerPlugin> getTabCompleter() {
        return new DancerTabCompleter(this);
    }
}
