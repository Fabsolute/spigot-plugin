package org.gronia.items;

import org.bukkit.Material;
import org.gronia.plugin.uei.CraftableItem;
import org.gronia.plugin.uei.CustomItem;
import org.gronia.plugin.uei.CustomShapelessRecipe;
import org.gronia.plugin.uei.TierableItem;

import java.util.List;

public class Netherrack extends CustomItem implements TierableItem {
    private final int tier;

    private Netherrack(int tier, String internalName, String name) {
        super(Material.NETHERRACK, internalName, name);
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

    public static class Enchanted extends Netherrack implements CraftableItem<CustomShapelessRecipe> {
        public Enchanted() {
            super(1, ItemNames.ENCHANTED_NETHERRACK, "Enchanted Netherrack");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(8, Material.NETHERRACK);
        }
    }

    public static class ExtraEnchanted extends Netherrack implements CraftableItem<CustomShapelessRecipe> {
        public ExtraEnchanted() {
            super(2, ItemNames.EXTRA_ENCHANTED_NETHERRACK, "Extra Enchanted Netherrack");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(8, ItemNames.ENCHANTED_NETHERRACK);
        }
    }

    public static class UltraEnchanted extends Netherrack implements CraftableItem<CustomShapelessRecipe> {
        public UltraEnchanted() {
            super(3, ItemNames.ULTRA_ENCHANTED_NETHERRACK, "Ultra Enchanted Netherrack");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(8, ItemNames.EXTRA_ENCHANTED_NETHERRACK);
        }
    }

    public static class SuperEnchanted extends Netherrack implements CraftableItem<CustomShapelessRecipe> {
        public SuperEnchanted() {
            super(4, ItemNames.SUPER_ENCHANTED_NETHERRACK, "Super Enchanted Netherrack");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(8, ItemNames.ULTRA_ENCHANTED_NETHERRACK);
        }
    }
}
