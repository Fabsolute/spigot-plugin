package org.gronia.utils;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.Gronia;
import org.gronia.items.upgrader.UpgraderBase;
import org.jetbrains.annotations.NotNull;
import xyz.janboerman.guilib.api.mask.Mask;
import xyz.janboerman.guilib.api.mask.Pattern;
import xyz.janboerman.guilib.api.mask.patterns.BorderPattern;
import xyz.janboerman.guilib.api.menu.MenuHolder;

import java.util.Map;

public class UpgraderMenu extends MenuHolder<Gronia> {
    private final UpgraderBase upgraderBase;
    private final ItemStack upgrader;

    public UpgraderMenu(Gronia plugin, ItemStack upgrader, String name, UpgraderBase upgraderBase) {
        super(plugin, 54, name);
        this.upgraderBase = upgraderBase;
        this.upgrader = upgrader;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        var item = event.getCurrentItem();
        if (item == null) {
            return;
        }

        var meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        if (upgraderBase.upgrade(item)) {
            var tmpItem = upgrader.clone();
            tmpItem.setAmount(1);
            event.getView().getBottomInventory().removeItem(tmpItem);
        }

        event.getView().close();
    }

    @Override
    public @NotNull Inventory getInventory() {
        var inventory = super.getInventory();
        BorderPattern borderPattern = Pattern.border(9, 6);
        Mask<BorderPattern.Border, ItemStack> mask = Mask.ofMap(Map.of(
                BorderPattern.Border.OUTER, new ItemStack(Material.BLACK_STAINED_GLASS_PANE),
                BorderPattern.Border.INNER, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)));
        Mask.applyInventory(mask, borderPattern, inventory);

        return inventory;
    }
}
