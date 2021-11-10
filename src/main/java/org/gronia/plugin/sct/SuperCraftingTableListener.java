package org.gronia.plugin.sct;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.SubListener;


public class SuperCraftingTableListener extends SubListener<SuperCraftingTablePlugin> {
    Recipe recipe;

    public SuperCraftingTableListener(SuperCraftingTablePlugin plugin) {
        super(plugin);

        recipe = new Recipe(this.getPlugin());
        recipe.setRecipe(0, new ItemStack(Material.COBBLESTONE, 2));
        recipe.setRecipe(1, new ItemStack(Material.COBBLESTONE, 2));
        recipe.setRecipe(2, new ItemStack(Material.COBBLESTONE, 2));
        recipe.setRecipe(3, new ItemStack(Material.COBBLESTONE, 2));
        recipe.setRecipe(4, new ItemStack(Material.COBBLESTONE, 2));
        recipe.setRecipe(5, new ItemStack(Material.COBBLESTONE, 2));
        recipe.setRecipe(6, new ItemStack(Material.COBBLESTONE, 2));
        recipe.setRecipe(7, new ItemStack(Material.COBBLESTONE, 2));
        recipe.setRecipe(8, new ItemStack(Material.COBBLESTONE, 2));

        ItemStack enchantedCobbleStone = new ItemStack(Material.COBBLESTONE);
        enchantedCobbleStone.addUnsafeEnchantment(Enchantment.LURE, 1);
        ItemMeta enchantedCobbleStoneMeta = enchantedCobbleStone.getItemMeta();
        assert enchantedCobbleStoneMeta != null;
        enchantedCobbleStoneMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        enchantedCobbleStone.setItemMeta(enchantedCobbleStoneMeta);

        recipe.setOutput(enchantedCobbleStone);
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(this.getPlugin().CRAFTING_TABLE_NAME)) {
            return;
        }

        ItemStack current = event.getCurrentItem();
        if (current == null) {
            return;
        }

        if (this.getPlugin().isLockedItem(current)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryItemChanged(InventoryDragEvent event) {
        if (!this.getPlugin().craftingTableItem.equals(event.getInventory().getItem(0))) {
            return;
        }

        this.checkRecipe(event.getInventory(), null);
    }

    @EventHandler
    public void onInventoryClosed(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(this.getPlugin().CRAFTING_TABLE_NAME)) {
            return;
        }

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        this.getPlugin().applyInventory(event.getInventory(), (current, i, j) -> {
            if (current == null) {
                return;
            }

            player.getInventory().addItem(current);
        });
    }

    private void checkRecipe(Inventory inventory, Player player) {
        player.sendMessage("checkRecipe");
        if (recipe.peek(inventory)) {
            player.sendMessage("checkRecipe done");
        } else {
            player.sendMessage("checkRecipe nope");
        }
    }
}
