package org.gronia.plugin.planter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubCommandExecutor;

public class PlanterCommand extends SubCommandExecutor<PlanterPlugin> {
    public PlanterCommand(PlanterPlugin plugin) {
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

        if (command.equalsIgnoreCase("enable")) {
            return this.handleStatus(true);
        }

        if (command.equalsIgnoreCase("disable")) {
            return this.handleStatus(false);
        }

        return this.warnUser(player);
    }

    public boolean handleStatus(boolean enabled) {
        ConfigurationSection config = this.getPlugin().getConfig();
        config.set("enabled", enabled);
        this.save();
        return true;
    }

    private boolean warnUser(Player player) {
        player.sendMessage("[Planter] " + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/help planter" + ChatColor.WHITE + " for help");
        return true;
    }

    private void save() {
        this.getPlugin().saveConfig();
    }
}
