package org.gronia.plugin.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.uei.CustomItem;

public class FireworkRocket /*extends CustomItem*/ {
    public FireworkRocket() {
//        super(Material.FIREWORK_ROCKET, ItemNames.INFINITY_ROCKET, name);
    }

//    private void onFireworkRocketFired(PlayerInteractEvent event) {
//        if (event.hasBlock()) {
//            event.setCancelled(true);
//            return;
//        }
//
//        var item = event.getItem();
//        assert item != null;
//
//        if (item.getType() != Material.FIREWORK_ROCKET) {
//            return;
//        }
//
//        ItemMeta meta = item.getItemMeta();
//        assert meta != null;
//        if (!meta.hasEnchant(Enchantment.LURE)) {
//            return;
//        }
//
//        var player = event.getPlayer();
//        if (player.isGliding() || event.hasBlock()) {
//            if (item.getAmount() == 1) {
//                item.setAmount(2);
//                Bukkit.getScheduler().scheduleSyncDelayedTask(this.getPlugin().getPlugin(), () -> {
//                    if (item.getAmount() != 1) {
//                        item.setAmount(1);
//                    }
//                }, 2);
//                return;
//            }
//
//            player.sendMessage("§cYou can not use more than one enchanted firework at once!");
//        }
//
//        event.setCancelled(true);
//    }
}
