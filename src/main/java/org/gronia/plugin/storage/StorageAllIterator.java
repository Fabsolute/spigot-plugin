package org.gronia.plugin.storage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import xyz.janboerman.guilib.api.GuiInventoryHolder;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class StorageAllIterator implements Iterator<GuiInventoryHolder<?>> {
    private int page;
    private final int pageCount;
    private final List<Map.Entry<String, Integer>> items;

    public StorageAllIterator(Map<String, Integer> items) {
        this.page = 0;
        this.items = items.entrySet().stream().filter(i -> i.getValue() != 0).sorted(Map.Entry.comparingByValue()).toList();
        this.pageCount = (int) (Math.ceil(items.size() / 45f));
    }

    @Override
    public boolean hasNext() {
        return pageCount > page;
    }

    @Override
    public GuiInventoryHolder<?> next() {
        var menu = new MenuHolder<>(Gronia.getInstance(), 45, "Page " + page);

        List<Map.Entry<String, Integer>> list = items.stream().skip(page * 45L).limit(45).collect(Collectors.toList());
        int i = 0;
        for (Map.Entry<String, Integer> e : list) {
            ItemStack stack = ItemRegistry.createItem(e.getKey());
            List<String> lore = new ArrayList<>();
            int count = e.getValue();
            if (count > 0) {
                lore.add(ChatColor.GREEN + "Count: " + count);
            } else {
                lore.add(ChatColor.RED + "Count: " + count);
            }
            ItemMeta meta = stack.getItemMeta();
            assert meta != null;
            meta.setLore(lore);
            stack.setItemMeta(meta);
            menu.setButton(i, new ItemButton<MenuHolder<Gronia>>(stack) {
                @Override
                public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                    super.onClick(holder, event);
                    Gronia.getInstance().getServer().dispatchCommand(event.getWhoClicked(), "storage open " + e.getKey());
                }
            });
            i++;
        }

        page++;
        return menu;
    }
}
