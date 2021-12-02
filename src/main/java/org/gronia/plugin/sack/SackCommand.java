package org.gronia.plugin.sack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubCommandExecutor;

public class SackCommand extends SubCommandExecutor<SackPlugin> {
    public SackCommand(SackPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        String command = args[0];

        if (!command.equalsIgnoreCase("flush")) {
            return false;
        }

        if (!args[1].equalsIgnoreCase(this.getPlugin().getPassword())) {
            return false;
        }

        this.getPlugin().getUtils().flushSack(player, args[2].equalsIgnoreCase("free"));
        return true;
    }
}
