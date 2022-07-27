package org.gronia.utils.configuration;

import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CaseMemoryConfiguration extends MemoryConfiguration {
    private final Set<String> dirtyList = new HashSet<>();
    public final Set<String> deletedList = new HashSet<>();

    private final InventoryMysqlConfiguration parent;

    public CaseMemoryConfiguration(InventoryMysqlConfiguration parent) {
        this.parent = parent;
    }

    @Override
    public void set(@NotNull String path, Object value) {
        super.set(path, value);
        if (parent != null) {
            parent.setDirty();
        }

        dirtyList.add(path);
        if (value == null) {
            deletedList.add(path);
        }
    }

    public CaseMemoryConfiguration createConfiguration(String name) {
        var config = new CaseMemoryConfiguration(this.parent);
        this.set(name, config);
        return config;
    }

    boolean isDirty(String path) {
        return this.dirtyList.contains(path);
    }

    void onSaveCompleted() {
        this.dirtyList.clear();
        this.deletedList.clear();

        for (var key : this.getKeys(false)) {
            var configuration = this.getConfigurationSection(key);
            if (!(configuration instanceof CaseMemoryConfiguration memoryConfiguration)) {
                continue;
            }

            memoryConfiguration.onSaveCompleted();
        }
    }
}
