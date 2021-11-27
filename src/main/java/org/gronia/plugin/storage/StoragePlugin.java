package org.gronia.plugin.storage;

import com.comphenix.packetwrapper.WrapperPlayClientUpdateSign;
import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEditor;
import com.comphenix.packetwrapper.WrapperPlayServerTileEntityData;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;
import org.gronia.utils.GroniaMysqlConfiguration;
import org.gronia.utils.Pair2;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.menu.*;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class StoragePlugin extends SubPlugin<StoragePlugin> {
    private final Map<UUID, Location> mSignGUILocationMap = new HashMap<>();
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

    public Map<UUID, Location> getSignGUILocationMap() {
        return mSignGUILocationMap;
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
                totalLength += oldCounts.get(materialName);
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
        this.enableSearchListener();
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

    void enableSearchListener() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(new PacketAdapter(this.getPlugin(), PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                WrapperPlayClientUpdateSign wrapper = new WrapperPlayClientUpdateSign(event.getPacket());
                BlockPosition blockPos = wrapper.getLocation();
                Location savedPos = mSignGUILocationMap.get(event.getPlayer().getUniqueId());

                if (savedPos != null && blockPos.getX() == savedPos.getX() && blockPos.getY() == savedPos.getY() && blockPos.getZ() == savedPos.getZ()) {
                    // Do anything here
                    fixFakeBlockFor(event.getPlayer(), savedPos);
                    var line = wrapper.getLines()[0];
                    var items = StoragePlugin.this.getItems();
                    if (items == null) {
                        return;
                    }

                    var newMenu = PageMenu.create(StoragePlugin.this.getPlugin(), new StorageAllIterator(items, List.of("*" + line.toLowerCase() + "*")));
                    newMenu.setButton(45, new BackButton<>(StoragePlugin.this::getInventory));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(this.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            event.getPlayer().openInventory(newMenu.getInventory());
                        }
                    }, 1);
                }
            }
        });
    }

    void fixFakeBlockFor(Player player, Location loc) {
        if (loc.getWorld() != null && player.getWorld().equals(loc.getWorld())) {
            ProtocolManager manager = ProtocolLibrary.getProtocolManager();

            WrapperPlayServerBlockChange wrapperBlockChange = new WrapperPlayServerBlockChange(manager.createPacket(PacketType.Play.Server.BLOCK_CHANGE));

            Material material = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getType();
            WrappedBlockData blockData = WrappedBlockData.createData(material, loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getData());
            wrapperBlockChange.setLocation(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            wrapperBlockChange.setBlockData(blockData);

            wrapperBlockChange.sendPacket(player);
        }
    }

    public Map<String, Integer> getItems() {
        ConfigurationSection section = this.getStackableConfig();
        ConfigurationSection section2 = this.getSerializableConfig();
        Set<String> keys = section.getKeys(false);
        Set<String> keys2 = section2.getKeys(false);

        Map<String, Integer> items = new HashMap<>();
        for (String key : keys) {
            items.put(key, section.getInt(key));
        }

        for (String key : keys2) {
            items.put(key, section2.getList(key, new ArrayList<>()).size());
        }

        if (items.size() == 0) {
            return null;
        }

        return items;
    }

    public void showInventory(final HumanEntity ent) {
        var inventory = getInventory();
        if (inventory == null) {
            return;
        }

        ent.openInventory(inventory);
    }

    public Inventory getInventory() {
        var items = this.getItems();
        if (items == null) {
            return null;
        }

        var pageMenu = PageMenu.create(this.getPlugin(), new StorageAllIterator(items));
        for (int i = 0; i < 9; i++) {
            var category = this.getCategory(Integer.toString(i));
            if (category == null || !category.enabled()) {
                continue;
            }

            Material m = Material.BARRIER;

            try {
                m = Material.valueOf(category.icon());
            } catch (Exception ignored) {
            }

            var item = new ItemStack(m);
            var meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.RED + "Category: " + ChatColor.GREEN + category.name());
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);

            pageMenu.setButton(45 + i, new RedirectItemButton<>(item, () -> {
                var newMenu = PageMenu.create(this.getPlugin(), new StorageAllIterator(items, category.items()));
                newMenu.setButton(45, new BackButton<>(pageMenu::getInventory));
                return newMenu.getInventory();
            }));
        }

        pageMenu.setButton(53, new ItemButton<MenuHolder<Gronia>>(new ItemBuilder(Material.OAK_SIGN).name("Search").build()) {
            @Override
            public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                super.onClick(holder, event);
                var player = (Player) event.getWhoClicked();
                ProtocolManager manager = ProtocolLibrary.getProtocolManager();
                BlockPosition pos = new BlockPosition(player.getLocation().getBlockX(), 0, player.getLocation().getBlockZ());
                NbtCompound signNbt = NbtFactory.ofCompound("Search");
                signNbt.put("Text1", "{\"text\":\"\"}");
                signNbt.put("Text2", "{\"text\":\"===============\"}");
                signNbt.put("Text3", "{\"text\":\"===============\"}");
                signNbt.put("Text4", "{\"text\":\"===============\"}");
                signNbt.put("id", "minecraft:oak_sign");
                signNbt.put("x", pos.getX());
                signNbt.put("y", pos.getY());
                signNbt.put("z", pos.getZ());

                WrapperPlayServerBlockChange wrapperBlockChange = new WrapperPlayServerBlockChange(manager.createPacket(PacketType.Play.Server.BLOCK_CHANGE));
                WrapperPlayServerOpenSignEditor wrapperOpenSignEditor = new WrapperPlayServerOpenSignEditor(manager.createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR));
                WrapperPlayServerTileEntityData wrapperTileEntityData = new WrapperPlayServerTileEntityData(manager.createPacket(PacketType.Play.Server.TILE_ENTITY_DATA));

                wrapperBlockChange.setLocation(pos);
                wrapperBlockChange.setBlockData(WrappedBlockData.createData(Material.OAK_SIGN));
                wrapperOpenSignEditor.setLocation(pos);
                wrapperTileEntityData.setNbtData(signNbt);
                wrapperTileEntityData.setAction(9);
                wrapperTileEntityData.setLocation(pos);
                wrapperBlockChange.sendPacket(player);
                wrapperOpenSignEditor.sendPacket(player);
                wrapperTileEntityData.sendPacket(player);
                StoragePlugin.this.getSignGUILocationMap().put(player.getUniqueId(), new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
                StoragePlugin.this.getSignGUILocationMap().put(player.getUniqueId(), new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
            }
        });

        return pageMenu.getInventory();
    }
}
