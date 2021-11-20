package org.gronia.plugin.sack;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubTabCompleter;
import org.gronia.plugin.SubUtilPlugin;

import java.util.Arrays;
import java.util.List;

public class SackPlugin extends SubUtilPlugin<SackPlugin, SackUtil> {
    public int MAX_COUNT = 1024;
    public final ItemStack lockedAreaItem;
    public final ItemStack enderChestItem;
    public final ItemStack applyAllItem;
    public final ItemStack craftingTable;

    public static final List<Material> sackableItems = Arrays.asList(
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

    public SackPlugin(JavaPlugin plugin) {
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
    public SackUtil getUtils() {
        return new SackUtil(this);
    }

    @Override
    public String getName() {
        return "sack";
    }

    @Override
    public SubListener<SackPlugin> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<SackPlugin> getExecutor() {
        return new SackCommand(this);
    }

    @Override
    public SubTabCompleter<SackPlugin> getTabCompleter() {
        return null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this.getPlugin(), this::saveConfig, 80, 80);
    }
}
