package org.gronia.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.uei.*;
import org.gronia.utils.pair.Pair;

import java.util.List;

public class SuperPickaxe extends CustomItem implements TierableItem, CraftableItem<CustomShapedRecipe>, EventListenerItem {
    public SuperPickaxe() {
        super(Material.NETHERITE_PICKAXE, ItemNames.SUPER_PICKAXE, "Super Pickaxe");
    }

    @Override
    public int getTier() {
        return 4;
    }

    @Override
    public boolean isShaped() {
        return true;
    }

    @Override
    public void fillRecipe(CustomShapedRecipe recipe) {
        recipe.shape("OOO", " C ", " C ");
        recipe.setIngredient('O', ItemNames.ENCHANTED_OBSIDIAN);
        recipe.setIngredient('C', ItemNames.SUPER_ENCHANTED_COBBLESTONE);
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(PlayerInteractEvent.class, this::onRightClick)
        );
    }

    @Override
    public void onMetaCreate(ItemMeta meta) {
        super.onMetaCreate(meta);
        meta.addEnchant(Enchantment.DURABILITY, 4, true);
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 4, true);
        meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        meta.setLore(this.getLore(false));
    }

    void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        var item = event.getItem();
        if (item == null || ItemRegistry.getCustomItem(item) != this) {
            return;
        }

        var meta = item.getItemMeta();
        assert meta != null;

        var isSilkTouch = false;

        if (meta.hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
            meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
            meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
            isSilkTouch = true;
        } else {
            meta.removeEnchant(Enchantment.SILK_TOUCH);
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 4, true);
        }

        meta.setLore(this.getLore(isSilkTouch));
        item.setItemMeta(meta);

        event.getPlayer().sendTitle(isSilkTouch ? (ChatColor.GREEN + "SILK TOUCH") : (ChatColor.BLUE + "FORTUNE"), ChatColor.GOLD + (isSilkTouch ? "Silk Touch" : "Fortune") + " activated.", 1, 20, 1);
    }

    private List<String> getLore(boolean isSilkTouch) {
        return List.of("", ChatColor.LIGHT_PURPLE + "Mode: " + ChatColor.RED + " " + (isSilkTouch ? "Silk Touch" : "Fortune"));
    }
}
