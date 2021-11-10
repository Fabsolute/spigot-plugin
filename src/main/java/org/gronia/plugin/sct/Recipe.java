package org.gronia.plugin.sct;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class Recipe {
    private final ItemStack[] itemStacks;
    private final SuperCraftingTablePlugin plugin;
    private ItemStack output;

    public Recipe(SuperCraftingTablePlugin plugin) {
        this.itemStacks = new ItemStack[9];
        this.plugin = plugin;
    }

    public void setRecipe(int i, ItemStack stack) {
        this.itemStacks[i] = stack;
    }

    public void setOutput(ItemStack output) {
        this.output = output;
    }

    public boolean peek(Inventory inventory) {
        AtomicBoolean canCraft = new AtomicBoolean(true);
        this.plugin.applyInventory(inventory, (current, i, j) -> {
            int index = i * 3 + j;
            if (!current.equals(itemStacks[index])) {
                canCraft.set(false);
            }
        });

        if (!canCraft.get()) {
            return false;
        }

        inventory.setItem(24, output);

        return true;
    }
}
