package org.gronia.items.ingredient;

import org.bukkit.Material;
import org.gronia.items.ItemNames;
import org.gronia.plugin.uei.*;

import java.util.List;

public class Cobblestone extends CustomItem implements TierableItem {
    private final int tier;

    private Cobblestone(int tier, String internalName, String name) {
        super(Material.COBBLESTONE, internalName, name);
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
    public boolean isPlaceable() {
        return false;
    }

    @Override
    public boolean isEnchanted() {
        return true;
    }

    public static class Enchanted extends Cobblestone implements CraftableItem<CustomShapelessRecipe> {
        public Enchanted() {
            super(1, ItemNames.ENCHANTED_COBBLESTONE, "Enchanted Cobblestone");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, Material.COBBLESTONE);
        }
    }

    public static class ExtraEnchanted extends Cobblestone implements CraftableItem<CustomShapelessRecipe> {
        public ExtraEnchanted() {
            super(2, ItemNames.EXTRA_ENCHANTED_COBBLESTONE, "Extra Enchanted Cobblestone");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.ENCHANTED_COBBLESTONE);
        }
    }

    public static class UltraEnchanted extends Cobblestone implements CraftableItem<CustomShapelessRecipe> {
        public UltraEnchanted() {
            super(3, ItemNames.ULTRA_ENCHANTED_COBBLESTONE, "Ultra Enchanted Cobblestone");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.EXTRA_ENCHANTED_COBBLESTONE);
        }
    }

    public static class SuperEnchanted extends Cobblestone implements CraftableItem<CustomShapelessRecipe> {
        public SuperEnchanted() {
            super(4, ItemNames.SUPER_ENCHANTED_COBBLESTONE, "Super Enchanted Cobblestone");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(8, ItemNames.ULTRA_ENCHANTED_COBBLESTONE);
        }
    }
}
