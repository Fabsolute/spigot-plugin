package org.gronia.plugin.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.sack.SackPlugin;
import org.gronia.plugin.uei.*;
import org.gronia.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShulkerSack extends CustomItem implements CraftableItem<CustomShapedRecipe>, TierableItem, EventListenerItem {
    private final Map<HumanEntity, Inventory> inventoryMap = new HashMap<>();

    public ShulkerSack() {
        super(Material.PLAYER_HEAD, ItemNames.SHULKER_SACK, "Shulker Sack");
    }

    @Override
    public void fillRecipe(CustomShapedRecipe recipe) {
        recipe.shape("CCC", "CSC", "CCC");
        recipe.setIngredient('C', ItemNames.SUPER_ENCHANTED_COBBLESTONE);
        recipe.setIngredient('S', ItemNames.ULTRA_ENCHANTED_OBSIDIAN);
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
                Pair.of(BlockPlaceEvent.class, this::onSackPlaced),
                Pair.of(InventoryClickEvent.class, this::onItemClick)
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

    void onItemClick(InventoryClickEvent event) {
        var plugin = Gronia.getInstance().getSubPlugin(SackPlugin.class);
        if (event.getInventory() != this.inventoryMap.get(event.getWhoClicked())) {
            return;
        }

        event.setCancelled(true);


        ItemStack current = event.getCurrentItem();
        if (current == null) {
            return;
        }

        if (current.equals(plugin.lockedAreaItem)) {
            return;
        }

        var player = event.getWhoClicked();

        if (current.equals(plugin.enderChestItem)) {
            player.openInventory(player.getEnderChest());
            return;
        }

        if (current.equals(plugin.craftingTable)) {
            player.openWorkbench(null, true);
            return;
        }

        if (current.equals(plugin.applyAllItem)) {
            var inventory = event.getView().getTopInventory();

            for (int j = 0; j < 5; j++) {
                for (int i = 0; i < 5; i++) {
                    clickItem(
                            player,
                            Objects.requireNonNull(inventory.getItem(j * 9 + (i + 2))),
                            event.isLeftClick()
                    );
                }
            }

            return;
        }

        if (event.getView().getTopInventory() != event.getClickedInventory()) {
            return;
        }

        clickItem(player, current, event.isLeftClick());
    }

    private void clickItem(HumanEntity player, ItemStack current, boolean isLeftClick) {
        var plugin = Gronia.getInstance().getSubPlugin(SackPlugin.class);
        var type = current.getType();

        if (isLeftClick) {
            plugin.getUtils().fillPlayer(player, type);
        } else {
            plugin.getUtils().emptyPlayer(player, type);
        }

        var meta = current.getItemMeta();
        assert meta != null;
        meta.setLore(plugin.getUtils().getLore(player, type));
        current.setItemMeta(meta);
    }

    private void checkSackClicked(ItemStack stack, Player player, Cancellable event) {
        if (ItemRegistry.getCustomItem(stack) != this) {
            return;
        }

        event.setCancelled(true);

        if (!inventoryMap.containsKey(player)) {
            inventoryMap.put(player, Bukkit.createInventory(player, 54));
        }

        Gronia.getInstance().getSubPlugin(SackPlugin.class).getUtils().openSack(player, inventoryMap.get(player));
    }
}