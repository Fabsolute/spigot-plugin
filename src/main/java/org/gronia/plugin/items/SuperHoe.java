package org.gronia.plugin.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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
            Material.BEETROOT,
            Material.SWEET_BERRY_BUSH,
            Material.SWEET_BERRIES
    );

    public SuperHoe() {
        super(Material.NETHERITE_HOE, ItemNames.SUPER_HOE, "Super Hoe");
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
        meta.addEnchant(Enchantment.DURABILITY, 4, true);
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(BlockBreakEvent.class, this::onBlockBreak)
        );
    }

    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (ItemRegistry.getCustomItem(item) != this) {
            return;
        }

        var block = event.getBlock();
        final BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Ageable ageable)) {
            event.setCancelled(true);
            return;
        }

        if (ageable.getAge() != ageable.getMaximumAge()) {
            event.setCancelled(true);
            return;
        }

        final Material material = block.getType();
        if (!this.cropList.containsKey(material)) {
            event.setCancelled(true);
            return;
        }

        boolean shouldDamage = (Math.random() < (1f / (item.getEnchantmentLevel(Enchantment.DURABILITY) * 2 + 1)));
        if (shouldDamage) {
            var meta = (Damageable) item.getItemMeta();
            assert meta != null;
            meta.setDamage(meta.getDamage() + 1);
            item.setItemMeta(meta);
        }

        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(Gronia.getInstance(), () -> {
            block.setType(material);
            player.getInventory().removeItem(new ItemStack(this.cropList.get(material)));
        }, 1L);
    }
}
