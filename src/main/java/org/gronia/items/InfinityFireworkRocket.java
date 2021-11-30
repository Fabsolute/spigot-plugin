package org.gronia.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.uei.*;
import org.gronia.utils.Pair;

import java.util.List;

public class InfinityFireworkRocket extends CustomItem implements TierableItem, EventListenerItem, CraftableItem<CustomShapedRecipe> {
    public InfinityFireworkRocket() {
        super(Material.FIREWORK_ROCKET, ItemNames.INFINITY_ROCKET, "Infinity Rocket");
    }

    @Override
    public int getTier() {
        return 4;
    }

    @Override
    public void fillRecipe(CustomShapedRecipe recipe) {
        recipe.shape("FFF", "FUF", "FFF");
        recipe.setIngredient('F', ItemNames.ENCHANTED_FIREWORK_ROCKET);
        recipe.setIngredient('U', ItemNames.UPGRADE_CRYSTAL);
    }

    @Override
    public boolean isEnchanted() {
        return true;
    }

    @Override
    public boolean isShaped() {
        return true;
    }

    private void onFireworkRocketFired(PlayerInteractEvent event) {
        var item = event.getItem();
        assert item != null;

        if (ItemRegistry.getCustomItem(item) != this) {
            return;
        }

        if (event.hasBlock()) {
            event.setCancelled(true);
            return;
        }

        var player = event.getPlayer();
        if (player.isGliding()) {
            if (item.getAmount() == 1) {
                item.setAmount(2);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Gronia.getInstance(), () -> {
                    if (item.getAmount() != 1) {
                        item.setAmount(1);
                    }
                }, 2);
                return;
            }

            player.sendMessage(ChatColor.RED + "You can not use more than one enchanted firework at once!");
        }

        event.setCancelled(true);
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(PlayerInteractEvent.class, this::onFireworkRocketFired)
        );
    }
}
