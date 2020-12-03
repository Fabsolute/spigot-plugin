package org.gronia.plugin.griefing;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class GriefingPlugin extends SubPlugin<GriefingPlugin> {
    public GriefingPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "griefing";
    }

    @Override
    public SubListener<GriefingPlugin> getListener() {
        return new GriefingListener(this);
    }

    @Override
    public SubCommandExecutor<GriefingPlugin> getExecutor() {
        return new GriefingCommand(this);
    }

    @Override
    public SubTabCompleter<GriefingPlugin> getTabCompleter() {
        return new GriefingTabCompleter(this);
    }
}
