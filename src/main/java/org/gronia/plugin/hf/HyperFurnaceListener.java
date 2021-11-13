package org.gronia.plugin.hf;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.ItemUtils;
import org.gronia.plugin.SubListener;

import java.util.HashMap;
import java.util.Map;

public class HyperFurnaceListener extends SubListener<HyperFurnacePlugin> {
    private final Map<Block, Boolean> furnaces = new HashMap<>();
    private final ItemStack dp;

    public HyperFurnaceListener(HyperFurnacePlugin plugin) {
        super(plugin);
        this.dp = new ItemStack(Material.DIAMOND_PICKAXE);
    }

    @EventHandler
    public void onFurnaceBurn(FurnaceStartSmeltEvent event) {
        var block = event.getBlock();
        if (!furnaces.containsKey(block)) {
            var found = false;
            for (var drop : block.getDrops(dp)) {
                if (drop.getType() == Material.FURNACE) {
                    var meta = drop.getItemMeta();
                    assert meta != null;
                    if (meta.getDisplayName().equals(ItemUtils.getHyperFurnaceName())) {
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
}
