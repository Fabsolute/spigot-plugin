package org.gronia.plugin;

import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public abstract class SubPlugin<T extends SubPlugin<T>> {
    private File file;
    private FileConfiguration configuration;

    public abstract String getName();

    public abstract SubListener<T> getListener();

    public abstract SubCommandExecutor<T> getExecutor();

    public abstract SubTabCompleter<T> getTabCompleter();

    private final JavaPlugin plugin;

    public SubPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ConfigurationSection getConfig() {
        if (this.file == null) {
            this.file = new File(this.getPlugin().getDataFolder(), this.getName() + ".yml");
            if (!this.file.exists()) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    this.file.createNewFile();
                } catch (IOException ignored) {
                }
            }

            this.configuration = YamlConfiguration.loadConfiguration(this.file);
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
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }

    public void saveConfig() {
        try {
            this.configuration.save(this.file);
        } catch (IOException ignored) {
        }
    }

    public <K extends SubPlugin<K>> K getSubPlugin(Class<K> clazz) {
        return ((Gronia) this.getPlugin()).getSubPlugin(clazz);
    }

    public Logger getLogger(){
        return this.getPlugin().getLogger();
    }
}
