package org.gronia.plugin.storage;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubListener;
import org.gronia.utils.GroniaMysqlConfiguration;
import org.gronia.utils.NumberMap;
import org.gronia.utils.Pair2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StorageListener extends SubListener<StoragePlugin> {
    private final Map<Inventory, Map<String, Integer>> tempCounts = new HashMap<>();

    public StorageListener(StoragePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent e) {
        Map<String, Integer> counts = new HashMap<>();

        String title = e.getView().getTitle();
        if (!title.startsWith(ChatColor.DARK_GRAY + "Storage ") && !title.startsWith(ChatColor.DARK_GRAY + "S ")) {
            return;
        }

        title = title.replace(ChatColor.DARK_GRAY + "Storage ", "").replace(ChatColor.DARK_GRAY + "S ", "").toLowerCase().replace(" ", "_");
        if (title.equalsIgnoreCase("view")) {
            return;
        }

        if (title.equalsIgnoreCase("deposit")) {
            return;
        }

        for (ItemStack stack : e.getInventory().getContents()) {
            if (stack == null) {
                continue;
            }

            if (stack.getAmount() == 0) {
                continue;
            }

            e.getPlayer().sendMessage(ChatColor.RED + "Already in use");
            e.setCancelled(true);
            return;
        }

        List<String> serializableList = this.getPlugin().getSerializableItemList();
        if (serializableList.contains(title)) {
            GroniaMysqlConfiguration config = this.getPlugin().getSerializableConfig();
            List<ItemStack> items = (List<ItemStack>) config.get(title, null);
            if (items == null) {
                return;
            }

            List<ItemStack> visible = items;
            List<ItemStack> remain = new ArrayList<>();
            if (items.size() > e.getInventory().getSize()) {
                visible = items.subList(0, e.getInventory().getSize() - 1);
                remain = items.subList(e.getInventory().getSize(), items.size() - 1);
            }

            for (ItemStack stack : visible) {
                e.getInventory().addItem(stack);
            }

            config.set(title, remain);
            config.setDirty();
        } else {
            GroniaMysqlConfiguration config = this.getPlugin().getStackableConfig();
            int oldCount = config.getInt(title, 0);
            if (oldCount > 0) {
                int count = oldCount;

                var material = ItemRegistry.getMaterialFor(title);

                int maxCount = material.getMaxStackSize() * e.getInventory().getSize();
                count = Math.min(count, maxCount);

                counts.put(title.toLowerCase(), count);

                int stackCount = (int) Math.ceil(((double) count) / material.getMaxStackSize());
                int diff = stackCount * material.getMaxStackSize() - count;

                for (int i = 0; i < stackCount; i++) {
                    ItemStack stack = ItemRegistry.createItem(title);
                    stack.setAmount(material.getMaxStackSize() - (i == stackCount - 1 ? diff : 0));
                    e.getInventory().addItem(stack);
                }

                int newCount = oldCount - count;
                config.set(title, newCount);
                config.setDirty();
            }

            this.tempCounts.put(e.getInventory(), counts);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        if (!title.startsWith(ChatColor.DARK_GRAY + "Storage ") && !title.startsWith(ChatColor.DARK_GRAY + "S ")) {
            return;
        }

        title = title.replace(ChatColor.DARK_GRAY + "Storage ", "").replace(ChatColor.DARK_GRAY + "S ", "");
        if (title.equalsIgnoreCase("View")) {
            return;
        }

        this.flushInventory(e.getInventory(), e.getPlayer());
    }

    private void flushInventory(Inventory inventory, HumanEntity entity) {
        Map<String, Integer> counts = this.tempCounts.getOrDefault(inventory, new HashMap<>());
        ItemStack[] contents = inventory.getContents();
        if (contents.length == 0) {
            return;
        }

        GroniaMysqlConfiguration serializableConfig = this.getPlugin().getSerializableConfig();
        var newCounts = new NumberMap<String>();
        for (ItemStack stack : contents) {
            if (stack == null) {
                continue;
            }

            String key = ItemRegistry.getInternalName(stack);

            List<String> serializableList = this.getPlugin().getSerializableItemList();
            if (serializableList.contains(key)) {
                List<ItemStack> stacks = (List<ItemStack>) serializableConfig.get(key, new ArrayList<ItemStack>());
                stacks.add(stack);
                serializableConfig.set(key, stacks);
                serializableConfig.setDirty();
            } else {
                newCounts.plus(key, stack.getAmount());
            }

            inventory.remove(stack);
        }

        this.getPlugin().applyStackable(entity.getName(), newCounts, counts);

        this.tempCounts.remove(inventory);
    }
}
