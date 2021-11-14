package org.gronia.plugin.hf;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;

public class HyperFurnacePlugin extends SubPlugin<HyperFurnacePlugin> {
    public HyperFurnacePlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "hf";
    }

    @Override
    public SubListener<HyperFurnacePlugin> getListener() {
        return new HyperFurnaceListener(this);
    }

    @Override
    public SubCommandExecutor<HyperFurnacePlugin> getExecutor() {
        return null;
    }

    @Override
    public SubTabCompleter<HyperFurnacePlugin> getTabCompleter() {
        return null;
    }
}
