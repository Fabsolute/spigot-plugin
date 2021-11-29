package org.gronia.npc;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.repair.RepairPlugin;
import xyz.janboerman.guilib.api.menu.*;

public class BlacksmithTrait extends Trait {
    private final MenuHolder<Gronia> menu;

    public BlacksmithTrait() {
        super("Blacksmith");
        menu = new MenuHolder<>(Gronia.getInstance(), 9, "Blacksmith");

        menu.setButton(0, new ItemButton<MenuHolder<Gronia>>(this.createEmptyItem(Material.DIAMOND_SWORD, "Repair All Items")) {
            @Override
            public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                super.onClick(holder, event);
                var player = event.getWhoClicked();
                Gronia.getInstance().getSubPlugin(RepairPlugin.class).executeRepairUnsafe(player);
                event.getView().close();
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.AMBIENT, 1, 1);
            }
        });
        menu.setButton(4, new ItemButton<MenuHolder<Gronia>>(this.createEmptyItem(Material.ENCHANTED_BOOK, "Repair Mending Items")) {
            @Override
            public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                super.onClick(holder, event);
                var player = event.getWhoClicked();
                Gronia.getInstance().getSubPlugin(RepairPlugin.class).executeRepair(player);
                event.getView().close();
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.AMBIENT, 1, 1);
            }
        });
        menu.setButton(8, new CloseButton<>(Material.BARRIER));
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (!event.getNPC().hasTrait(BlacksmithTrait.class)) {
            return;
        }

        event.getClicker().openInventory(menu.getInventory());
    }

    private ItemStack createEmptyItem(Material material, String name) {
        ItemStack output = new ItemStack(material);
        ItemMeta meta = output.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        output.setItemMeta(meta);
        return output;
    }
}
