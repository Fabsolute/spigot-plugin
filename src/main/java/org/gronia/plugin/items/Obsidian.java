package org.gronia.plugin.items;

import org.bukkit.Material;
import org.gronia.plugin.uei.*;

import java.util.List;

public class Obsidian extends CustomItem implements TierableItem {
    private final int tier;

    private Obsidian(int tier, String internalName, String name) {
        super(Material.OBSIDIAN, internalName, name);
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

    public static class Enchanted extends Obsidian implements CraftableItem<CustomShapelessRecipe> {
        public Enchanted() {
            super(1, ItemNames.ENCHANTED_OBSIDIAN, "Enchanted Obsidian");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(2, ItemNames.SUPER_ENCHANTED_COBBLESTONE);
        }
    }

    public static class ExtraEnchanted extends Obsidian implements CraftableItem<CustomShapelessRecipe> {
        public ExtraEnchanted() {
            super(2, ItemNames.EXTRA_ENCHANTED_OBSIDIAN, "Extra Enchanted Obsidian");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.ENCHANTED_OBSIDIAN);
        }
    }

    public static class UltraEnchanted extends Obsidian implements CraftableItem<CustomShapelessRecipe> {
        public UltraEnchanted() {
            super(3, ItemNames.ULTRA_ENCHANTED_OBSIDIAN, "Ultra Enchanted Obsidian");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.EXTRA_ENCHANTED_OBSIDIAN);
        }
    }

    public static class SuperEnchanted extends Obsidian implements CraftableItem<CustomShapelessRecipe> {
        public SuperEnchanted() {
            super(4, ItemNames.SUPER_ENCHANTED_OBSIDIAN, "Super Enchanted Obsidian");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.ULTRA_ENCHANTED_OBSIDIAN);
        }
    }
}
