package org.gronia.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.List;

public class SubUtil<T extends SubPlugin<T>> {
    private final T plugin;

    public SubUtil(T plugin) {
        this.plugin = plugin;
    }

    public T getPlugin() {
        return plugin;
    }

    public ItemStack createSkullItem(String name, String data, List<String> lore) {
        var stack = new ItemStack(Material.PLAYER_HEAD);

        Bukkit.getUnsafe().modifyItemStack(
                stack,
                data
        );

        ItemMeta meta = stack.getItemMeta();
        assert meta != null;

        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(lore);
        }

        stack.setItemMeta(meta);

        return stack;
    }

    public void lookAt(Entity target, Entity source) {
        target.teleport(getLookAt(
                getEyeLocation(target),
                getEyeLocation(source)
        ));
    }

    public Location getLookAt(Location target, Location source) {
        Vector direction = target.toVector().subtract(source.toVector()).normalize();
        double x = direction.getX();
        double y = direction.getY();
        double z = direction.getZ();

        Location changed = target.clone();
        changed.setYaw(180 - toDegree(Math.atan2(x, z)));
        changed.setPitch(90 - toDegree(Math.acos(y)));
        return changed;
    }

    private float toDegree(double angle) {
        return (float) Math.toDegrees(angle);
    }

    private Location getEyeLocation(Entity entity) {
        if (entity instanceof Player)
            return ((Player) entity).getEyeLocation();
        else
            return entity.getLocation();
    }
}
