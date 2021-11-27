package org.gronia.items;

import org.bukkit.Material;
import org.gronia.plugin.uei.CraftableItem;
import org.gronia.plugin.uei.CustomItem;
import org.gronia.plugin.uei.CustomShapelessRecipe;
import org.gronia.plugin.uei.TierableItem;

import java.util.List;

public class SweetBerries extends CustomItem implements TierableItem {
    private final int tier;

    private SweetBerries(int tier, String internalName, String name) {
        super(Material.SWEET_BERRIES, internalName, name);
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

    public static class Enchanted extends SweetBerries implements CraftableItem<CustomShapelessRecipe> {
        public Enchanted() {
            super(1, ItemNames.ENCHANTED_SWEET_BERRIES, "Enchanted Sweet Berries");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, Material.SWEET_BERRIES);
        }
    }

    public static class ExtraEnchanted extends SweetBerries implements CraftableItem<CustomShapelessRecipe> {
        public ExtraEnchanted() {
            super(2, ItemNames.EXTRA_ENCHANTED_SWEET_BERRIES, "Extra Enchanted Sweet Berries");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.ENCHANTED_SWEET_BERRIES);
        }
    }

    public static class UltraEnchanted extends SweetBerries implements CraftableItem<CustomShapelessRecipe> {
        public UltraEnchanted() {
            super(3, ItemNames.ULTRA_ENCHANTED_SWEET_BERRIES, "Ultra Enchanted Sweet Berries");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.EXTRA_ENCHANTED_SWEET_BERRIES);
        }
    }

    public static class SuperEnchanted extends SweetBerries implements CraftableItem<CustomShapelessRecipe> {
        public SuperEnchanted() {
            super(4, ItemNames.SUPER_ENCHANTED_SWEET_BERRIES, "Super Enchanted Sweet Berries");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, ItemNames.ULTRA_ENCHANTED_SWEET_BERRIES);
        }
    }
}
