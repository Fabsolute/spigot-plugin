package org.gronia.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gronia.items.upgrader.UpgraderBase;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.warehouse.WareHousePlugin;
import org.jetbrains.annotations.NotNull;
import xyz.janboerman.guilib.api.mask.Mask;
import xyz.janboerman.guilib.api.mask.Pattern;
import xyz.janboerman.guilib.api.mask.patterns.BorderPattern;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.Map;

public class WareHouseCaseMenu extends MenuHolder<Gronia> {
    private final WareHousePlugin plugin;
    private static final ItemStack commonCaseItem;

    static {
        commonCaseItem = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(commonCaseItem, "{SkullOwner:{Id:[I;-1938086820,2026588190,-1123594741,2095845784],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFmN2NkZmVhMmQyMWNkNWY2ZWJiZjQ4NDgxNzYxYzZjYmRmMzZkMDBmZTY0MDgzNjg2ZTlhZWFhM2YxZjIxNyJ9fX0=\"}]}}}");

        var meta = commonCaseItem.getItemMeta();
        meta.displayName(Component.text("Common Case"));
        commonCaseItem.setItemMeta(meta);
    }

    public WareHouseCaseMenu(WareHousePlugin plugin) {
        super(plugin.getPlugin(), 9, "Cases");
        this.plugin = plugin;
        this.initialize();
    }

    private void initialize() {
        this.setButton(0, new ItemButton<MenuHolder<Gronia>>(commonCaseItem) {
            @Override
            public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                super.onClick(holder, event);
                plugin.executeListCaseCommand(event.getWhoClicked(), WareHousePlugin.COMMON_CASE_NAME);
            }
        });
    }
}
