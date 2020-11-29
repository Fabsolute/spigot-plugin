package org.gronia.plugin.craft;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;
import org.gronia.plugin.storage.StoragePlugin;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;

public class Craft extends SubPlugin<Craft> {
    public Craft(JavaPlugin plugin) {
        super(plugin);
    }

    private AutoCraftTask task;

    @Override
    public void onEnable() {
        super.onEnable();
        this.getConfig().addDefault("auto", new MemoryConfiguration());
        this.getConfig().addDefault("forced_recipes", new MemoryConfiguration());
        this.getConfig().addDefault("blocked_materials", new ArrayList<String>());
        this.task = new AutoCraftTask(this);
        this.task.runTaskTimer(this.getPlugin(), 100, 600);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.task.cancel();
    }

    @Override
    public String getName() {
        return "craft";
    }

    @Override
    public SubListener<Craft> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<Craft> getExecutor() {
        return new CraftCommand(this);
    }

    @Override
    public SubTabCompleter<Craft> getTabCompleter() {
        return null;
    }

    public void applyRecipe(ShapedRecipe shapedRecipe, String materialName, int count, NumberMap<String> changes) {
        this.applyRecipe(shapedRecipe, materialName, count, changes, null);
    }

    public void applyRecipe(ShapedRecipe shapedRecipe, String materialName, int count, NumberMap<String> changes, Predicate<NumberMap<String>> predicate) {
        int multiplier = (int) Math.ceil(count / (double) shapedRecipe.getResult().getAmount());
        this.getPlugin().getLogger().log(Level.INFO, "multiplier " + multiplier);
        String[] shapes = shapedRecipe.getShape();

        NumberMap<String> newChanges = new NumberMap<>();

        for (Map.Entry<Character, ItemStack> ingredient : shapedRecipe.getIngredientMap().entrySet()) {
            if (ingredient.getValue() == null) {
                continue;
            }

            String type = ingredient.getValue().getType().name().toLowerCase();
            if (this.getSubPlugin(StoragePlugin.class).getAPI().getSerializableItemList().contains(type)) {
                return;
            }

            int usage = 0;
            for (String shape : shapes) {
                for (Character c : shape.toCharArray()) {
                    if (c == ingredient.getKey()) {
                        usage++;
                    }
                }
            }
            newChanges.plus(type, -(usage * multiplier));
        }

        newChanges.plus(materialName, multiplier * shapedRecipe.getResult().getAmount());

        if (predicate == null || predicate.test(newChanges)) {
            for (Map.Entry<String, Integer> change : newChanges.entrySet()) {
                changes.plus(change.getKey(), change.getValue());
            }
        }
    }

    public void applyRecipe(ShapelessRecipe shapelessRecipe, String materialName, int count, NumberMap<String> changes) {
        this.applyRecipe(shapelessRecipe, materialName, count, changes, null);
    }

    public void applyRecipe(ShapelessRecipe shapelessRecipe, String materialName, int count, NumberMap<String> changes, Predicate<NumberMap<String>> predicate) {
        NumberMap<String> newChanges = new NumberMap<>();
        int multiplier = (int) Math.ceil(count / (double) shapelessRecipe.getResult().getAmount());
        for (ItemStack stack : shapelessRecipe.getIngredientList()) {
            newChanges.plus(stack.getType().name().toLowerCase(), -(multiplier * stack.getAmount()));
        }

        newChanges.plus(materialName, multiplier * shapelessRecipe.getResult().getAmount());

        if (predicate == null || predicate.test(newChanges)) {
            for (Map.Entry<String, Integer> change : newChanges.entrySet()) {
                changes.plus(change.getKey(), change.getValue());
            }
        }
    }
}
