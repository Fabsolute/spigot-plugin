package org.gronia.disabled.craft;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.scheduler.BukkitRunnable;
import org.gronia.plugin.NumberMap;
import org.gronia.plugin.storage.StoragePlugin;

import java.util.Map;
import java.util.function.Predicate;

public class AutoCraftTask extends BukkitRunnable {

    private final CraftPlugin plugin;

    public AutoCraftTask(CraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ConfigurationSection autoList = plugin.getConfig().getConfigurationSection("auto");
        assert autoList != null;

        StoragePlugin plugin = this.plugin.getSubPlugin(StoragePlugin.class);

        NumberMap<String> changes = new NumberMap<>();
        for (String key : autoList.getKeys(false)) {
            String recipeName = autoList.getString(key);
            if (recipeName != null) {
                Recipe recipe = this.plugin.getServer().getRecipe(NamespacedKey.minecraft(recipeName));
                Predicate<NumberMap<String>> predicate = c -> {
                    ConfigurationSection stackableConfig = plugin.getStackableConfig();
                    for (Map.Entry<String, Integer> change : c.entrySet()) {
                        if (change.getValue() < 0) {
                            int count = stackableConfig.getInt(change.getKey(), 0);
                            if (count < -change.getValue()) {
                                return false;
                            }
                        }
                    }

                    return true;
                };

                if (recipe instanceof ShapelessRecipe) {
                    this.plugin.applyRecipe((ShapelessRecipe) recipe, key, 64, changes, predicate);
                } else if (recipe instanceof ShapedRecipe) {
                    this.plugin.applyRecipe((ShapedRecipe) recipe, key, 64, changes, predicate);
                }
            }
        }

        plugin.getAPI().applyStackable(ChatColor.LIGHT_PURPLE + "Auto Craft" + ChatColor.WHITE, changes);
    }
}
