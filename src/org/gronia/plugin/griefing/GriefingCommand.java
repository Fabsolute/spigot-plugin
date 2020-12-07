package org.gronia.plugin.griefing;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubCommandExecutor;

import java.util.ArrayList;
import java.util.List;

public class GriefingCommand extends SubCommandExecutor<GriefingPlugin> {
    public GriefingCommand(GriefingPlugin plugin) {
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

        if (command.equalsIgnoreCase("list")) {
            return this.handleList(player);
        }

        if (args.length < 2) {
            return this.warnUser(player);
        }

        if (command.equalsIgnoreCase("remove")) {
            return this.handleRemove(player, args[1].toLowerCase());
        }

        if (command.equalsIgnoreCase("add")) {
            return this.handleAdd(player, args[1].toLowerCase());
        }

        return this.warnUser(player);
    }

    public boolean handleList(Player player) {
        List<String> allowed = this.getPlugin().getConfig().getStringList("allowed");
        if (allowed.size() == 0) {
            player.sendMessage("[Grief] " + ChatColor.RED + "There is no griefing setting to list.");
            return true;
        }

        for (String name : allowed) {
            player.sendMessage("[Grief] " + ChatColor.GREEN + name);
        }
        return true;
    }

    public boolean handleAdd(Player player, String name) {
        List<String> allowed = this.getPlugin().getConfig().getStringList("allowed");
        if (allowed.size() == 0) {
            allowed = new ArrayList<>();
        }

        if (allowed.contains(name)) {
            player.sendMessage("[Grief] " + ChatColor.RED + "This grief setting is already exists.");
            return true;
        }

        if(!this.getPlugin().entityTypeSettingLookup.containsValue(name)){
            player.sendMessage("[Grief] " + ChatColor.RED + "This grief setting is not available.");
            return true;
        }

        allowed.add(name);

        player.sendMessage("[Grief] " + ChatColor.GREEN + "Grief setting is activated.");


        this.getPlugin().getConfig().set("allowed", allowed);
        this.save();

        return true;
    }

    public boolean handleRemove(Player player, String name) {
        List<String> allowed = this.getPlugin().getConfig().getStringList("allowed");

        if (!allowed.contains(name)) {
            player.sendMessage("[Grief] " + ChatColor.RED + "There is no grief setting to remove.");
            return true;
        }

        allowed.remove(name);

        this.getPlugin().getConfig().set("allowed", allowed);

        this.save();

        player.sendMessage("[Grief] " + ChatColor.GREEN + "Grief setting is deactivated.");
        return true;
    }

    private boolean warnUser(Player player) {
        player.sendMessage("[Grief] " + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/help grief" + ChatColor.WHITE + " for help");
        return true;
    }

    private void save() {
        this.getPlugin().saveConfig();
    }
}
