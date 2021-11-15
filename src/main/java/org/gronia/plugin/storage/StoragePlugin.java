package org.gronia.plugin.storage;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;
import org.gronia.utils.GroniaMysqlConfiguration;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoragePlugin extends SubPlugin<StoragePlugin> {
    public final Map<Inventory, Map<String, Integer>> tempCounts = new HashMap<>();
    private final StorageAPI api;
    private GroniaMysqlConfiguration storageStackable;
    private GroniaMysqlConfiguration storageSerializable;

    public StoragePlugin(JavaPlugin plugin) {
        super(plugin);
        this.api = new StorageAPI(this);
    }

    @Override
    public String getName() {
        return "storage";
    }

    @Override
    public SubListener<StoragePlugin> getListener() {
        return new StorageListener(this);
    }

    @Override
    public SubCommandExecutor<StoragePlugin> getExecutor() {
        return new StorageCommand(this);
    }

    @Override
    public SubTabCompleter<StoragePlugin> getTabCompleter() {
        return new StorageTabCompleter(this);
    }

    public GroniaMysqlConfiguration getStackableConfig() {
        if (storageStackable == null) {
            this.storageStackable = GroniaMysqlConfiguration.loadConfiguration(GroniaMysqlConfiguration.Integer.class, "storage_stackable");
        }

        return this.storageStackable;
    }

    public GroniaMysqlConfiguration getSerializableConfig() {
        if (storageSerializable == null) {
            this.storageSerializable = GroniaMysqlConfiguration.loadConfiguration(GroniaMysqlConfiguration.JSON.class, "storage_serializable");
        }

        return this.storageSerializable;
    }

    public List<String> getSerializableItemList() {
        return this.getConfig().getStringList("serializable_items");
    }

    public List<String> getNotTakableItemList() {
        return this.getConfig().getStringList("not_takable_list");
    }

    public StorageAPI getAPI() {
        return this.api;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.getServer().getScheduler().runTaskTimerAsynchronously(this.getPlugin(), this::saveConfig, 1200, 1200);
    }

    @Override
    public void saveConfig() {
        try {
            if (this.storageSerializable != null) {
                this.storageSerializable.save();
            }

            if (this.storageStackable != null) {
                this.storageStackable.save();
            }
        } catch (SQLException ignored) {
        }

        super.saveConfig();
    }
}
