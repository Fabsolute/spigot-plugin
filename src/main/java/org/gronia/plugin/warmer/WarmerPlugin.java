package org.gronia.plugin.warmer;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class WarmerPlugin extends SubPlugin<WarmerPlugin> {
    public WarmerPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "warmer";
    }

    @Override
    public SubListener<WarmerPlugin> getListener() {
        return new WarmerListener(this);
    }

    @Override
    public SubCommandExecutor<WarmerPlugin> getExecutor() {
        return null;
    }

    @Override
    public SubTabCompleter<WarmerPlugin> getTabCompleter() {
        return null;
    }
}
