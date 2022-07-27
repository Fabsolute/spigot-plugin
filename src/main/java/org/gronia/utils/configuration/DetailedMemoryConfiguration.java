package org.gronia.utils.configuration;

import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class DetailedMemoryConfiguration extends MemoryConfiguration {
    private final Set<String> dirtyList = new HashSet<>();
    public final Set<String> deletedList = new HashSet<>();

    private final DetailedMysqlConfiguration parent;

    public DetailedMemoryConfiguration(DetailedMysqlConfiguration parent) {
        this.parent = parent;
    }

    @Override
    public void set(@NotNull String path, Object value) {
        super.set(path, value);
        parent.setDirty();
        dirtyList.add(path);
        if (value == null) {
            deletedList.add(path);
        }
    }

    boolean isDirty(String path) {
        return this.dirtyList.contains(path);
    }

    void onSaveCompleted() {
        this.dirtyList.clear();
        this.deletedList.clear();
    }
}