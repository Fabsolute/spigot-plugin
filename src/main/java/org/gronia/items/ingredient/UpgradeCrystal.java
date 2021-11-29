package org.gronia.items.ingredient;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.gronia.items.ItemNames;
import org.gronia.plugin.uei.CraftableItem;
import org.gronia.plugin.uei.CustomItem;
import org.gronia.plugin.uei.CustomShapedRecipe;
import org.gronia.plugin.uei.TierableItem;

public class UpgradeCrystal extends CustomItem implements TierableItem, CraftableItem<CustomShapedRecipe> {
    public UpgradeCrystal() {
        super(Material.PLAYER_HEAD, ItemNames.UPGRADE_CRYSTAL, "Upgrade Crystal");
    }

    @Override
    public void fillRecipe(CustomShapedRecipe recipe) {
        recipe.shape("E");
        recipe.setIngredient('E', ItemNames.SUPER_ENCHANTED_NETHERRACK);
    }

    @Override
    public boolean isShaped() {
        return true;
    }

    @Override
    public int getTier() {
        return 3;
    }

    @Override
    public boolean isPlaceable() {
        return false;
    }

    @Override
    public void beforeCreate(ItemStack stack) {
        super.beforeCreate(stack);
        Bukkit.getUnsafe().modifyItemStack(
                stack,
                "{SkullOwner:{Id:[I;-1718110351,-1251721086,-1203867091,1926394648],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGY3NDY3YzVmNzM4YzY0MTI0NmMwOWY4Y2U3OTFlMzM5YTg2ZTgxZGU2MjA0OWI0MWY0OTI4ODgxNzJmYTcyNiJ9fX0=\"}]}}}"
        );
    }
}