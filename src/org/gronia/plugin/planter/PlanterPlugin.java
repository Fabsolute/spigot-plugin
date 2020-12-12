package org.gronia.plugin.planter;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class PlanterPlugin extends SubPlugin<PlanterPlugin> {
    public PlanterPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "planter";
    }

    @Override
    public SubListener<PlanterPlugin> getListener() {
        return new PlanterListener(this);
    }

    @Override
    public SubCommandExecutor<PlanterPlugin> getExecutor() {
        return new PlanterCommand(this);
    }

    @Override
    public SubTabCompleter<PlanterPlugin> getTabCompleter() {
        return new PlanterTabCompleter(this);
    }
}
