package org.gronia.plugin.repair;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class RepairPlugin extends SubPlugin<RepairPlugin> {
    public RepairPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "repair";
    }

    @Override
    public SubListener<RepairPlugin> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<RepairPlugin> getExecutor() {
        return new RepairCommand(this);
    }

    @Override
    public SubTabCompleter<RepairPlugin> getTabCompleter() {
        return null;
    }
}
