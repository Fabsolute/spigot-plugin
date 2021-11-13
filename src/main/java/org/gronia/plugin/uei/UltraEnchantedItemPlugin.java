package org.gronia.plugin.uei;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;
import org.gronia.utils.Pair;

import java.util.List;
import java.util.Map;

public class UltraEnchantedItemPlugin extends SubPlugin<UltraEnchantedItemPlugin> {
    public final Map<Material, List<Pair<String, Integer>>> enchantConfigs = Map.of(
            Material.COBBLESTONE,
            List.of(
                    Pair.of("cobblestone", 1),
                    Pair.of("enchanted_cobblestone", 8),
                    Pair.of("extra_enchanted_cobblestone", 8),
                    Pair.of("ultra_enchanted_cobblestone", 8),
                    Pair.of("super_enchanted_cobblestone", 8)
            ),
            Material.CARROT,
            List.of(
                    Pair.of("carrot", 1),
                    Pair.of("enchanted_carrot", 4),
                    Pair.of("extra_enchanted_carrot", 4),
                    Pair.of("ultra_enchanted_carrot", 4),
                    Pair.of("super_enchanted_carrot", 8)
            ),
            Material.BAKED_POTATO,
            List.of(
                    Pair.of("baked_potato", 1),
                    Pair.of("enchanted_baked_potato", 4)
            )
    );

    public static final List<ChatColor> enchantColors = List.of(
            ChatColor.WHITE,
            ChatColor.GREEN,
            ChatColor.BLUE,
            ChatColor.LIGHT_PURPLE,
            ChatColor.GOLD
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
