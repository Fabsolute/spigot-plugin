package org.gronia.plugin.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.uei.*;
import org.gronia.utils.Pair;

import java.util.List;
import java.util.Map;

public class SuperHoe extends CustomItem implements TierableItem, CraftableItem<CustomShapedRecipe>, EventListenerItem {
    private final Map<Material, Material> cropList = Map.of(
            Material.WHEAT,
            Material.WHEAT_SEEDS,
            Material.POTATOES,
            Material.POTATO,
            Material.CARROTS,
            Material.CARROT,
            Material.BEETROOTS,
            Material.BEETROOT
    );

    public SuperHoe() {
        super(Material.GOLDEN_HOE, ItemNames.SUPER_HOE, "Super Hoe");
    }

    @Override
    public int getTier() {
        return 4;
    }

    @Override
    public void fillRecipe(CustomShapedRecipe recipe) {
        recipe.shape("OO ", " C ", " C ");
        recipe.setIngredient('O', ItemNames.SUPER_ENCHANTED_COBBLESTONE);
        recipe.setIngredient('C', ItemNames.SUPER_ENCHANTED_CARROT);
    }

    @Override
    public boolean isShaped() {
        return true;
    }

    @Override
    public void onMetaCreate(ItemMeta meta) {
        super.onMetaCreate(meta);
        meta.setUnbreakable(true);
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(BlockBreakEvent.class, this::onBlockBreak)
        );
    }

    public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        final BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Ageable ageable)) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (ItemRegistry.getCustomItem(item) != this) {
            return;
        }

        final Material material = block.getType();
        final Player player = event.getPlayer();
        if (!this.cropList.containsKey(material)) {
            return;
        }

        if (ageable.getAge() != ageable.getMaximumAge()) {
            event.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTaskLater(Gronia.getInstance(), () -> {
            block.setType(material);
            player.getInventory().removeItem(new ItemStack(this.cropList.get(material)));
        }, 1L);
    }
}
