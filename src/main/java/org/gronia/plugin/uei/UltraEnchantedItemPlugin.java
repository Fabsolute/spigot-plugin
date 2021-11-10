package org.gronia.plugin.uei;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class UltraEnchantedItemPlugin extends SubPlugin<UltraEnchantedItemPlugin> {
    public UltraEnchantedItemPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "uei";
    }

    @Override
    public SubListener<UltraEnchantedItemPlugin> getListener() {
        return new UltraEnchantedItemListener(this);
    }

    @Override
    public SubCommandExecutor<UltraEnchantedItemPlugin> getExecutor() {
        return null;
    }

    @Override
    public SubTabCompleter<UltraEnchantedItemPlugin> getTabCompleter() {
        return null;
    }
}
