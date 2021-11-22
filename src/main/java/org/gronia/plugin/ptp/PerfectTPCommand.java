package org.gronia.plugin.ptp;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.items.ItemNames;
import org.gronia.plugin.storage.StoragePlugin;

import java.util.*;

public class PerfectTPCommand extends SubCommandExecutor<PerfectTPPlugin> {
    public PerfectTPCommand(PerfectTPPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            return this.warnUser(player);
        }

        String command = args[0];

        if (command.equalsIgnoreCase("list")) {
            return this.handleList(player);
        }

        if (command.equalsIgnoreCase("home")) {
            return this.handleHome(player);
        }

        if (command.equalsIgnoreCase("take")) {
            return this.handleTake(player);
        }

        if (args.length < 2) {
            return this.warnUser(player);
        }

        if (command.equalsIgnoreCase("remove")) {
            return this.handleRemove(player, args[1].toLowerCase());
        }

        if (command.equalsIgnoreCase("add")) {
            return this.handleAdd(player, args[1].toLowerCase(), true);
        }

        if (args.length < 3) {
            return this.warnUser(player);
        }

        if (command.equalsIgnoreCase("icon")) {
            return this.handleIcon(player, args[1].toLowerCase(), args[2].toLowerCase());
        }

        return this.warnUser(player);
    }

    public boolean handleIcon(Player player, String name, String iconName) {
        ConfigurationSection config = this.getPlugin().getConfig();
        boolean found = false;

        if (config != null) {
            if (config.get(name, null) != null) {
                found = true;
            }
        }

        if (!found) {
            player.sendMessage("[Perfect TP] " + ChatColor.RED + "There is no named tp point.");
            return true;
        }

        try {
            Material.valueOf(iconName.toUpperCase());
        } catch (Exception ignored) {
            return this.warnUser(player);
        }

        ConfigurationSection section = config.getConfigurationSection(name);
        assert section != null;
        section.set("icon", iconName);

        this.save();

        return false;
    }

    public boolean handleList(Player player) {
        ConfigurationSection config = this.getPlugin().getConfig();
        if (config == null) {
            player.sendMessage("[Perfect TP] " + ChatColor.RED + "There is no named tp point to list.");
            return true;
        }

        Set<String> keys = config.getKeys(false);
        for (String name : keys) {
            if (name.startsWith("custom_home_")) {
                continue;
            }

            Location detail = config.getConfigurationSection(name).getLocation("location");
            assert detail != null;

            player.sendMessage("[Perfect TP] " + ChatColor.GREEN + "[" + detail.getWorld().getName() + "] " + name + ChatColor.AQUA + " [" + detail.getX() + ", " + detail.getY() + ", " + "]");
        }
        return true;
    }

    public boolean handleAdd(Player player, String name, boolean checkExistence) {
        ConfigurationSection config = this.getPlugin().getConfig();

        if (checkExistence && config.get(name, null) != null) {
            player.sendMessage("[Perfect TP] " + ChatColor.RED + "This name is already exists.");
            return true;
        }

        MemoryConfiguration configuration = new MemoryConfiguration();
        configuration.set("location", player.getLocation());
        config.set(name, configuration);

        player.sendMessage("[Perfect TP] " + ChatColor.GREEN + "Named TP point is activated.");
        this.save();

        return true;
    }

    public boolean handleRemove(Player player, String name) {
        ConfigurationSection config = this.getPlugin().getConfig();
        boolean found = false;

        if (config != null) {
            if (config.get(name, null) != null) {
                found = true;
            }
        }

        if (!found) {
            player.sendMessage("[Perfect TP] " + ChatColor.RED + "There is no named tp point to remove.");
            return true;
        }

        config.set(name, null);

        this.save();

        player.sendMessage("[Perfect TP] " + ChatColor.GREEN + "Named TP point is deactivated.");
        return true;
    }

    public boolean handleHome(Player player) {
        return this.handleAdd(player, "custom_home_" + player.getName(), false);
    }

    public boolean handleTake(Player player) {
        this.getPlugin().getSubPlugin(StoragePlugin.class).executeTakeCommand(player, ItemNames.TELEPORTER, 64);
        return true;
    }

    private boolean warnUser(Player player) {
        player.sendMessage("[Perfect TP] " + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/help ptp" + ChatColor.WHITE + " for help");
        return true;
    }

    private void save() {
        this.getPlugin().getConfig().setDirty();
        this.getPlugin().saveConfig();
    }
}
