package org.gronia.plugin.npc;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.SubListener;

public class NPCListener extends SubListener<NPCPlugin> {
    public NPCListener(NPCPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(this.getPlugin().BLACKSMITH_TITLE)) {
            return;
        }

        event.setCancelled(true);


        ItemStack current = event.getCurrentItem();
        if (current == null) {
            return;
        }

        if (current.getType() != Material.GREEN_WOOL) {
            return;
        }

        var player = event.getWhoClicked();

        this.getPlugin().getServer().dispatchCommand(player, "repair unsafe");
        event.getView().close();
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.AMBIENT, 1, 1);
    }
}
