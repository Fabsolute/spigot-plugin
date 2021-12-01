package org.gronia.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.sack.SackPlugin;
import org.gronia.plugin.uei.*;
import org.gronia.utils.Pair;

import java.util.List;

public class ShulkerSack extends CustomItem implements CraftableItem<CustomShapedRecipe>, TierableItem, EventListenerItem {
    public NamespacedKey sizeKey;
    public NamespacedKey enderChestKey;
    public NamespacedKey craftingTableKey;
    public NamespacedKey storageKey;

    public ShulkerSack() {
        super(Material.PLAYER_HEAD, ItemNames.SHULKER_SACK, "Shulker Sack");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        sizeKey = Gronia.getInstance().getKey("shulker_sack.size");
        enderChestKey = Gronia.getInstance().getKey("shulker_sack.ender_chest");
        craftingTableKey = Gronia.getInstance().getKey("shulker_sack.crafting_table");
        storageKey = Gronia.getInstance().getKey("shulker_sack.storage");
    }

    @Override
    public void fillRecipe(CustomShapedRecipe recipe) {
        recipe.shape(" C ", "CSC", " C ");
        recipe.setIngredient('C', ItemNames.SUPER_ENCHANTED_COBBLESTONE);
        recipe.setIngredient('S', ItemNames.EXTRA_ENCHANTED_OBSIDIAN);
    }

    @Override
    public boolean isShaped() {
        return true;
    }

    @Override
    public boolean isPlaceable() {
        return false;
    }

    @Override
    public int getTier() {
        return 4;
    }

    @Override
    public void beforeCreate(ItemStack stack) {
        super.beforeCreate(stack);
        Bukkit.getUnsafe().modifyItemStack(
                stack,
                "{SkullOwner:{Id:[I;1456257671,-733461130,-1195860888,754248132],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGQzN2Q0NzVjN2JiNjJkNTI5NjU3YWZjOGU3NjFjNjllOWIxMmNhMTRjMGE5MzlmZjY0MTZlYjI1ODkwNDc0YSJ9fX0=\"}]}}}"
        );
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(PlayerInteractEvent.class, this::onPlayerInteract),
                Pair.of(BlockPlaceEvent.class, this::onSackPlaced)
        );
    }

    private void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null) {
            return;
        }

        checkSackClicked(itemStack, event.getPlayer(), event);
    }

    private void onSackPlaced(BlockPlaceEvent event) {
        ItemStack stack = event.getItemInHand();
        checkSackClicked(stack, event.getPlayer(), event);
    }

    private void checkSackClicked(ItemStack stack, Player player, Cancellable event) {
        if (ItemRegistry.getCustomItem(stack) != this) {
            return;
        }

        event.setCancelled(true);

        Gronia.getInstance().getSubPlugin(SackPlugin.class).getUtils().openSack(player, stack);
    }
}
