package org.gronia.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public abstract class SubTabCompleter<T extends SubPlugin<T>> implements TabCompleter {
    private final T plugin;

    public SubTabCompleter(T plugin) {
        this.plugin = plugin;
    }

    public T getPlugin() {
        return this.plugin;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command cmd, String s, String[] args) {
        if (!cmd.getName().equalsIgnoreCase(this.getPlugin().getName())) {
            return null;
        }

        return this.onSubTabComplete(commandSender, cmd, s, args);
    }

    public abstract List<String> onSubTabComplete(CommandSender commandSender, Command cmd, String s, String[] args);
}
