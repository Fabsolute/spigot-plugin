package org.gronia.plugin.griefing;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.gronia.plugin.SubListener;

public class GriefingListener extends SubListener<GriefingPlugin> {
    public GriefingListener(GriefingPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (cannot(event.getEntityType())) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onEntityDestroyEntity(EntityDamageByEntityEvent event) {
        if (!event.getEntityType().isAlive() || event.getEntityType() == EntityType.ARMOR_STAND) {
            if (cannot(event.getEntityType())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        if (remover == null) {
            return;
        }

        if (cannot(remover.getType())) {
            event.setCancelled(true);
        } else {
            if (remover.getType() == EntityType.PLAYER) {
                if (cannot(EntityType.PRIMED_TNT) && event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onZombieDoorBreak(EntityBreakDoorEvent event) {
        if (cannot(EntityType.ZOMBIE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTurtleEggDestroy(EntityInteractEvent event) {
        if (cannot(event.getEntityType())) {
            if (event.getBlock().getType() == Material.TURTLE_EGG) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (cannot(event.getEntityType())) {
            event.setCancelled(true);
        } else {
            if (event.getBlock().getType() == Material.FARMLAND && cannot("farmland")) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onSnowGolemStep(EntityBlockFormEvent event) {
        if (cannot(event.getEntity().getType())) {
            event.setCancelled(true);
        }
    }

    public boolean cannot(EntityType type) {
        return this.cannot(this.getPlugin().entityTypeSettingLookup.getOrDefault(type, null));
    }

    public boolean cannot(String type) {
        if (type == null) {
            return false;
        }

        return !this.getPlugin().getConfig().getStringList("allowed").contains(type);
    }
}
