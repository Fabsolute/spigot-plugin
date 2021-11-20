package org.gronia.plugin.storage;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubListener;
import org.gronia.utils.GroniaMysqlConfiguration;
import org.gronia.utils.Pair2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StorageListener extends SubListener<StoragePlugin> {
    public StorageListener(StoragePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getView().getTitle().equals("[Storage] View")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getView().getTitle().equals("[Storage] View")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent e) {
        Map<String, Integer> counts = new HashMap<>();

        String title = e.getView().getTitle();
        if (!title.startsWith("[Storage] ") && !title.startsWith("[S] ")) {
            return;
        }

        title = title.replace("[Storage] ", "").replace("[S] ", "").toLowerCase().replace(" ", "_");
        if (title.equalsIgnoreCase("view")) {
            return;
        }

        if (title.equalsIgnoreCase("deposit")) {
            return;
        }

        if (StorageCommand.DISABLED && !e.getPlayer().getName().equalsIgnoreCase("fabsolutely")) {
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

            this.getPlugin().tempCounts.put(e.getInventory(), counts);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        if (!title.startsWith("[Storage] ") && !title.startsWith("[S] ")) {
            return;
        }

        title = title.replace("[Storage] ", "").replace("[S] ", "");
        if (title.equalsIgnoreCase("View")) {
            return;
        }

        if (StorageCommand.DISABLED && !e.getPlayer().getName().equalsIgnoreCase("fabsolutely")) {
            return;
        }

        this.flushInventory(e.getInventory(), e.getPlayer());
    }


    private void flushInventory(Inventory inventory, HumanEntity entity) {
        this.flushInventory(inventory, entity, null);
    }

    private void flushInventory(Inventory inventory, HumanEntity entity, String name) {
        Map<String, Integer> counts = this.getPlugin().tempCounts.getOrDefault(inventory, new HashMap<>());
        ItemStack[] contents = inventory.getContents();
        if (contents.length == 0) {
            return;
        }

        GroniaMysqlConfiguration stackableConfig = this.getPlugin().getStackableConfig();
        GroniaMysqlConfiguration serializableConfig = this.getPlugin().getSerializableConfig();
        Map<String, Integer> loads = new HashMap<>();
        Map<String, Integer> newCounts = new HashMap<>();
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
                int count = stackableConfig.getInt(key, 0);
                count += stack.getAmount();
                stackableConfig.set(key, count);

                stackableConfig.setDirty();

                loads.put(key, loads.getOrDefault(key, 0) + stack.getAmount());
                newCounts.put(key, count);
            }

            inventory.remove(stack);
        }

        if (name == null) {
            name = "";
        }

        name = ChatColor.LIGHT_PURPLE + name.replace("[", "").replace("]", "") + ChatColor.WHITE;
        if (entity != null) {
            name = ChatColor.AQUA + entity.getName() + ChatColor.WHITE;
        }

        var messages = new ArrayList<Pair2<String, Boolean>>();

        for (Map.Entry<String, Integer> load : loads.entrySet()) {
            int old = counts.getOrDefault(load.getKey(), 0);
            counts.remove(load.getKey());
            int count = load.getValue() - old;

            if (count == 0) {
                continue;
            }

            if (count > 0) {
                messages.add(Pair2.of("[Storage] " + name + " stored " + ChatColor.GREEN + "" + count + " " + load.getKey() + ChatColor.WHITE + " and new count is " + ChatColor.GOLD + newCounts.get(load.getKey()) + ChatColor.WHITE + ".",false));
            } else {
                messages.add(Pair2.of("[Storage] " + name + " took " + ChatColor.GREEN + "" + -count + " " + load.getKey() + ChatColor.WHITE + ".",true));
            }
        }

        for (Map.Entry<String, Integer> count : counts.entrySet()) {
            messages.add(Pair2.of("[Storage] " + name + " took " + ChatColor.GREEN + "" + count.getValue() + " " + count.getKey() + ChatColor.WHITE + ".",true));
        }

        this.getPlugin().getAPI().sendMessages(messages, name);

        this.getPlugin().tempCounts.remove(inventory);
    }
}
