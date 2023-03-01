package org.gronia.plugin.warehouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gronia.items.ItemNames;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.utils.pair.Pair2;

import java.util.HashMap;
import java.util.Map;

public class WareHouseCommand extends SubCommandExecutor<WareHousePlugin> {

    public WareHouseCommand(WareHousePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length < 2) {
            return this.warnUser(player);
        }

        String password = args[0];
        if (!this.getPlugin().getPassword().equalsIgnoreCase(password)) {
            return this.warnUser(player);
        }

        String command = args[1];

        if (args.length < 3 || (!command.equalsIgnoreCase("open") && !command.equalsIgnoreCase("take") && !command.equalsIgnoreCase("list") && !command.equalsIgnoreCase("list-case"))) {
            return this.warnUser(player);
        }

        var name = args[2];
        if (command.equalsIgnoreCase("list")) {
            if (args.length != 3) {
                return this.warnUser(player);
            }

            return this.handleListCommand(player, name.equalsIgnoreCase("free"));
        }

        if (command.equalsIgnoreCase("list-case")) {
            if (args.length != 3) {
                return this.warnUser(player);
            }

            return this.handleListCaseCommand(player, name);
        }

        if (command.equalsIgnoreCase("open")) {
            if (args.length != 4) {
                return this.warnUser(player);
            }

            var itemName = args[3];

            return this.handleOpenCommand(player, name, itemName);
        }

        if (command.equalsIgnoreCase("take")) {
            if (args.length != 5) {
                return this.warnUser(player);
            }

            var itemName = args[3];

            var count = 0;
            try {
                count = Integer.parseInt(args[4]);
            } catch (NumberFormatException ignored) {
            }

            if (count <= 0) {
                return this.warnUser(player);
            }

            return this.handleTakeCommand(player, name, itemName, count);
        }

        return this.warnUser(player);
    }

    public boolean handleListCaseCommand(Player player, String caseName) {
        this.getPlugin().showCaseInventory(player, caseName);
        return true;
    }

    public boolean handleListCommand(Player player, boolean isFree) {
        if (!isFree) {
            Map<ItemStack, Integer> changes = new HashMap<>();
            Map<String, Integer> diffs = new HashMap<>();
            changes.put(ItemRegistry.createItem(ItemNames.TELEPORTER), -2);
            diffs.put(ItemNames.TELEPORTER, -2);

            this.getPlugin().applyStackable(player.getName(), WareHousePlugin.COMMON_CASE_NAME, changes, diffs);
        }

        this.getPlugin().showInventory(player);
        return true;
    }

    public boolean handleOpenCommand(Player player, String caseName, String materialName) {
        if (!ItemRegistry.isValidMaterialName(materialName)) {
            return this.warnUser(player);
        }

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Warehouse");
        this.getPlugin().inventoryMap.put(inv, Pair2.of(caseName, materialName));
        player.openInventory(inv);

        return true;
    }

    public boolean handleTakeCommand(Player player, String caseName, String materialName, int count) {
        if (!ItemRegistry.isValidMaterialName(materialName)) {
            return this.warnUser(player);
        }

        this.getPlugin().takeItem(player, caseName, materialName, count);

        // todo
        return true;
    }

    public boolean warnUser(HumanEntity player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "Warehouse " + ChatColor.RESET + "Type " + ChatColor.GREEN + "/help warehouse" + ChatColor.WHITE + " for help");
        return true;
    }
}
