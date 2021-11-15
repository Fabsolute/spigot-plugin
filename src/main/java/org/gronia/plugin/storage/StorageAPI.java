package org.gronia.plugin.storage;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.ItemRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageAPI {
    private final StoragePlugin plugin;

    public StorageAPI(StoragePlugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, Integer> applyStackable(final String name, Map<String, Integer> changes) {
        ConfigurationSection stackableConfig = this.plugin.getStackableConfig();

        List<String> serializableItemList = this.plugin.getSerializableItemList();

        Map<String, Integer> output = new HashMap<>();

        for (Map.Entry<String, Integer> change : changes.entrySet()) {
            String materialName = change.getKey();
            int count = -change.getValue();

            if (serializableItemList.contains(materialName)) {
                output.put(materialName, count);
                continue;
            }

            int totalLength = stackableConfig.getInt(materialName, 0);

            int newCount = totalLength - count;

            if (count > 0) {
                if (totalLength < count) {
                    if (totalLength <= 0) {
                        this.plugin.getServer().broadcastMessage("[Storage] " + name + " owed " + ChatColor.RED + "" + count + " " + materialName + ChatColor.WHITE + ".");
                    } else {
                        this.plugin.getServer().broadcastMessage("[Storage] " + name + " took " + ChatColor.GREEN + "" + totalLength + " " + materialName + ChatColor.WHITE + " and owed " + ChatColor.RED + (count - totalLength) + ChatColor.WHITE + ".");
                    }
                } else {
                    this.plugin.getServer().broadcastMessage("[Storage] " + name + " took " + ChatColor.GREEN + "" + count + " " + materialName + ChatColor.WHITE + ".");
                }
            } else {
                this.plugin.getServer().broadcastMessage("[Storage] " + name + " stored " + ChatColor.GREEN + "" + -count + " " + materialName + ChatColor.WHITE + " and new count is " + ChatColor.GOLD + newCount + ChatColor.WHITE + ".");
            }

            stackableConfig.set(materialName, newCount);
        }

        this.plugin.getStackableConfig().setDirty();

        return output;
    }

    public Map<String, Integer> getLoans() {
        ConfigurationSection stackableConfig = this.plugin.getStackableConfig();

        Map<String, Integer> output = new HashMap<>();

        for (String materialName : stackableConfig.getKeys(false)) {
            int count = stackableConfig.getInt(materialName);
            if (count < 0) {
                output.put(materialName, count);
            }
        }

        return output;
    }

    public List<String> getSerializableItemList() {
        return this.plugin.getSerializableItemList();
    }

    public void addItemToPlayer(HumanEntity player, int count, String materialName) {
        var stack = ItemRegistry.createItem(materialName);
        stack.setAmount(count);
        this.addItemToPlayer(player, stack);
    }

    public void addItemToPlayer(HumanEntity player, ItemStack stack) {
        HashMap<Integer, ItemStack> drops = player.getInventory().addItem(stack);
        for (ItemStack item : drops.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item).setOwner(player.getUniqueId());
        }
    }
}
