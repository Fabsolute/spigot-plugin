package org.gronia.plugin.storage;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;
import org.gronia.utils.GroniaMysqlConfiguration;
import org.gronia.utils.Pair2;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class StoragePlugin extends SubPlugin<StoragePlugin> {
    private GroniaMysqlConfiguration storageStackable;
    private GroniaMysqlConfiguration storageSerializable;

    public StoragePlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "storage";
    }

    @Override
    public SubListener<StoragePlugin> getListener() {
        return new StorageListener(this);
    }

    @Override
    public SubCommandExecutor<StoragePlugin> getExecutor() {
        return new StorageCommand(this);
    }

    @Override
    public SubTabCompleter<StoragePlugin> getTabCompleter() {
        return null;
    }

    public GroniaMysqlConfiguration getStackableConfig() {
        if (storageStackable == null) {
            this.storageStackable = GroniaMysqlConfiguration.loadConfiguration(GroniaMysqlConfiguration.Integer.class, "storage_stackable");
        }

        return this.storageStackable;
    }

    public GroniaMysqlConfiguration getSerializableConfig() {
        if (storageSerializable == null) {
            this.storageSerializable = GroniaMysqlConfiguration.loadConfiguration(GroniaMysqlConfiguration.YAML.class, "storage_serializable");
        }

        return this.storageSerializable;
    }

    public List<String> getSerializableItemList() {
        return this.getConfig().getStringList("serializable_items");
    }

    public List<String> getNotTakableItemList() {
        return this.getConfig().getStringList("not_takable_list");
    }

    public ConfigurationSection getCategorySection(String id) {
        return this.getConfig().getConfigurationSection("category_" + id);
    }

    public StorageCategory getCategory(String id) {
        var section = getCategorySection(id);
        if (section == null) {
            return null;
        }

        return new StorageCategory(section.getBoolean("enabled"), section.getString("name"), section.getString("icon"), section.getStringList("list"));
    }

    public void executeTakeCommand(HumanEntity player, String itemName, int count) {
        this.getServer().dispatchCommand(player, "storage take " + getPassword() + " " + itemName + " " + count);
    }

    public void executeOpenCommand(HumanEntity player, String itemName) {
        this.getServer().dispatchCommand(player, "storage open " + getPassword() + " " + itemName);
    }

    public void executeListCommand(HumanEntity player) {
        this.getServer().dispatchCommand(player, "storage list " + getPassword());
    }


    public Map<String, Integer> applyStackable(final String name, Map<String, Integer> changes) {
        return this.applyStackable(name, changes, new HashMap<>());
    }

    public Map<String, Integer> applyStackable(final String name, Map<String, Integer> changes, Map<String, Integer> oldCounts) {
        ConfigurationSection stackableConfig = this.getStackableConfig();

        List<String> serializableItemList = this.getSerializableItemList();

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
            stackableConfig.set(materialName, newCount);

            if (oldCounts.containsKey(materialName)) {
                count += oldCounts.get(materialName);
            }

            if (count == 0) {
                continue;
            }

            if (count > 0) {
                if (totalLength < count) {
                    if (totalLength <= 0) {
                        messages.add(Pair2.of(ChatColor.DARK_PURPLE + "Storage " + ChatColor.RESET + name + " owed " + ChatColor.RED + "" + count + " " + materialName + ChatColor.WHITE + ". New count is " + ChatColor.GOLD + newCount + ChatColor.WHITE + ".", true));
                    } else {
                        messages.add(Pair2.of(ChatColor.DARK_PURPLE + "Storage " + ChatColor.RESET + name + " took " + ChatColor.GREEN + "" + totalLength + " " + materialName + ChatColor.WHITE + " and owed " + ChatColor.RED + (count - totalLength) + ChatColor.WHITE + ". New count is " + ChatColor.GOLD + newCount + ChatColor.WHITE + ".", true));
                    }
                } else {
                    messages.add(Pair2.of(ChatColor.DARK_PURPLE + "Storage " + ChatColor.RESET + name + " took " + ChatColor.GREEN + "" + count + " " + materialName + ChatColor.WHITE + ". New count is " + ChatColor.GOLD + newCount + ChatColor.WHITE + ".", true));
                }
            } else {
                messages.add(Pair2.of(ChatColor.DARK_PURPLE + "Storage " + ChatColor.RESET + name + " stored " + ChatColor.GREEN + "" + -count + " " + materialName + ChatColor.WHITE + ". New count is " + ChatColor.GOLD + newCount + ChatColor.WHITE + ".", false));
            }
        }

        this.sendMessages(messages, name);
        this.getStackableConfig().setDirty();

        return output;
    }

    public void sendMessages(List<Pair2<String, Boolean>> messages, String name) {
        if (messages.size() == 1) {
            this.getPlugin().getServer().broadcastMessage(messages.get(0).p1());
        } else if (messages.size() > 1) {
            var message = " did something in the storage.";
            if (messages.stream().allMatch(Pair2::p2)) {
                message = " took some things.";
            } else if (messages.stream().noneMatch(Pair2::p2)) {
                message = " stored some things.";
            }

            TextComponent component = new TextComponent(ChatColor.DARK_PURPLE + "Storage " + ChatColor.RESET + name + message + ChatColor.GREEN + ChatColor.BOLD + " HOVER");
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.join("\n", messages.stream().map(Pair2::p1).toList()))));
            this.getPlugin().getServer().spigot().broadcast(component);
            for (var msg : messages) {
                Bukkit.getLogger().log(Level.INFO, msg.p1());
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.getServer().getScheduler().runTaskTimerAsynchronously(this.getPlugin(), this::saveConfig, 1200, 1200);
    }

    @Override
    public void saveConfig() {
        try {
            if (this.storageSerializable != null) {
                this.storageSerializable.save();
            }

            if (this.storageStackable != null) {
                this.storageStackable.save();
            }
        } catch (SQLException ignored) {
        }

        super.saveConfig();
    }

}
