package org.gronia.plugin.ti;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.pouch.PouchPlugin;

public class TeleportItemListener extends SubListener<TeleportItemPlugin> {
    public TeleportItemListener(TeleportItemPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockDropItemEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        for (var item : event.getItems()) {
            this.getPlugin().getSubPlugin(PouchPlugin.class).getUtils().pickItem(player, item.getItemStack());
        }
    }
}
