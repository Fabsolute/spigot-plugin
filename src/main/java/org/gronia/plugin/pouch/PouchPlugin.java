package org.gronia.plugin.pouch;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubTabCompleter;
import org.gronia.plugin.SubUtilPlugin;

import java.util.Arrays;
import java.util.List;

public class PouchPlugin extends SubUtilPlugin<PouchPlugin, PouchUtil> {
    public int MAX_COUNT;
    public final String INVENTORY_TITLE = "[Pouch]";
    public final ItemStack lockedAreaItem;
    public final ItemStack enderChestItem;
    public final ItemStack applyAllItem;
    public final ItemStack craftingTable;

    public static final List<Material> pouchableItems = Arrays.asList(
            Material.TUFF,
            Material.COBBLED_DEEPSLATE,
            Material.DIORITE,
            Material.ANDESITE,
            Material.GRANITE,

            Material.COBBLESTONE,
            Material.MAGMA_BLOCK,
            Material.BASALT,
            Material.NETHER_BRICKS,
            Material.NETHERRACK,

            Material.GRAVEL,
            Material.DIRT,
            Material.SAND,
            Material.SOUL_SOIL,
            Material.SOUL_SAND,

            Material.FLINT,
            Material.RAW_IRON,
            Material.RAW_COPPER,
            Material.RAW_GOLD,
            Material.QUARTZ,

            Material.LAPIS_LAZULI,
            Material.COAL,
            Material.DIAMOND,
            Material.EMERALD,
            Material.REDSTONE
    );

    public PouchPlugin(JavaPlugin plugin) {
        super(plugin);

        lockedAreaItem = this.getUtils().createEmptyItem(Material.BLACK_STAINED_GLASS_PANE);
        enderChestItem = this.getUtils().createEmptyItem(Material.ENDER_CHEST);
        craftingTable = this.getUtils().createEmptyItem(Material.CRAFTING_TABLE);
        applyAllItem = this.getUtils().createSkullItem(
                "Apply All",
                "{SkullOwner:{Id:[I;1645403077,-1355199707,-1482328559,-500862078],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWZkOGJlZDJkZmU0YzMyMTY4Yzk3MjE1NGVlYTMzNWE4MDQyZTlkNjRiODUwNzY3YzZlYTA0Y2U4Zjg1ZjEyYSJ9fX0=\"}]}}}",
                this.getUtils().getLore(null, null)
        );
    }

    @Override
    public PouchUtil getUtils() {
        return new PouchUtil(this);
    }

    @Override
    public String getName() {
        return "pouch";
    }

    @Override
    public SubListener<PouchPlugin> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<PouchPlugin> getExecutor() {
        return new PouchCommand(this);
    }

    @Override
    public SubTabCompleter<PouchPlugin> getTabCompleter() {
        return null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        MAX_COUNT = this.getConfig().getInt("MAX_COUNT", 20480);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this.getPlugin(), this::saveConfig, 80, 80);
    }
}
