package org.gronia.plugin.uei;

import org.bukkit.Material;

import java.sql.Array;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomShapelessRecipe implements CustomRecipe {
    private final String result;
    private String[] alteredIngredients;
    private int count;

    public CustomShapelessRecipe(String result) {
        this.result = result;
    }

    public CustomShapelessRecipe addIngredient(int count, Material... alteredIngredients) {
        var output = new String[alteredIngredients.length];
        for (var i = 0; i < alteredIngredients.length; i++) {
            output[i] = alteredIngredients[i].name().toLowerCase();
        }
        return this.addIngredient(count, output);
    }

    public CustomShapelessRecipe addIngredient(int count, String... alteredIngredients) {
        this.alteredIngredients = alteredIngredients;
        this.count = count;
        return this;
    }

    public String[] getAlteredIngredients() {
        return this.alteredIngredients;
    }

    public int getCount() {
        return this.count;
    }

    public String getResult() {
        return result;
    }
}
