package org.gronia.disabled.swap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubCommandExecutor;

public class SwapCommand extends SubCommandExecutor<SwapPlugin> {
    public SwapCommand(SwapPlugin plugin) {
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

        String command = args[0];

        if (command.equalsIgnoreCase("register")) {
            if (this.getPlugin().players.contains(player)) {
                return true;
            }

            this.getPlugin().players.add(player);
            return true;
        }

        if (command.equalsIgnoreCase("deregister")) {
            if (!this.getPlugin().players.contains(player)) {
                return true;
            }

            this.getPlugin().players.remove(player);
            return true;
        }

        if (command.equalsIgnoreCase("start") && !this.getPlugin().started) {
            this.getPlugin().task = new SwapTask(this.getPlugin());
            this.getPlugin().task.runTaskTimer(this.getPlugin().getPlugin(), 100, 6000);
            this.getPlugin().started = true;
        }

        if (command.equalsIgnoreCase("end") && this.getPlugin().started) {
            this.getPlugin().task.cancel();
            this.getPlugin().started = false;
        }

        return false;
    }

    public boolean warnUser(Player player) {
        player.sendMessage("[Task] " + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/help task" + ChatColor.WHITE + " for help");
        return true;
    }
}
