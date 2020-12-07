package org.gronia.plugin.ptp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.SubListener;

public class PerfectTPListener extends SubListener<PerfectTPPlugin> {
    public PerfectTPListener(PerfectTPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() == Material.CHORUS_FRUIT) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String name = meta.getDisplayName();
                if (name.contains("[TP] ")) {
                    String tpName = name.replace("[TP] ", "");
                    Location location = null;

                    Player otherPlayer = this.getPlugin().getServer().getPlayer(tpName);
                    if (otherPlayer != null) {
                        location = otherPlayer.getLocation();
                    } else {
                        location = this.getPlugin().getConfig().getLocation(tpName, null);
                    }

                    if (location != null) {
                        event.getPlayer().teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        return;
                    }
                }
                event.setCancelled(true);
            }
        }
    }
}
