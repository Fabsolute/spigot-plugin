package org.gronia.plugin.storage;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
    public final Map<Material, Boolean> materials = new HashMap<>();
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
            this.storageStackable = GroniaMysqlConfiguration.loadConfiguration("storage_stackable");
        }

        return this.storageStackable;
    }

    public GroniaMysqlConfiguration getSerializableConfig() {
        if (storageSerializable == null) {
            this.storageSerializable = GroniaMysqlConfiguration.loadConfiguration("storage_serializable");
        }

        return this.storageSerializable;
    }

    public List<String> getSerializableItemList() {
        return this.getConfig().getStringList("serializable_items");
    }

    public StorageAPI getAPI() {
        return this.api;
    }

    @Override
    public void saveConfig() {
        super.getConfig().setDirty();
        this.getStackableConfig().setDirty();
        this.getSerializableConfig().setDirty();

        try {
            this.storageStackable.save();
            this.storageSerializable.save();
        } catch (SQLException ignored) {
        }

        super.saveConfig();
    }
}
