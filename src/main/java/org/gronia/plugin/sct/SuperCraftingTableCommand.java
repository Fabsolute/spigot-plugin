package org.gronia.plugin.sct;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.SubCommandExecutor;

public class SuperCraftingTableCommand extends SubCommandExecutor<SuperCraftingTablePlugin> {
    public SuperCraftingTableCommand(SuperCraftingTablePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        Inventory craftInventory = this.getPlugin().getServer().createInventory(player, 54, this.getPlugin().CRAFTING_TABLE_NAME);

        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 9; i++) {
                if (!(i >= 2 && i <= 4 && j >= 1 && j <= 3)) {
                    craftInventory.setItem(
                            j * 9 + i,
                            getItem(i, j)
                    );
                }
            }
        }


        player.openInventory(craftInventory);
        return true;
    }

    ItemStack getItem(int i, int j) {
        if (i == 0 && j == 0) {
            return this.getPlugin().craftingTableItem;
        }

        if (i == 6 && j == 2) {
            return this.getPlugin().emptyResultAreaItem;
        }

        if (j == 5) {
            return this.getPlugin().redAreaItem;
        }

        return this.getPlugin().lockedAreaItem;
    }
}
