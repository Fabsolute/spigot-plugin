package org.gronia.plugin.ti;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class TeleportItemPlugin extends SubPlugin<TeleportItemPlugin> {
    public TeleportItemPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "ti";
    }

    @Override
    public SubListener<TeleportItemPlugin> getListener() {
        return new TeleportItemListener(this);
    }

    @Override
    public SubCommandExecutor<TeleportItemPlugin> getExecutor() {
        return null;
    }

    @Override
    public SubTabCompleter<TeleportItemPlugin> getTabCompleter() {
        return null;
    }
}
