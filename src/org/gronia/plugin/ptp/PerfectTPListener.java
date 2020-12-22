package org.gronia.plugin.ptp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.gronia.plugin.SubListener;

public class PerfectTPListener extends SubListener<PerfectTPPlugin> {
    public PerfectTPListener(PerfectTPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
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

        Player otherPlayer = this.getPlugin().getServer().getPlayer(tpName);
        if (otherPlayer != null) {
            location = otherPlayer.getLocation();
        } else {
            location = this.getPlugin().getConfig().getConfigurationSection(tpName).getLocation("location", null);
        }

        if (location != null) {
            event.getWhoClicked().teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        event.getView().close();
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() == Material.CHORUS_FRUIT) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String name = meta.getDisplayName();
                if (name.contains("[TP]")) {
                    Inventory inventory = this.getPlugin().getServer().createInventory(null, 54, "[Teleport]");
                    for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {
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

                    for (String locationName : this.getPlugin().getConfig().getKeys(false)) {
                        ConfigurationSection section = this.getPlugin().getConfig().getConfigurationSection(locationName);
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
                    return;
                }
                event.setCancelled(true);
            }
        }
    }
}
