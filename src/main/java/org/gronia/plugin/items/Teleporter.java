package org.gronia.plugin.items;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.ptp.PerfectTPPlugin;
import org.gronia.plugin.uei.*;
import org.gronia.utils.Pair;

import java.util.List;

public class Teleporter extends CustomItem implements TierableItem, EventListenerItem, CraftableItem<CustomShapelessRecipe> {
    public Teleporter() {
        super(Material.CHORUS_FRUIT, ItemNames.TELEPORTER, "Teleporter");
    }

    @Override
    public boolean isEnchanted() {
        return true;
    }

    @Override
    public int getTier() {
        return 1;
    }

    private void onEat(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (ItemRegistry.getCustomItem(item) != this) {
            if (item.getType() == Material.CHORUS_FRUIT) {
                event.setCancelled(true);
            }

            return;
        }

        Inventory inventory = Gronia.getInstance().getServer().createInventory(null, 54, "[Teleport]");
        var players = Gronia.getInstance().getServer().getOnlinePlayers();
        for (Player player : players) {
            if (player == event.getPlayer()) {
                continue;
            }
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
            assert playerHeadMeta != null;
            playerHeadMeta.setOwningPlayer(player);
            playerHeadMeta.setDisplayName(player.getName());
            playerHead.setItemMeta(playerHeadMeta);
            inventory.addItem(playerHead);
        }

        for (String locationName : Gronia.getInstance().getSubPlugin(PerfectTPPlugin.class).getConfig().getKeys(false)) {
            ConfigurationSection section = Gronia.getInstance().getSubPlugin(PerfectTPPlugin.class).getConfig().getConfigurationSection(locationName);
            assert section != null;
            String itemName = section.getString("icon", "compass");
            assert itemName != null;
            ItemStack locationStack = new ItemStack(Material.valueOf(itemName.toUpperCase()));
            ItemMeta locationMeta = locationStack.getItemMeta();
            assert locationMeta != null;
            locationMeta.setDisplayName(locationName);
            locationStack.setItemMeta(locationMeta);
            inventory.addItem(locationStack);
        }

        event.getPlayer().openInventory(inventory);
    }

    private void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) {
            event.setCancelled(true);
        }
    }

    private void onItemClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("[Teleport]")) {
            return;
        }

        event.setCancelled(true);
        ItemStack current = event.getCurrentItem();
        if (current == null) {
            return;
        }
        ItemMeta meta = current.getItemMeta();
        assert meta != null;
        String tpName = meta.getDisplayName();
        Location location;

        Player otherPlayer = Gronia.getInstance().getServer().getPlayer(tpName);
        if (otherPlayer != null) {
            location = otherPlayer.getLocation();
        } else {
            var configuration = Gronia.getInstance().getSubPlugin(PerfectTPPlugin.class).getConfig().getConfigurationSection(tpName);
            location = configuration.getLocation("location");
        }

        if (location != null) {
            event.getWhoClicked().teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        event.getView().close();
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(PlayerItemConsumeEvent.class, this::onEat),
                Pair.of(PlayerTeleportEvent.class, this::onTeleport),
                Pair.of(InventoryClickEvent.class, this::onItemClick)
        );
    }

    @Override
    public void fillRecipe(CustomShapelessRecipe recipe) {
        recipe.addIngredient(1, Material.CHORUS_FRUIT);
    }
}
