package org.gronia.plugin.hf;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;
import org.gronia.plugin.uei.UltraEnchantedShapedRecipe;

public class HyperFurnacePlugin extends SubPlugin<HyperFurnacePlugin> {
    final NamespacedKey key;

    public HyperFurnacePlugin(JavaPlugin plugin) {
        super(plugin);
        key = this.<Gronia>getPlugin().getKey("hyper_furnace");
    }

    @Override
    public String getName() {
        return "hf";
    }

    @Override
    public SubListener<HyperFurnacePlugin> getListener() {
        return new HyperFurnaceListener(this);
    }

    @Override
    public SubCommandExecutor<HyperFurnacePlugin> getExecutor() {
        return null;
    }

    @Override
    public SubTabCompleter<HyperFurnacePlugin> getTabCompleter() {
        return null;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        var recipe = new UltraEnchantedShapedRecipe(key, ItemUtils.createHyperFurnace());
        recipe.shape("OOO", "OFO", "GRG");
        recipe.setIngredient('O', ItemUtils.createSuperEnchantedCobblestone());
        recipe.setIngredient('F', Material.FURNACE);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        this.<Gronia>getPlugin().addRecipe(recipe);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.getServer().removeRecipe(key);
    }
}
