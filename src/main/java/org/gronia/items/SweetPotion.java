package org.gronia.items;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.fatigue.FatiguePlugin;
import org.gronia.plugin.uei.*;
import org.gronia.utils.Pair;

import java.util.List;

public abstract class SweetPotion extends CustomItem implements TierableItem, CraftableItem<CustomShapedRecipe>, EventListenerItem {
    private final int tier;

    public SweetPotion(int tier, String internalName, String name) {
        super(Material.POTION, internalName, name);
        this.tier = tier;
    }

    @Override
    public boolean isConsumable() {
        return true;
    }

    @Override
    public boolean isEnchanted() {
        return true;
    }

    @Override
    public int getTier() {
        return this.tier;
    }

    @Override
    public boolean isShaped() {
        return true;
    }

    @Override
    public void onMetaCreate(ItemMeta meta) {
        super.onMetaCreate(meta);
        PotionMeta p = (PotionMeta) meta;
        p.setColor(getColor());
        meta.setLore(List.of(
                ChatColor.BLUE + "+" + getFatigueRefresh()
        ));
    }

    private void onConsume(PlayerItemConsumeEvent event) {
        var item = event.getItem();
        if (ItemRegistry.getCustomItem(item) != this) {
            return;
        }

        Gronia.getInstance().getSubPlugin(FatiguePlugin.class).getUtil().changeRestness(event.getPlayer(), this.getFatigueRefresh());
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(PlayerItemConsumeEvent.class, this::onConsume)
        );
    }

    abstract Color getColor();

    abstract int getFatigueRefresh();

    public static List<CustomItem> getAll() {
        return List.of(
                new Enchanted(),
                new ExtraEnchanted(),
                new UltraEnchanted(),
                new SuperEnchanted()
        );
    }

    public static class Enchanted extends SweetPotion {
        public Enchanted() {
            super(1, ItemNames.ENCHANTED_SWEET_POTION, "Enchanted Sweet Potion");
        }

        @Override
        public void fillRecipe(CustomShapedRecipe recipe) {
            recipe.shape("SSS", "SP ");
            recipe.setIngredient('P', Material.GLASS_BOTTLE);
            recipe.setIngredient('S', ItemNames.ENCHANTED_SWEET_BERRIES);
        }

        @Override
        Color getColor() {
            return Color.GREEN;
        }

        @Override
        int getFatigueRefresh() {
            return 20;
        }
    }

    public static class ExtraEnchanted extends SweetPotion {
        public ExtraEnchanted() {
            super(2, ItemNames.EXTRA_ENCHANTED_SWEET_POTION, "Extra Enchanted Sweet Potion");
        }

        @Override
        public void fillRecipe(CustomShapedRecipe recipe) {
            recipe.shape("SSS", "P  ");
            recipe.setIngredient('P', Material.GLASS_BOTTLE);
            recipe.setIngredient('S', ItemNames.EXTRA_ENCHANTED_SWEET_BERRIES);
        }

        @Override
        Color getColor() {
            return Color.BLUE;
        }

        @Override
        int getFatigueRefresh() {
            return 50;
        }
    }

    public static class UltraEnchanted extends SweetPotion {
        public UltraEnchanted() {
            super(3, ItemNames.ULTRA_ENCHANTED_SWEET_POTION, "Ultra Enchanted Sweet Potion");
        }

        @Override
        public void fillRecipe(CustomShapedRecipe recipe) {
            recipe.shape("SSP");
            recipe.setIngredient('P', Material.GLASS_BOTTLE);
            recipe.setIngredient('S', ItemNames.ULTRA_ENCHANTED_SWEET_BERRIES);
        }

        @Override
        Color getColor() {
            return Color.PURPLE;
        }

        @Override
        int getFatigueRefresh() {
            return 120;
        }
    }

    public static class SuperEnchanted extends SweetPotion {
        public SuperEnchanted() {
            super(4, ItemNames.SUPER_ENCHANTED_SWEET_POTION, "Super Enchanted Sweet Potion");
        }

        @Override
        public void fillRecipe(CustomShapedRecipe recipe) {
            recipe.shape("SP ");
            recipe.setIngredient('P', Material.GLASS_BOTTLE);
            recipe.setIngredient('S', ItemNames.SUPER_ENCHANTED_SWEET_BERRIES);
        }

        @Override
        Color getColor() {
            return Color.YELLOW;
        }

        @Override
        int getFatigueRefresh() {
            return 225;
        }
    }
}
