package org.gronia.plugin.uei;

import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;

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
