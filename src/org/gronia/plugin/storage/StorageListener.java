package org.gronia.plugin.storage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.SubListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class StorageListener extends SubListener<StoragePlugin> {
    public StorageListener(StoragePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInventoryPickupItem(final InventoryMoveItemEvent e) {
        if (e.getSource().getHolder() instanceof Barrel) {
            this.handlePuller(e);
            return;
        }

        if (!(e.getDestination().getHolder() instanceof Barrel)) {
            return;
        }

        Barrel barrel = (Barrel) e.getDestination().getHolder();
        String name = barrel.getCustomName();
        if (name == null || !name.startsWith("[Flusher]")) {
            return;
        }

        if (e.getDestination().getItem(e.getDestination().getSize() - 1) == null) {
            return;
        }

        this.flushInventory(e.getDestination(), null, name);
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

        Material material;
        try {
            material = Material.valueOf(title.toUpperCase());
        } catch (IllegalArgumentException ex) {
            Player player = this.getPlugin().getServer().getPlayer(title);
            if (player != null && player != e.getPlayer()) {
                e.getPlayer().sendMessage(ChatColor.RED + "You dont have any access to open this chest.");
                e.setCancelled(true);
            }
            return;
        }

        List<String> serializableList = this.getPlugin().getSerializableItemList();
        if (serializableList.contains(title)) {
            ConfigurationSection config = this.getPlugin().getSerializableConfig();
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
            this.getPlugin().saveConfig();
        } else {
            ConfigurationSection config = this.getPlugin().getStackableConfig();
            int oldCount = config.getInt(title, 0);
            if (oldCount > 0) {
                int count = oldCount;

                int maxCount = material.getMaxStackSize() * e.getInventory().getSize();
                count = Math.min(count, maxCount);

                counts.put(title.toLowerCase(), count);

                int stackCount = (int) Math.ceil(((double) count) / material.getMaxStackSize());
                int diff = stackCount * material.getMaxStackSize() - count;

                for (int i = 0; i < stackCount; i++) {
                    ItemStack stack = new ItemStack(material, material.getMaxStackSize() - (i == stackCount - 1 ? diff : 0));
                    e.getInventory().addItem(stack);
                }

                int newCount = oldCount - count;
                config.set(title, newCount > 0 ? newCount : null);
                this.getPlugin().saveConfig();
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

        ConfigurationSection stackableConfig = this.getPlugin().getStackableConfig();
        ConfigurationSection serializableConfig = this.getPlugin().getSerializableConfig();
        Map<String, Integer> loads = new HashMap<>();
        Map<String, Integer> newCounts = new HashMap<>();
        for (ItemStack stack : contents) {
            if (stack == null) {
                continue;
            }

            String key = stack.getType().name().toLowerCase();

            List<String> serializableList = this.getPlugin().getSerializableItemList();
            if (serializableList.contains(key)) {
                List<ItemStack> stacks = (List<ItemStack>) serializableConfig.get(key, new ArrayList<ItemStack>());
                stacks.add(stack);
                serializableConfig.set(key, stacks);
            } else {
                ItemMeta meta = stack.getItemMeta();
                if (meta != null) {
                    if (meta.hasDisplayName()) {
                        this.drop(entity, inventory, stack);
                        continue;
                    }
                }

                int count = stackableConfig.getInt(key, 0);
                count += stack.getAmount();
                stackableConfig.set(key, count == 0 ? null : count);

                inventory.remove(stack);
                loads.put(key, loads.getOrDefault(key, 0) + stack.getAmount());
                newCounts.put(key, count);
            }
        }

        if (name == null) {
            name = "";
        }

        name = ChatColor.LIGHT_PURPLE + name.replace("[", "").replace("]", "") + ChatColor.WHITE;
        if (entity != null) {
            name = ChatColor.AQUA + entity.getName() + ChatColor.WHITE;
        }

        for (Map.Entry<String, Integer> load : loads.entrySet()) {
            int old = counts.getOrDefault(load.getKey(), 0);
            counts.remove(load.getKey());
            int count = load.getValue() - old;

            if (count == 0) {
                continue;
            }

            if (count > 0) {
                this.getPlugin().getServer().broadcastMessage("[Storage] " + name + " stored " + ChatColor.GREEN + "" + count + " " + load.getKey() + ChatColor.WHITE + " and new count is " + ChatColor.GOLD + newCounts.get(load.getKey()) + ChatColor.WHITE + ".");
            } else {
                this.getPlugin().getServer().broadcastMessage("[Storage] " + name + " took " + ChatColor.GREEN + "" + -count + " " + load.getKey() + ChatColor.WHITE + ".");
            }
        }

        for (Map.Entry<String, Integer> count : counts.entrySet()) {
            this.getPlugin().getServer().broadcastMessage("[Storage] " + name + " took " + ChatColor.GREEN + "" + count.getValue() + " " + count.getKey() + ChatColor.WHITE + ".");
        }

        this.getPlugin().tempCounts.remove(inventory);

        this.getPlugin().saveConfig();
    }

    private void drop(HumanEntity entity, Inventory inventory, ItemStack stack) {
        if (entity != null) {
            entity.getWorld().dropItemNaturally(entity.getLocation(), stack);
            inventory.remove(stack);
        }
    }

    private void handlePuller(final InventoryMoveItemEvent e) {
        Barrel barrel = (Barrel) e.getSource().getHolder();
        assert barrel != null;

        String name = barrel.getCustomName();

        if (name == null || !name.startsWith("[Puller]")) {
            return;
        }
        if (e.getSource().getItem(e.getSource().getSize() - 1) != null) {
            return;
        }

        String materialName = name.replace("[Puller] ", "").toLowerCase();
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (Exception ignored) {
            return;
        }

        List<String> serializableItemList = this.getPlugin().getSerializableItemList();
        if (serializableItemList.contains(materialName)) {
            return;
        }

        ConfigurationSection stackableConfig = this.getPlugin().getStackableConfig();

        int count = stackableConfig.getInt(materialName, 0);
        int totalCount = 0;


        this.getPlugin().getLogger().log(Level.INFO, name + " " + count);

        for (ItemStack stack : barrel.getInventory()) {
            if (stack != null) {
                continue;
            }

            int min = Math.min(material.getMaxStackSize(), count);

            if (min > 0) {
                stack = new ItemStack(material, min);
                count -= min;
                barrel.getInventory().addItem(stack);
                totalCount += min;
            }
        }

        if (totalCount == 0) {
            return;
        }

        int size = barrel.getInventory().getSize();

        ItemStack[] oldContents = barrel.getInventory().getContents();
        ItemStack[] newContents = new ItemStack[size];
        int j = size;
        for (int i = 0; i < size; i++) {
            newContents[j - 1] = oldContents[i];
            j = j - 1;
        }

        barrel.getInventory().setContents(newContents);

        this.getPlugin().getServer().broadcastMessage("[Storage] " + name.replace("[", "").replace("]", "") + " took " + ChatColor.GREEN + "" + totalCount + " " + materialName + ChatColor.WHITE + ".");

        stackableConfig.set(materialName, count == 0 ? null : count);

        this.getPlugin().saveConfig();
    }
}
