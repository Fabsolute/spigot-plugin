package org.gronia.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class SubCommandExecutor<T extends SubPlugin<T>> implements CommandExecutor {
    private final T plugin;

    public SubCommandExecutor(T plugin) {
        this.plugin = plugin;
    }

    public T getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!cmd.getName().equalsIgnoreCase(this.plugin.getName())) {
            return true;
        }

        return this.onSubCommand(sender, cmd, s, args);
    }

    public abstract boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args);
}
