package org.gronia.plugin.ride;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubCommandExecutor;

public class RideCommand extends SubCommandExecutor<RidePlugin> {
    public RideCommand(RidePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            return this.warnUser(player);
        }

        var other = Bukkit.getPlayer(args[0]);
        if (other == null) {
            return this.warnUser(player);
        }

        other.addPassenger(player);
        return false;
    }

    public boolean warnUser(Player player) {
        player.sendMessage("[Ride] " + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/help task" + ChatColor.WHITE + " for help");
        return true;
    }
}
