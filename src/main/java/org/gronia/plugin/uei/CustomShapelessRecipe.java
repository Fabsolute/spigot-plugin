package org.gronia.plugin.uei;

import org.bukkit.Material;

public class CustomShapelessRecipe implements CustomRecipe {
    private final String result;
    private String ingredient;
    private int count;

    public CustomShapelessRecipe(String result) {
        this.result = result;
    }

    public CustomShapelessRecipe addIngredient(int count, Material ingredient) {
        return this.addIngredient(count, ingredient.name().toLowerCase());
    }

    public CustomShapelessRecipe addIngredient(int count, String ingredient) {
        this.ingredient = ingredient;
        this.count = count;
        return this;
    }

    public String getIngredient() {
        return this.ingredient;
    }

    public int getCount() {
        return this.count;
    }

    public String getResult() {
        return result;
    }
}
