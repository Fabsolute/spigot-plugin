package org.gronia.plugin;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.disenchant.DisenchantPlugin;
import org.gronia.plugin.pouch.PouchPlugin;
import org.gronia.plugin.ptp.PerfectTPPlugin;
import org.gronia.plugin.ride.RidePlugin;
import org.gronia.plugin.sp.SuperPlanterPlugin;
import org.gronia.plugin.storage.StoragePlugin;
import org.gronia.plugin.uei.UltraEnchantedItemPlugin;
import org.gronia.plugin.ti.TeleportItemPlugin;
import org.gronia.utils.GroniaMysqlConfiguration;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Gronia extends JavaPlugin {
    private final Map<String, NamespacedKey> keys = new HashMap<>();

    private final SubPlugin<?>[] plugins = new SubPlugin[]{
            new TeleportItemPlugin(this),
            new PouchPlugin(this),
            new UltraEnchantedItemPlugin(this),
            new PerfectTPPlugin(this),
            new StoragePlugin(this)
    };

    @Override
    public void onEnable() {
        var config = this.getConfig();
        var url = config.getString("mysql_connection");
        try {
            assert url != null;
            GroniaMysqlConfiguration.initialize(DriverManager.getConnection(url));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (SubPlugin<?> plugin : this.plugins) {
            plugin.onEnable();
        }
    }

    @Override
    public void onDisable() {
        for (SubPlugin<?> plugin : this.plugins) {
            plugin.onDisable();
        }
    }

    public <T extends SubPlugin<T>> T getSubPlugin(Class<T> clazz) {
        for (SubPlugin<?> plugin : this.plugins) {
            if (clazz.isInstance(plugin)) {
                return (T) plugin;
            }
        }

        return null;
    }

    public NamespacedKey getKey(String name) {
        if (!keys.containsKey(name)) {
            keys.put(name, new NamespacedKey(Gronia.this, name));
        }

        return keys.get(name);
    }
}
