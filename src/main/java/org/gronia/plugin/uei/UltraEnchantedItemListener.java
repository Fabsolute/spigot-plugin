package org.gronia.plugin.uei;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.SubListener;

import java.util.List;

public class UltraEnchantedItemListener extends SubListener<UltraEnchantedItemPlugin> {
    public UltraEnchantedItemListener(UltraEnchantedItemPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!event.hasItem()) {
            return;
        }

        var item = event.getItem();
        assert item != null;

        if (item.getType() == Material.FIREWORK_ROCKET) {
            onFireworkRocketFired(event);
            return;
        }

        if (item.getType() == Material.NETHERITE_PICKAXE) {
            onSuperPickaxeRightClick(event);
            return;
        }
    }

    @EventHandler
    public void onEnchantedItemConsume(PlayerItemConsumeEvent event) {
        var type = event.getItem().getType();

        if (type == Material.BAKED_POTATO) {
            onBakedPotatoEat(event);
//            return;
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (isEnchanted(event.getItem())) {
            event.setCancelled(true);
        }
    }

    private void onFireworkRocketFired(PlayerInteractEvent event) {
        if (event.hasBlock()) {
            event.setCancelled(true);
            return;
        }

        var item = event.getItem();
        assert item != null;

        if (item.getType() != Material.FIREWORK_ROCKET) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        if (!meta.hasEnchant(Enchantment.LURE)) {
            return;
        }

        var player = event.getPlayer();
        if (player.isGliding() || event.hasBlock()) {
            if (item.getAmount() == 1) {
                item.setAmount(2);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.getPlugin().getPlugin(), () -> {
                    if (item.getAmount() != 1) {
                        item.setAmount(1);
                    }
                }, 2);
                return;
            }

            player.sendMessage("§cYou can not use more than one enchanted firework at once!");
        }

        event.setCancelled(true);
    }

    private void onSuperPickaxeRightClick(PlayerInteractEvent event) {
        if (event.hasBlock()) {
            return;
        }

        var item = event.getItem();
        assert item != null;

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        if (!meta.hasEnchant(Enchantment.LURE)) {
            return;
        }

        var isSilkTouch = false;

        if (meta.hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
            meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
            meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
            isSilkTouch = true;
        } else {
            meta.removeEnchant(Enchantment.SILK_TOUCH);
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 4, true);
        }

        meta.setLore(this.getLore(isSilkTouch));
        item.setItemMeta(meta);

        event.getPlayer().sendTitle(ChatColor.GREEN + (isSilkTouch ? "SILK TOUCH" : "FORTUNE"), ChatColor.GOLD + (isSilkTouch ? "Silk Touch" : "Fortune") + " activated.", 1, 20, 1);
    }

    private List<String> getLore(boolean isSilkTouch) {
        return List.of("", "§dMode: §c " + (isSilkTouch ? "Silk Touch" : "Fortune"));
    }

    private void onBakedPotatoEat(PlayerItemConsumeEvent event) {
        ItemMeta meta = event.getItem().getItemMeta();
        assert meta != null;
        if (!meta.hasEnchant(Enchantment.LURE)) {
            return;
        }

        Player player = event.getPlayer();
        player.setFoodLevel(this.clamp(player.getFoodLevel() + 10, 0, 20));
        player.setSaturation(this.clamp(player.getSaturation() + 12, 0f, (float) player.getFoodLevel()));
        player.setHealth(this.clamp((int) player.getHealth() + 2, 0, 20));
    }

    private boolean isEnchanted(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        if (stack.getType() != Material.BAKED_POTATO) {
            return false;
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return false;
        }

        return meta.hasEnchant(Enchantment.LURE);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }

    private int clamp(int value, int min, int max) {
        return (int) this.clamp((float) value, min, max);
    }
}
