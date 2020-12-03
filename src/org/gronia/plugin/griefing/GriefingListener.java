package org.gronia.plugin.griefing;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.gronia.plugin.SubListener;

import java.util.HashMap;
import java.util.Map;

// creeper
// tnt
// ghast
// zombie
// enderman
// sheep
// rabbit
// ravager
// snow-golem
// ender-dragon
// farm-land
// silverfish

public class GriefingListener extends SubListener<GriefingPlugin> {
    private final Map<EntityType, String> entityTypeSettingLookup = new HashMap<>();

    public GriefingListener(GriefingPlugin plugin) {
        super(plugin);

        entityTypeSettingLookup.put(EntityType.CREEPER, "creeper");
        entityTypeSettingLookup.put(EntityType.FIREBALL, "ghast");
        entityTypeSettingLookup.put(EntityType.WITHER_SKULL, "wither");
        entityTypeSettingLookup.put(EntityType.WITHER, "wither");
        entityTypeSettingLookup.put(EntityType.ENDER_DRAGON, "ender-dragon");
        entityTypeSettingLookup.put(EntityType.PRIMED_TNT, "tnt");
        entityTypeSettingLookup.put(EntityType.MINECART_TNT, "tnt");
        entityTypeSettingLookup.put(EntityType.SHEEP, "sheep");
        entityTypeSettingLookup.put(EntityType.RABBIT, "rabbit");
        entityTypeSettingLookup.put(EntityType.RAVAGER, "ravager");
        entityTypeSettingLookup.put(EntityType.ENDERMAN, "enderman");
        entityTypeSettingLookup.put(EntityType.SILVERFISH, "silverfish");
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        String key = this.entityTypeSettingLookup.get(event.getEntityType());
        if (key == null) {
            return;
        }


        if (cannot(key)) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onEntityDestroyEntity(EntityDamageByEntityEvent event) {
        if (!event.getEntityType().isAlive() || event.getEntityType() == EntityType.ARMOR_STAND) {
            String key = this.entityTypeSettingLookup.get(event.getEntityType());
            if (key == null) {
                return;
            }


            if (cannot(key)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        String key = this.entityTypeSettingLookup.get(event.getRemover().getType());
        if (key == null) {
            if (event.getRemover().getType() == EntityType.PLAYER) {
                if (cannot("tnt") && event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
                    event.setCancelled(true);
                }
            }
            return;
        }

        if (cannot(key)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onZombieDoorBreak(EntityBreakDoorEvent event) {
        if (cannot("zombie")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTurtleEggDestroy(EntityInteractEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE && cannot("zombie")) {
            if (event.getBlock().getType() == Material.TURTLE_EGG) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        String key = this.entityTypeSettingLookup.get(event.getEntityType());
        if (key == null) {
            if (event.getBlock().getType() == Material.FARMLAND && cannot("farm-land")) {
                event.setCancelled(true);
            }

            return;
        }

        if (cannot(key)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onSnowGolemStep(EntityBlockFormEvent event) {
        if (event.getEntity().getType() == EntityType.SNOWMAN && cannot("snow-golem")) {
            event.setCancelled(true);
        }
    }

    public boolean cannot(String type) {
        return !this.getPlugin().getConfig().getStringList("allowed").contains(type);
    }
}
