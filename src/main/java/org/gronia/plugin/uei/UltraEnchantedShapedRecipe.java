package org.gronia.plugin.uei;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class UltraEnchantedShapedRecipe extends ShapedRecipe {
    private final Map<Character, ItemStack> ingredients = new HashMap<>();

    public UltraEnchantedShapedRecipe(NamespacedKey key, ItemStack result) {
        super(key, result);
    }

    public UltraEnchantedShapedRecipe setIngredient(char key, ItemStack ingredient) {
        super.setIngredient(key, ingredient.getType());
        this.ingredients.put(key, ingredient);
        return this;
    }

    public boolean match(CraftingInventory inventory) {
        var matrix = inventory.getMatrix();
        int i = 0;
        for (var line : this.getShape()) {
            int j = 0;
            for (var c : line.toCharArray()) {
                var item = matrix[i * 3 + j];
                if (this.ingredients.containsKey(c)){
                    if (!item.isSimilar(this.ingredients.get(c))) {
                        return false;
                    }
                }
                j++;
            }

            i++;
        }

        return true;
    }
}
