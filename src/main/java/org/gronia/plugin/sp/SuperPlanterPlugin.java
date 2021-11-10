package org.gronia.plugin.sp;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;

public class SuperPlanterPlugin extends SubUtilPlugin<SuperPlanterPlugin, SuperPlanterUtil> {
    public SuperPlanterPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "sp";
    }

    @Override
    public SubListener<SuperPlanterPlugin> getListener() {
        return new SuperPlanterListener(this);
    }

    @Override
    public SubCommandExecutor<SuperPlanterPlugin> getExecutor() {
        return null;
    }

    @Override
    public SubTabCompleter<SuperPlanterPlugin> getTabCompleter() {
        return null;
    }

    @Override
    public SuperPlanterUtil getUtils() {
        return new SuperPlanterUtil(this);
    }
}
