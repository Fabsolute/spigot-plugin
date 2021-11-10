package org.gronia.plugin.disenchant;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class DisenchantPlugin extends SubPlugin<DisenchantPlugin> {
    public DisenchantPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "disenchant";
    }

    @Override
    public SubListener<DisenchantPlugin> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<DisenchantPlugin> getExecutor() {
        return new DisenchantCommand(this);
    }

    @Override
    public SubTabCompleter<DisenchantPlugin> getTabCompleter() {
        return null;
    }
}
