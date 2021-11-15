package org.gronia.plugin.npc;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.Gronia;

public class BlacksmithTrait extends Trait {
    private final NPCPlugin plugin;

    private final ItemStack lockedAreaItem;
    private final ItemStack acceptButton;

    public BlacksmithTrait() {
        super("Blacksmith");
        this.plugin = Gronia.getInstance().getSubPlugin(NPCPlugin.class);
        lockedAreaItem = this.createEmptyItem(Material.BLACK_STAINED_GLASS_PANE);
        acceptButton = this.createEmptyItem(Material.GREEN_WOOL, "Repair All Items");
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (!event.getNPC().hasTrait(BlacksmithTrait.class)) {
            return;
        }

        var inventory = Bukkit.createInventory(null, 9, plugin.BLACKSMITH_TITLE);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, this.lockedAreaItem);
        }

        inventory.setItem(
                4,
                this.acceptButton
        );

        event.getClicker().openInventory(inventory);
    }

    private ItemStack createEmptyItem(Material material, String name) {
        ItemStack output = new ItemStack(material);
        ItemMeta meta = output.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        output.setItemMeta(meta);
        return output;
    }

    private ItemStack createEmptyItem(Material material) {
        return createEmptyItem(material, " ");
    }
}