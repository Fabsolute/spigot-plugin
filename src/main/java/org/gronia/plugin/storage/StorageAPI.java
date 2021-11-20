package org.gronia.plugin.storage;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.ItemRegistry;
import org.gronia.utils.Pair2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class StorageAPI {
    private final StoragePlugin plugin;

    public StorageAPI(StoragePlugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, Integer> applyStackable(final String name, Map<String, Integer> changes) {
        ConfigurationSection stackableConfig = this.plugin.getStackableConfig();

        List<String> serializableItemList = this.plugin.getSerializableItemList();

        Map<String, Integer> output = new HashMap<>();

        var messages = new ArrayList<Pair2<String, Boolean>>();

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
                        messages.add(Pair2.of("[Storage] " + name + " owed " + ChatColor.RED + "" + count + " " + materialName + ChatColor.WHITE + ".", true));
                    } else {
                        messages.add(Pair2.of("[Storage] " + name + " took " + ChatColor.GREEN + "" + totalLength + " " + materialName + ChatColor.WHITE + " and owed " + ChatColor.RED + (count - totalLength) + ChatColor.WHITE + ".", true));
                    }
                } else {
                    messages.add(Pair2.of("[Storage] " + name + " took " + ChatColor.GREEN + "" + count + " " + materialName + ChatColor.WHITE + ".", true));
                }
            } else {
                messages.add(Pair2.of("[Storage] " + name + " stored " + ChatColor.GREEN + "" + -count + " " + materialName + ChatColor.WHITE + " and new count is " + ChatColor.GOLD + newCount + ChatColor.WHITE + ".", false));
            }

            stackableConfig.set(materialName, newCount);
        }

        sendMessages(messages, name);
        this.plugin.getStackableConfig().setDirty();

        return output;
    }

    public void sendMessages(List<Pair2<String, Boolean>> messages, String name) {
        if (messages.size() == 1) {
            this.plugin.getServer().broadcastMessage(messages.get(0).p1());
        } else if (messages.size() > 1) {
            var message = " did something in the storage.";
            if (messages.stream().allMatch(Pair2::p2)) {
                message = " took some things.";
            } else if (messages.stream().noneMatch(Pair2::p2)) {
                message = " stored some things.";
            }

            TextComponent component = new TextComponent("[Storage] " + name + message + ChatColor.GREEN + ChatColor.BOLD + " HOVER");
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.join("\n", messages.stream().map(Pair2::p1).toList()))));
            this.plugin.getServer().spigot().broadcast(component);
            for (var msg : messages) {
                Bukkit.getLogger().log(Level.INFO, msg.p1());
            }
        }
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
