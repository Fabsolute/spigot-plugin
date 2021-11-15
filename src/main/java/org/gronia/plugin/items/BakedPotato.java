package org.gronia.plugin.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.uei.*;
import org.gronia.utils.Pair;

import java.util.List;

public class BakedPotato extends CustomItem implements TierableItem {
    private final int tier;

    private BakedPotato(int tier, String internalName, String name) {
        super(Material.BAKED_POTATO, internalName, name);
        this.tier = tier;
    }

    public static List<CustomItem> getAll() {
        return List.of(new Enchanted());
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public boolean isEnchanted() {
        return true;
    }

    public static class Enchanted extends BakedPotato implements CraftableItem<CustomShapelessRecipe>, EventListenerItem {
        public Enchanted() {
            super(1, ItemNames.ENCHANTED_BAKED_POTATO, "Enchanted Baked Potato");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(4, Material.BAKED_POTATO);
        }

        void onEat(PlayerItemConsumeEvent event) {
            if (ItemRegistry.getCustomItem(event.getItem()) != this) {
                return;
            }

            Player player = event.getPlayer();
            player.setFoodLevel(this.clamp(player.getFoodLevel() + 10, 0, 20));
            player.setSaturation(this.clamp(player.getSaturation() + 12, 0f, (float) player.getFoodLevel()));
            player.setHealth(this.clamp((int) player.getHealth() + 2, 0, 20));
        }

        void onFoodLevelChange(FoodLevelChangeEvent event) {
            if (!(event.getEntity() instanceof Player)) {
                return;
            }

            if (event.getItem() == null) {
                return;
            }

            if (ItemRegistry.getCustomItem(event.getItem()) == this) {
                event.setCancelled(true);
            }
        }

        @Override
        public List<Pair<? extends Event>> getEventConsumers() {
            return List.of(
                    Pair.of(PlayerItemConsumeEvent.class, this::onEat),
                    Pair.of(FoodLevelChangeEvent.class, this::onFoodLevelChange)
            );
        }

        private float clamp(float value, float min, float max) {
            return Math.max(Math.min(value, max), min);
        }

        private int clamp(int value, int min, int max) {
            return (int) this.clamp((float) value, min, max);
        }
    }

}
