package org.gronia.plugin;

public class SubUtil<T extends SubPlugin<T>> {
    private final T plugin;

    public SubUtil(T plugin) {
        this.plugin = plugin;
    }

    public T getPlugin() {
        return plugin;
    }
}
