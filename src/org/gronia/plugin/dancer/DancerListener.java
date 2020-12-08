package org.gronia.plugin.dancer;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.gronia.plugin.SubListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class DancerListener extends SubListener<DancerPlugin> {
    private final NamespacedKey key;
    private final NamespacedKey key2;
    private final List<EulerAngle> headPoses = new ArrayList<>() {
        {
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(Math.toRadians(20), 0, 0));
            add(new EulerAngle(Math.toRadians(343), 0, 0));
            add(new EulerAngle(Math.toRadians(334), 0, 0));
            add(new EulerAngle(Math.toRadians(334), 0, 0));
            add(new EulerAngle(Math.toRadians(334), 0, 0));
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(0, 30, 0));
            add(new EulerAngle(Math.toRadians(346), Math.toRadians(357), Math.toRadians(354)));
            add(new EulerAngle(Math.toRadians(324), Math.toRadians(17), Math.toRadians(345)));
            add(new EulerAngle(0, 0, 0));
        }
    };
    private final List<EulerAngle> bodyPoses = new ArrayList<>() {
        {
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(0, 0, Math.toRadians(2)));
            add(new EulerAngle(0, 0, Math.toRadians(2)));
            add(new EulerAngle(0, 0, Math.toRadians(358)));
            add(new EulerAngle(0, 0, Math.toRadians(358)));
            add(new EulerAngle(0, 0, Math.toRadians(358)));
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(Math.toRadians(350), Math.toRadians(20), Math.toRadians(356)));
            add(new EulerAngle(0, 0, 0));
        }
    };
    private final List<EulerAngle> leftLegPoses = new ArrayList<>() {
        {
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(6, 0, 0));
            add(new EulerAngle(6, 0, 0));
            add(new EulerAngle(6, 0, 0));
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(0, 0, Math.toRadians(350)));
            add(new EulerAngle(Math.toRadians(10), 0, 0));
            add(new EulerAngle(Math.toRadians(236), Math.toRadians(46), Math.toRadians(336)));
            add(new EulerAngle(0, 0, 0));
        }
    };
    private final List<EulerAngle> rightLegPoses = new ArrayList<>() {
        {
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(Math.toRadians(350), 0, 0));
            add(new EulerAngle(Math.toRadians(350), 0, 0));
            add(new EulerAngle(Math.toRadians(350), 0, 0));
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(Math.toRadians(10), 0, 0));
            add(new EulerAngle(Math.toRadians(330), 0, 0));
            add(new EulerAngle(0, 0, Math.toRadians(345)));
            add(new EulerAngle(0, 0, 0));
        }
    };
    private final List<EulerAngle> leftArmPoses = new ArrayList<>() {
        {
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(Math.toRadians(313), Math.toRadians(15), 0));
            add(new EulerAngle(0, Math.toRadians(345), Math.toRadians(356)));
            add(new EulerAngle(0, Math.toRadians(345), Math.toRadians(356)));
            add(new EulerAngle(Math.toRadians(250), Math.toRadians(50), Math.toRadians(30)));
            add(new EulerAngle(Math.toRadians(250), Math.toRadians(310), Math.toRadians(330)));
            add(new EulerAngle(Math.toRadians(350), 0, 0));
            add(new EulerAngle(0, Math.toRadians(256), Math.toRadians(240)));
            add(new EulerAngle(Math.toRadians(245), 0, 0));
            add(new EulerAngle(Math.toRadians(254), Math.toRadians(245), Math.toRadians(346)));
            add(new EulerAngle(0, 0, Math.toRadians(345)));
        }
    };
    private final List<EulerAngle> rightArmPoses = new ArrayList<>() {
        {
            add(new EulerAngle(0, 0, 0));
            add(new EulerAngle(Math.toRadians(293), Math.toRadians(341), 0));
            add(new EulerAngle(Math.toRadians(302), Math.toRadians(13), Math.toRadians(13)));
            add(new EulerAngle(Math.toRadians(259), Math.toRadians(51), Math.toRadians(13)));
            add(new EulerAngle(Math.toRadians(250), Math.toRadians(310), Math.toRadians(330)));
            add(new EulerAngle(Math.toRadians(250), Math.toRadians(50), Math.toRadians(30)));
            add(new EulerAngle(Math.toRadians(304), Math.toRadians(343), Math.toRadians(324)));
            add(new EulerAngle(Math.toRadians(293), Math.toRadians(324), Math.toRadians(324)));
            add(new EulerAngle(Math.toRadians(245), 0, 0));
            add(new EulerAngle(Math.toRadians(251), Math.toRadians(115), Math.toRadians(15)));
            add(new EulerAngle(0, 0, Math.toRadians(15)));
        }
    };

    public DancerListener(DancerPlugin plugin) {
        super(plugin);
        key = new NamespacedKey(this.getPlugin().getPlugin(), "pose");
        key2 = new NamespacedKey(this.getPlugin().getPlugin(), "current");
    }

    @EventHandler
    public void onBlockRedstoneEvent(BlockPhysicsEvent event) {
        if (!this.getPlugin().getConfig().getBoolean("enabled", false)) {
            return;
        }

        Location location = event.getBlock().getLocation();
        Collection<Entity> entityList = location.getWorld().getNearbyEntities(location, 1, 1, 1);
        if (event.getBlock().getX() == -642 && event.getBlock().getY() == 63 && event.getBlock().getZ() == 118) {
            this.getPlugin().getLogger().log(Level.INFO, "AMK " + entityList);
        }

        entityList.forEach(entity -> {
            if (entity.getType() != EntityType.ARMOR_STAND) {
                return;
            }

            if (!isChanged(entity.getPersistentDataContainer(), event.getBlock().getBlockPower())) {
                return;
            }

            int animationCount = this.headPoses.size();
            int pose = this.getAndIncreasePose(entity.getPersistentDataContainer(), animationCount);


            ArmorStand armorStand = (ArmorStand) entity;
            armorStand.setArms(true);
            armorStand.setHeadPose(this.headPoses.get(pose));
            EulerAngle bodyPose = this.bodyPoses.get(pose);
            armorStand.setLeftLegPose(this.leftLegPoses.get(pose));
            armorStand.setRightLegPose(this.rightLegPoses.get(pose));
            armorStand.setLeftArmPose(this.leftArmPoses.get(pose));
            armorStand.setRightArmPose(this.rightArmPoses.get(pose));
        });
    }

    private int getAndIncreasePose(PersistentDataContainer container, int max) {
        int pose = container.getOrDefault(key, PersistentDataType.INTEGER, 0);
        container.set(key, PersistentDataType.INTEGER, (pose + 1) % max);
        return pose;
    }

    private boolean isChanged(PersistentDataContainer container, int current) {
        int old = container.getOrDefault(key2, PersistentDataType.INTEGER, -1);
        container.set(key2, PersistentDataType.INTEGER, current);
        return old != current;
    }
}
