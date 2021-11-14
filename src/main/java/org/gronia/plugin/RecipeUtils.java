package org.gronia.plugin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.uei.UltraEnchantedShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RecipeUtils {
    private static final List<NamespacedKey> keyList = new ArrayList<>();

    public static void registerAll() {
        registerHyperFurnace();
        registerSuperHoe();
    }

    public static void registerHyperFurnace() {
        register("hyper_furnace", ItemUtils::createHyperFurnace, recipe -> {
            recipe.shape("OOO", "OFO", "GRG");
            recipe.setIngredient('O', "super_enchanted_cobblestone");
            recipe.setIngredient('F', Material.FURNACE);
            recipe.setIngredient('G', Material.GOLD_BLOCK);
            recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        });
    }

    public static void registerSuperHoe() {
        register("super_hoe", ItemUtils::createSuperHoe, recipe -> {
            recipe.shape("OO ", " C ", " C ");
            recipe.setIngredient('O', "super_enchanted_cobblestone");
            recipe.setIngredient('C', "super_enchanted_carrot");
        });
    }

    public static void deregisterAll() {
        for (var key : keyList) {
            Gronia.getInstance().getServer().removeRecipe(key);
        }
    }

    private static void register(String name, Supplier<ItemStack> itemSupplier, Consumer<UltraEnchantedShapedRecipe> recipeConsumer) {
        var key = Gronia.getInstance().getKey(name);
        keyList.add(key);
        var recipe = new UltraEnchantedShapedRecipe(key, itemSupplier.get());
        recipeConsumer.accept(recipe);
        Gronia.getInstance().addRecipe(recipe);
    }
}
