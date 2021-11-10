package org.gronia.plugin.ti;

import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.pouch.PouchPlugin;

public class TeleportItemListener extends SubListener<TeleportItemPlugin> {
    public TeleportItemListener(TeleportItemPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var state = event.getBlock().getState();
        if (state instanceof Container) {
            return;
        }

        var drops = event.isDropItems();
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

        for (ItemStack stack : event.getBlock().getDrops(tool)) {
            Player player = event.getPlayer();
            this.getPlugin().getSubPlugin(PouchPlugin.class).getUtils().pickItem(player, stack);
            drops = false;
        }

        event.setDropItems(drops);
    }
}
