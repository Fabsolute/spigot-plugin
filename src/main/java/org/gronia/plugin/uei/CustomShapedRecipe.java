package org.gronia.plugin.uei;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.gronia.plugin.ItemRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomShapedRecipe extends ShapedRecipe implements CustomRecipe {
    private final Map<Character, String> ingredients = new HashMap<>();

    public CustomShapedRecipe(NamespacedKey key, ItemStack result) {
        super(key, result);
    }

    public CustomShapedRecipe setIngredient(char key, String ingredient) {
        super.setIngredient(key, Objects.requireNonNull(ItemRegistry.getMaterialFor(ingredient)));
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
                if (this.ingredients.containsKey(c)) {
                    var name = ItemRegistry.getInternalName(item);
                    if (name == null || !name.equalsIgnoreCase(this.ingredients.get(c))) {
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
