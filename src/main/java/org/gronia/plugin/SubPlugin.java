package org.gronia.plugin;

import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.utils.GroniaMysqlConfiguration;

import java.sql.SQLException;
import java.util.logging.Logger;

public abstract class SubPlugin<T extends SubPlugin<T>> {
    private GroniaMysqlConfiguration configuration;

    public abstract String getName();

    public abstract SubListener<T> getListener();

    public abstract SubCommandExecutor<T> getExecutor();

    public abstract SubTabCompleter<T> getTabCompleter();

    private final JavaPlugin plugin;

    public SubPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public GroniaMysqlConfiguration getConfig() {
        if (this.configuration == null) {
            this.configuration = GroniaMysqlConfiguration.loadConfiguration(GroniaMysqlConfiguration.YAML.class, this.getName());
        }

        return this.configuration;
    }

    public void onEnable() {
        this.getPlugin().getConfig().addDefault(this.getName(), new MemoryConfiguration());
        Listener listener = this.getListener();
        CommandExecutor executor = this.getExecutor();
        TabCompleter completer = this.getTabCompleter();


        if (listener != null) {
            this.getPlugin().getServer().getPluginManager().registerEvents(listener, this.getPlugin());
        }

        if (executor != null) {
            this.getPlugin().getCommand(this.getName()).setExecutor(executor);
        }

        if (completer != null) {
            this.getPlugin().getCommand(this.getName()).setTabCompleter(completer);
        }
    }

    public void onDisable() {
        if (this.configuration != null) {
            this.saveConfig();
        }
    }

    public <K extends JavaPlugin> K getPlugin() {
        return (K) this.plugin;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }

    public void saveConfig() {
        try {
            this.getConfig().save();
        } catch (SQLException ignored) {
        }
    }

    public <K extends SubPlugin<K>> K getSubPlugin(Class<K> clazz) {
        return ((Gronia) this.getPlugin()).getSubPlugin(clazz);
    }

    public Logger getLogger() {
        return this.getPlugin().getLogger();
    }
}
