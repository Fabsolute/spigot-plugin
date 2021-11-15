package org.gronia.plugin.items;

import org.bukkit.Material;
import org.gronia.plugin.uei.*;

import java.util.List;

public class Carrot extends CustomItem implements TierableItem {
    private final int tier;

    private Carrot(int tier, String internalName, String name) {
        super(Material.CARROT, internalName, name);
        this.tier = tier;
    }

    public static List<CustomItem> getAll() {
        return List.of(
                new Enchanted(),
                new ExtraEnchanted(),
                new UltraEnchanted(),
                new SuperEnchanted()
        );
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public boolean isConsumable() {
        return false;
    }

    @Override
    public boolean isEnchanted() {
        return true;
    }

    public static class Enchanted extends Carrot implements CraftableItem<CustomShapelessRecipe> {
        public Enchanted() {
            super(1, ItemNames.ENCHANTED_CARROT, "Enchanted Carrot");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, Material.CARROT);
        }
    }

    public static class ExtraEnchanted extends Carrot implements CraftableItem<CustomShapelessRecipe> {
        public ExtraEnchanted() {
            super(2, ItemNames.EXTRA_ENCHANTED_CARROT, "Extra Enchanted Carrot");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.ENCHANTED_CARROT);
        }
    }

    public static class UltraEnchanted extends Carrot implements CraftableItem<CustomShapelessRecipe> {
        public UltraEnchanted() {
            super(3, ItemNames.ULTRA_ENCHANTED_CARROT, "Ultra Enchanted Carrot");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.EXTRA_ENCHANTED_CARROT);
        }
    }

    public static class SuperEnchanted extends Carrot implements CraftableItem<CustomShapelessRecipe> {
        public SuperEnchanted() {
            super(4, ItemNames.SUPER_ENCHANTED_CARROT, "Super Enchanted Carrot");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(8, ItemNames.ULTRA_ENCHANTED_CARROT);
        }
    }
}
