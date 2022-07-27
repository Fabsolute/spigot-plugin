package org.gronia.plugin.warehouse;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubListener;
import org.gronia.utils.NumberMap;
import org.gronia.utils.configuration.CaseMemoryConfiguration;
import org.gronia.utils.pair.Pair2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WareHouseListener extends SubListener<WareHousePlugin> {
    private final Map<Inventory, NumberMap<String>> oldItems = new HashMap<>();

    public WareHouseListener(WareHousePlugin plugin) {
        super(plugin);
    }


    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent e) {
        var inv = e.getInventory();
        if (!this.getPlugin().inventoryMap.containsKey(inv)) {
            return;
        }

        var pair = this.getPlugin().inventoryMap.get(inv);

        var config = this.getPlugin().getWareHouseTally();

        var itemConfig = config.getConfig(pair.p1(), pair.p2());

        var material = ItemRegistry.getMaterialFor(pair.p2());
        assert material != null;
        var maxStackSize = material.getMaxStackSize();
        var slotCount = e.getInventory().getSize();

        var changes = new NumberMap<String>();

        for (var hash : itemConfig.getKeys(false)) {
            var hashConfig = (CaseMemoryConfiguration) itemConfig.getConfigurationSection(hash);
            assert hashConfig != null;
            var count = hashConfig.getInt("count", 0);
            if (count <= 0) {
                continue;
            }

            var item = hashConfig.getItemStack("item");
            assert item != null;

            do {
                var currentCount = Math.min(count, maxStackSize);
                count -= currentCount;
                slotCount--;

                var currentItem = item.clone();
                currentItem.setAmount(currentCount);
                inv.addItem(currentItem);

                changes.plus(pair.p2(), currentCount);
            } while (count != 0 && slotCount != 0);

            hashConfig.set("count", count);

            if (slotCount == 0) {
                break;
            }
        }

        config.setDirty();

        this.oldItems.put(inv, changes);
    }


    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        var inv = e.getInventory();
        if (!this.getPlugin().inventoryMap.containsKey(inv)) {
            return;
        }

        var oldItems = this.oldItems.get(inv);
        this.oldItems.remove(inv);

        var pair = this.getPlugin().inventoryMap.get(inv);

        var contents = inv.getContents();
        if (contents.length == 0) {
            return;
        }

        NumberMap<ItemStack> changes = new NumberMap<>();

        for (ItemStack stack : contents) {
            if (stack == null) {
                continue;
            }

            changes.plus(stack, stack.getAmount());
            inv.remove(stack);
        }

        this.getPlugin().applyStackable(e.getPlayer().getName(), pair.p1(), changes, this.getDiff(contents, oldItems));
        this.getPlugin().inventoryMap.remove(inv);
    }

    private NumberMap<String> getDiff(ItemStack[] newItems, Map<String, Integer> oldItems) {
        NumberMap<String> counts = new NumberMap<>();
        for (var stack : newItems) {
            if (stack == null) {
                continue;
            }

            counts.plus(ItemRegistry.getInternalName(stack), stack.getAmount());
        }

        for (var stack : oldItems.entrySet()) {
            counts.plus(stack.getKey(), -stack.getValue());
        }

        return counts;
    }
}
