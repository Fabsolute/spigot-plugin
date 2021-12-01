package org.gronia.menu;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEditor;
import com.comphenix.packetwrapper.WrapperPlayServerTileEntityData;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.gronia.menu.iterator.StorageIterator;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.storage.StoragePlugin;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.menu.BackButton;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;
import xyz.janboerman.guilib.api.menu.RedirectItemButton;

import java.util.List;
import java.util.Map;

public class StorageMenu {
    public static CustomPageMenu create(StoragePlugin plugin, StorageIterator iterator) {
        return CustomPageMenu.create(plugin.getPlugin(), iterator);
    }

    public static CustomPageMenu create(StoragePlugin plugin, Map<String, Integer> items, List<String> filter) {
        var pageMenu = create(plugin, new StorageIterator(items, filter));

        if (filter != null) {
            pageMenu.setButton(46, new BackButton<>(() -> create(plugin, items, null).getInventory()));
            return pageMenu;
        }

        for (int i = 0; i < 7; i++) {
            var category = plugin.getCategory(Integer.toString(i));
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

            pageMenu.setButton(46 + i, new RedirectItemButton<>(item, StorageMenu.create(plugin, items, category.items())::getInventory));
        }

        pageMenu.setButton(52, new ItemButton<MenuHolder<Gronia>>(new ItemBuilder(Material.OAK_SIGN).name("Search").build()) {
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
                plugin.getSignGUILocationMap().put(player.getUniqueId(), new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
                plugin.getSignGUILocationMap().put(player.getUniqueId(), new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
            }
        });

        return pageMenu;
    }
}
