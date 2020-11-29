package org.gronia.plugin;

import org.bukkit.event.Listener;

public class SubListener<T extends SubPlugin<T>> implements Listener {
    private final T plugin;

    public SubListener(T plugin) {
        this.plugin = plugin;
    }

    public T getPlugin() {
        return this.plugin;
    }
}
