package org.gronia.plugin;

import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.utils.configuration.MysqlConfiguration;
import org.gronia.utils.configuration.YAMLMysqlConfiguration;

import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Logger;

public abstract class SubPlugin<T extends SubPlugin<T>> {
    private MysqlConfiguration configuration;

    public abstract String getName();

    public abstract SubListener<T> getListener();

    public abstract SubCommandExecutor<T> getExecutor();

    public abstract SubTabCompleter<T> getTabCompleter();

    private final JavaPlugin plugin;
    private final String password;

    public SubPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
        password = randomString(16);
    }

    public MysqlConfiguration getConfig() {
        if (this.configuration == null) {
            this.configuration = MysqlConfiguration.loadConfiguration(YAMLMysqlConfiguration.class, this.getName());
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

    public String getPassword() {
        return this.password;
    }

    private String randomString(int length) {
        int leftLimit = 97;
        int rightLimit = 122;
        return new Random().ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
