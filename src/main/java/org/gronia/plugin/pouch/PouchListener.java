package org.gronia.plugin.pouch;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.SubListener;

import java.util.Objects;

public class PouchListener extends SubListener<PouchPlugin> {
    public PouchListener(PouchPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPouchClicked(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null) {
            return;
        }

        checkPouchClicked(itemStack, event.getPlayer(), event);
    }

    @EventHandler
    public void onPouchPlaced(BlockPlaceEvent event) {
        ItemStack stack = event.getItemInHand();
        checkPouchClicked(stack, event.getPlayer(), event);
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(this.getPlugin().INVENTORY_TITLE)) {
            return;
        }

        event.setCancelled(true);


        ItemStack current = event.getCurrentItem();
        if (current == null) {
            return;
        }

        if (current.equals(this.getPlugin().lockedAreaItem)) {
            return;
        }

        var player = event.getWhoClicked();

        if (current.equals(this.getPlugin().enderChestItem)) {
            player.openInventory(player.getEnderChest());
            return;
        }

        if (current.equals(this.getPlugin().craftingTable)) {
            player.openWorkbench(null, true);
            return;
        }

        if (current.equals(this.getPlugin().applyAllItem)) {
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
        var type = current.getType();

        if (isLeftClick) {
            this.getPlugin().getUtils().fillPlayer(player, type);
        } else {
            this.getPlugin().getUtils().emptyPlayer(player, type);
        }

        var meta = current.getItemMeta();
        assert meta != null;
        meta.setLore(this.getPlugin().getUtils().getLore(player, type));
        current.setItemMeta(meta);
    }

    private void checkPouchClicked(ItemStack stack, Player player, Cancellable event) {
        if (stack.getType() != Material.PLAYER_HEAD) {
            return;
        }

        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        if (!meta.getDisplayName().contains("Pouch")) {
            return;
        }

        event.setCancelled(true);
        this.getPlugin().getUtils().openPouch(player);
    }
}
