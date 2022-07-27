package org.gronia.menu.iterator;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.warehouse.WareHousePlugin;
import xyz.janboerman.guilib.api.GuiInventoryHolder;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WareHouseIterator implements Iterator<GuiInventoryHolder<Gronia>> {
    private int page;
    private final int pageCount;
    private final List<Map.Entry<String, Integer>> items;
    private final String caseName;

    public WareHouseIterator(String caseName, Map<String, Integer> items, List<String> filter) {
        var newItems = items.entrySet().stream().filter(i -> i.getValue() != 0);
        if (filter != null) {
            newItems = newItems.filter(i -> filter.stream().anyMatch(f -> {
                var contains = f.contains("*");
                f = f.replaceAll("\\*", "");
                if (contains) {
                    return i.getKey().contains(f);
                }

                return f.equalsIgnoreCase(i.getKey());
            }));
        }

        this.page = 0;
        this.items = newItems.sorted(Map.Entry.comparingByValue()).toList();
        this.pageCount = (int) (Math.ceil(this.items.size() / 45f));
        this.caseName = caseName;
    }

    public WareHouseIterator(String caseName, Map<String, Integer> items) {
        this(caseName, items, null);
    }

    @Override
    public boolean hasNext() {
        return pageCount > page;
    }

    @Override
    public GuiInventoryHolder<Gronia> next() {
        var menu = new MenuHolder<>(Gronia.getInstance(), 45, "WareHouse Page " + page);

        List<Map.Entry<String, Integer>> list = items.stream().skip(page * 45L).limit(45).toList();
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
                    Gronia.getInstance().getSubPlugin(WareHousePlugin.class).executeOpenCommand(event.getWhoClicked(), caseName, e.getKey());
                }
            });
            i++;
        }

        page++;
        return menu;
    }
}
