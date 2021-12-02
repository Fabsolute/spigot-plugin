package org.gronia.items.ingredient;

import org.bukkit.Material;
import org.gronia.items.ItemNames;
import org.gronia.plugin.uei.CraftableItem;
import org.gronia.plugin.uei.CustomItem;
import org.gronia.plugin.uei.CustomShapelessRecipe;
import org.gronia.plugin.uei.TierableItem;

import java.util.List;

public class CoalBlock extends CustomItem implements TierableItem {
    private final int tier;

    private CoalBlock(int tier, String internalName, String name) {
        super(Material.COAL_BLOCK, internalName, name);
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

    public static class Enchanted extends CoalBlock implements CraftableItem<CustomShapelessRecipe> {
        public Enchanted() {
            super(1, ItemNames.ENCHANTED_COAL_BLOCK, "Enchanted Coal Block");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(2, Material.COAL_BLOCK);
        }
    }

    public static class ExtraEnchanted extends CoalBlock implements CraftableItem<CustomShapelessRecipe> {
        public ExtraEnchanted() {
            super(2, ItemNames.EXTRA_ENCHANTED_COAL_BLOCK, "Extra Enchanted Coal Block");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.ENCHANTED_COAL_BLOCK);
        }
    }

    public static class UltraEnchanted extends CoalBlock implements CraftableItem<CustomShapelessRecipe> {
        public UltraEnchanted() {
            super(3, ItemNames.ULTRA_ENCHANTED_COAL_BLOCK, "Ultra Enchanted Coal Block");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.EXTRA_ENCHANTED_COAL_BLOCK);
        }
    }

    public static class SuperEnchanted extends CoalBlock implements CraftableItem<CustomShapelessRecipe> {
        public SuperEnchanted() {
            super(4, ItemNames.SUPER_ENCHANTED_COAL_BLOCK, "Super Enchanted Coal Block");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.ULTRA_ENCHANTED_COAL_BLOCK);
        }
    }
}
