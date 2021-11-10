package org.gronia.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class SubUtilPlugin<T extends SubUtilPlugin<T, ?>, K extends SubUtil<T>> extends SubPlugin<T> {
    public SubUtilPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    public abstract K getUtils();
}
