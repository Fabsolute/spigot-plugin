package org.gronia.plugin.uei;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;
import org.gronia.utils.Pair;

import java.util.List;
import java.util.Map;

public class UltraEnchantedItemPlugin extends SubPlugin<UltraEnchantedItemPlugin> {
    public final Map<String, List<Pair.Pair2<String, Integer>>> enchantConfigs = Map.of(
            Material.COBBLESTONE.name(),
            List.of(
                    Pair.Pair2.of("cobblestone", 1),
                    Pair.Pair2.of("enchanted_cobblestone", 4),
                    Pair.Pair2.of("extra_enchanted_cobblestone", 4),
                    Pair.Pair2.of("ultra_enchanted_cobblestone", 4),
                    Pair.Pair2.of("super_enchanted_cobblestone", 8)
            ),
            Material.CARROT.name(),
            List.of(
                    Pair.Pair2.of("carrot", 1),
                    Pair.Pair2.of("enchanted_carrot", 4),
                    Pair.Pair2.of("extra_enchanted_carrot", 4),
                    Pair.Pair2.of("ultra_enchanted_carrot", 4),
                    Pair.Pair2.of("super_enchanted_carrot", 8)
            ),
            Material.BAKED_POTATO.name(),
            List.of(
                    Pair.Pair2.of("baked_potato", 1),
                    Pair.Pair2.of("enchanted_baked_potato", 4)
            )
    );

    private final NamespacedKey hoeKey;

    public UltraEnchantedItemPlugin(JavaPlugin plugin) {
        super(plugin);
        hoeKey = this.<Gronia>getPlugin().getKey("super_hoe");
    }

    @Override
    public String getName() {
        return "uei";
    }

    @Override
    public SubListener<UltraEnchantedItemPlugin> getListener() {
        return new UltraEnchantedItemListener(this);
    }

    @Override
    public SubCommandExecutor<UltraEnchantedItemPlugin> getExecutor() {
        return null;
    }

    @Override
    public SubTabCompleter<UltraEnchantedItemPlugin> getTabCompleter() {
        return null;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        var recipe = new UltraEnchantedShapedRecipe(hoeKey, ItemUtils.createSuperHoe());
        recipe.shape("OO ", " C ", " C ");
        recipe.setIngredient('O', ItemUtils.createSuperEnchantedCobblestone());
        recipe.setIngredient('C', ItemUtils.createSuperEnchantedCarrot());
        this.<Gronia>getPlugin().addRecipe(recipe);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.<Gronia>getPlugin().getServer().removeRecipe(hoeKey);
    }
}
