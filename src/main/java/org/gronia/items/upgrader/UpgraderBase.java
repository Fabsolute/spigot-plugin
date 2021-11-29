package org.gronia.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.uei.*;
import org.gronia.utils.Pair;
import org.gronia.utils.UpgraderMenu;

import java.util.List;

public abstract class UpgraderBase extends CustomItem implements TierableItem, CraftableItem<CustomShapedRecipe>, EventListenerItem {
    private final String data;

    public UpgraderBase(String internalName, String name, String data) {
        super(Material.PLAYER_HEAD, internalName, name);
        this.data = data;
    }

    public abstract boolean upgrade(ItemStack itemStack);

    @Override
    public boolean isShaped() {
        return true;
    }

    @Override
    public int getTier() {
        return 4;
    }

    @Override
    public boolean isPlaceable() {
        return false;
    }

    @Override
    public void beforeCreate(ItemStack stack) {
        super.beforeCreate(stack);
        Bukkit.getUnsafe().modifyItemStack(
                stack,
                data
        );
    }

    private void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null) {
            return;
        }

        checkClicked(itemStack, event.getPlayer(), event);
    }

    private void onPlaced(BlockPlaceEvent event) {
        ItemStack stack = event.getItemInHand();
        checkClicked(stack, event.getPlayer(), event);
    }

    private void checkClicked(ItemStack stack, Player player, Cancellable event) {
        if (ItemRegistry.getCustomItem(stack) != this) {
            return;
        }

        event.setCancelled(true);

        var menu = new UpgraderMenu(Gronia.getInstance(), stack, this.getName(), this);
        player.openInventory(menu.getInventory());
    }

    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(PlayerInteractEvent.class, this::onPlayerInteract),
                Pair.of(BlockPlaceEvent.class, this::onPlaced)
        );
    }
}
