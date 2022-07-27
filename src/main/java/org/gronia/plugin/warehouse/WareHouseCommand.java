package org.gronia.plugin.warehouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.utils.pair.Pair2;

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

        if (args.length < 3 || (!command.equalsIgnoreCase("open") && !command.equalsIgnoreCase("take") && !command.equalsIgnoreCase("list"))) {
            return this.warnUser(player);
        }

        var name = args[2];
        if (command.equalsIgnoreCase("list")) {
            if (args.length != 3) {
                return this.warnUser(player);
            }

            return this.handleListCommand(player, name.equalsIgnoreCase("free"));
        }

        if (command.equalsIgnoreCase("open")) {
            if (args.length != 3) {
                return this.warnUser(player);
            }

            return this.handleOpenCommand(player, name);
        }

        if (command.equalsIgnoreCase("take")) {
            if (args.length != 4) {
                return this.warnUser(player);
            }

            var count = 0;
            try {
                count = Integer.parseInt(args[3]);
            } catch (NumberFormatException ignored) {
            }

            if (count <= 0) {
                return this.warnUser(player);
            }

            return this.handleTakeCommand(player, name, count);
        }

        return this.warnUser(player);
    }

    public boolean handleListCommand(Player player, boolean isFree) {
        // todo
        return true;
    }

    public boolean handleOpenCommand(Player player, String materialName) {
        if (!ItemRegistry.isValidMaterialName(materialName)) {
            return this.warnUser(player);
        }

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Warehouse");
        this.getPlugin().inventoryMap.put(inv, Pair2.of(player.getName(), materialName));
        player.openInventory(inv);

        return true;
    }

    public boolean handleTakeCommand(Player player, String materialName, int count) {
        if (!ItemRegistry.isValidMaterialName(materialName)) {
            return this.warnUser(player);
        }

        this.getPlugin().takeItem(player, materialName, count);

        // todo
        return true;
    }

    public boolean warnUser(HumanEntity player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "Warehouse " + ChatColor.RESET + "Type " + ChatColor.GREEN + "/help warehouse" + ChatColor.WHITE + " for help");
        return true;
    }
}
