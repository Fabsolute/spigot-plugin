package org.gronia.plugin.items;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.uei.*;
import org.gronia.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HyperFurnace extends CustomItem implements TierableItem, CraftableItem<CustomShapedRecipe>, EventListenerItem {
    private final Map<Block, Boolean> furnaces = new HashMap<>();
    private final ItemStack dp;

    public HyperFurnace() {
        super(Material.FURNACE, ItemNames.HYPER_FURNACE, "Hyper Furnace");
        dp = new ItemStack(Material.DIAMOND_PICKAXE);
    }

    @Override
    public int getTier() {
        return 3;
    }

    @Override
    public void fillRecipe(CustomShapedRecipe recipe) {
        recipe.shape("CCC", "CFC", "GRG");
        recipe.setIngredient('C', ItemNames.SUPER_ENCHANTED_COBBLESTONE);
        recipe.setIngredient('F', Material.FURNACE);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
    }

    @Override
    public boolean isShaped() {
        return true;
    }

    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        var block = event.getBlock();
        if (!furnaces.containsKey(block)) {
            var found = false;
            for (var drop : block.getDrops(dp)) {
                if (drop.getType() == Material.FURNACE) {
                    var meta = drop.getItemMeta();
                    assert meta != null;
                    if (meta.getDisplayName().equals(ItemRegistry.getItemName(ItemNames.HYPER_FURNACE))) {
                        found = true;
                        break;
                    }
                }
            }

            furnaces.put(block, found);
        }

        if (furnaces.get(block)) {
            event.setTotalCookTime(1);
        }
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(FurnaceStartSmeltEvent.class, this::onFurnaceStartSmelt)
        );
    }
}
