package org.gronia.items.ingredient;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.gronia.items.ItemNames;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.uei.*;
import org.gronia.utils.pair.Pair;

import java.util.List;

public class FireworkRocket extends CustomItem implements TierableItem, EventListenerItem {
    private final int tier;

    private FireworkRocket(int tier, String internalName, String name) {
        super(Material.FIREWORK_ROCKET, internalName, name);
        this.tier = tier;
    }

    public static List<CustomItem> getAll() {
        return List.of(
                new Enchanted()
        );
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public boolean isPlaceable() {
        return false;
    }

    @Override
    public boolean isConsumable() {
        return false;
    }

    @Override
    public boolean isEnchanted() {
        return true;
    }

    private void onPlayerInteract(PlayerInteractEvent event) {
        var item = event.getItem();
        if (item == null) {
            return;
        }

        if (ItemRegistry.getCustomItem(item) != this) {
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(PlayerInteractEvent.class, this::onPlayerInteract)
        );
    }

    public static class Enchanted extends FireworkRocket implements CraftableItem<CustomShapelessRecipe> {
        public Enchanted() {
            super(1, ItemNames.ENCHANTED_FIREWORK_ROCKET, "Enchanted Firework Rocket");
        }

        @Override
        public void fillRecipe(CustomShapelessRecipe recipe) {
            recipe.addIngredient(64, Material.FIREWORK_ROCKET);
        }
    }
}

