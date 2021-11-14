package org.gronia.plugin.uei;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.gronia.plugin.ItemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UltraEnchantedShapedRecipe extends ShapedRecipe {
    private final Map<Character, String> ingredients = new HashMap<>();

    public UltraEnchantedShapedRecipe(NamespacedKey key, ItemStack result) {
        super(key, result);
    }

    public UltraEnchantedShapedRecipe setIngredient(char key, String ingredient) {
        super.setIngredient(key, Objects.requireNonNull(ItemUtils.getMaterialFor(ingredient)));
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
                    if (!ItemUtils.getInternalName(item).equalsIgnoreCase(this.ingredients.get(c))) {
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
