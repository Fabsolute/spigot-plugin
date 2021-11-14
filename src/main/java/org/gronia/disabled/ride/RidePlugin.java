package org.gronia.disabled.ride;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class RidePlugin extends SubPlugin<RidePlugin> {

    public RidePlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "ride";
    }

    @Override
    public SubListener<RidePlugin> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<RidePlugin> getExecutor() {
        return new RideCommand(this);
    }

    @Override
    public SubTabCompleter<RidePlugin> getTabCompleter() {
        return null;
    }
}
