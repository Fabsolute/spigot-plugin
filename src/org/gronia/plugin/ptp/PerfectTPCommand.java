package org.gronia.plugin.ptp;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.NumberMap;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.storage.StorageAPI;
import org.gronia.plugin.storage.StoragePlugin;

import java.util.*;

public class PerfectTPCommand extends SubCommandExecutor<PerfectTPPlugin> {
    public PerfectTPCommand(PerfectTPPlugin plugin) {
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

        if (command.equalsIgnoreCase("take")) {
            return this.handleTake(player, args[1].toLowerCase());
        }

        return this.warnUser(player);
    }

    public boolean handleList(Player player) {
        ConfigurationSection config = this.getPlugin().getConfig();
        if (config == null) {
            player.sendMessage("[Perfect TP] " + ChatColor.RED + "There is no named tp point to list.");
            return true;
        }

        Set<String> keys = config.getKeys(false);
        for (String name : keys) {
            Location detail = config.getLocation(name);
            assert detail != null;

            player.sendMessage("[Perfect TP] " + ChatColor.GREEN + "[" + detail.getWorld().getName() + "] " + name + ChatColor.AQUA + " [" + detail.getX() + ", " + detail.getY() + ", " + "]");
        }
        return true;
    }

    public boolean handleAdd(Player player, String name) {
        ConfigurationSection config = this.getPlugin().getConfig();

        if (config.get(name, null) != null) {
            player.sendMessage("[Perfect TP] " + ChatColor.RED + "This name is already exists.");
            return true;
        }

        config.set(name, player.getLocation());

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

    public boolean handleTake(Player player, String name) {
        ConfigurationSection config = this.getPlugin().getConfig();

        Player otherPlayer = this.getPlugin().getServer().getPlayer(name);
        boolean found = otherPlayer != null;

        if (config != null) {
            if (config.get(name, null) != null) {
                found = true;
            }
        }

        if (!found) {
            player.sendMessage("[Perfect TP] " + ChatColor.RED + "There is no named tp point for take.");
            return true;
        }

        if (player.getLevel() < 1) {
            player.sendMessage("[Perfect TP] " + ChatColor.RED + "You should have at least one level.");
            return true;
        }

        ItemStack stack = new ItemStack(Material.CHORUS_FRUIT, 64);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("[TP] " + name);
        stack.setItemMeta(meta);

        StorageAPI api = this.getPlugin().getSubPlugin(StoragePlugin.class).getAPI();

        api.addItemToPlayer(player, stack);
        Map<String, Integer> changes = new HashMap<>();
        changes.put("chorus_fruit", -64);
        api.applyStackable(player.getName(), changes);

        player.setLevel(player.getLevel() - 1);

        return true;
    }

    private boolean warnUser(Player player) {
        player.sendMessage("[Perfect TP] " + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/help ptp" + ChatColor.WHITE + " for help");
        return true;
    }

    private void save() {
        this.getPlugin().saveConfig();
    }
}
