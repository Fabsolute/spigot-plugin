package org.gronia.disabled.sct;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

public class SuperCraftingTablePlugin extends SubPlugin<SuperCraftingTablePlugin> {
    public final String CRAFTING_TABLE_NAME = "[Super Crafting Table]";
    public final ItemStack lockedAreaItem;
    public final ItemStack redAreaItem;
    public final ItemStack greenAreaItem;
    public final ItemStack emptyResultAreaItem;
    public final ItemStack craftingTableItem;

    public SuperCraftingTablePlugin(JavaPlugin plugin) {
        super(plugin);

        lockedAreaItem = this.createEmptyItem(Material.BLACK_STAINED_GLASS_PANE);
        redAreaItem = this.createEmptyItem(Material.RED_STAINED_GLASS_PANE);
        greenAreaItem = this.createEmptyItem(Material.GREEN_STAINED_GLASS_PANE);
        emptyResultAreaItem = this.createEmptyItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        craftingTableItem = this.createEmptyItem(Material.CRAFTING_TABLE);
    }

    @Override
    public String getName() {
        return "sct";
    }

    @Override
    public SubListener<SuperCraftingTablePlugin> getListener() {
        return new SuperCraftingTableListener(this);
    }

    @Override
    public SubCommandExecutor<SuperCraftingTablePlugin> getExecutor() {
        return new SuperCraftingTableCommand(this);
    }

    @Override
    public SubTabCompleter<SuperCraftingTablePlugin> getTabCompleter() {
        return null;
    }

    public boolean isLockedItem(ItemStack stack) {
        return stack.equals(lockedAreaItem) ||
                stack.equals(redAreaItem) ||
                stack.equals(greenAreaItem) ||
                stack.equals(emptyResultAreaItem) ||
                stack.equals(craftingTableItem);
    }

    interface InventoryApplierInterface {
        void apply(ItemStack stack, int i, int j);
    }

    public void applyInventory(Inventory inventory, InventoryApplierInterface fn) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ItemStack current = inventory.getItem((i + 1) * 9 + j + 2);
                fn.apply(current, i, j);
            }
        }
    }

    private ItemStack createEmptyItem(Material material) {
        ItemStack output = new ItemStack(material);
        ItemMeta meta = output.getItemMeta();
        assert meta != null;
        meta.setDisplayName(" ");
        output.setItemMeta(meta);

        return output;
    }
}
