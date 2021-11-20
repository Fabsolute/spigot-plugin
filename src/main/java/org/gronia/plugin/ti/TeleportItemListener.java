package org.gronia.plugin.ti;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.sack.SackPlugin;

public class TeleportItemListener extends SubListener<TeleportItemPlugin> {
    public TeleportItemListener(TeleportItemPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var xp = event.getExpToDrop();
        event.setExpToDrop(0);
        giveExp(event.getPlayer(), xp);
    }

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        for (var item : event.getItems()) {
            this.getPlugin().getSubPlugin(SackPlugin.class).getUtils().pickItem(player, item.getItemStack());
        }
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        var xp = event.getDroppedExp();
        var killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        event.setDroppedExp(0);
        giveExp(killer, xp);
        for (var drop : event.getDrops()) {
            this.getPlugin().getSubPlugin(SackPlugin.class).getUtils().pickItem(killer, drop);
        }

        event.getDrops().clear();
    }

    public void giveExp(Player player, int xp) {
        player.giveExp(xp, true);
    }
}
