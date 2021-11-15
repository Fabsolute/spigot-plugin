package org.gronia.plugin.uei;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.pouch.PouchPlugin;

import java.util.ArrayList;
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
        var type = item.getType();
        if (type == Material.FIREWORK_ROCKET) {
            onFireworkRocketFired(event);
            return;
        }

        if (type == Material.NETHERITE_PICKAXE) {
            onSuperPickaxeRightClick(event);
            return;
        }

        onEnchantItem(event);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        ItemRegistry.fireEvent(event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemRegistry.fireEvent(event);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        ItemRegistry.fireEvent(event);
    }

    @EventHandler
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        ItemRegistry.fireEvent(event);
    }

    @EventHandler
    public void onEnchantedItemConsume(PlayerItemConsumeEvent event) {
        ItemRegistry.fireEvent(event);
        if (isNotConsumable(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        ItemRegistry.fireEvent(event);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        var shapedRecipe = this.getRecipe(event.getRecipe());
        if (shapedRecipe == null) {
            return;
        }

        if (!shapedRecipe.match(event.getInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraftItem(PrepareItemCraftEvent event) {
        var shapedRecipe = this.getRecipe(event.getRecipe());
        if (shapedRecipe == null) {
            return;
        }

        if (!shapedRecipe.match(event.getInventory())) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (!isNotPlaceable(event.getItemInHand())) {
            return;
        }

        event.setCancelled(true);
    }

    public String getUltraEnchantedRecipeName(ItemStack result) {
        var meta = result.getItemMeta();
        assert meta != null;
        return meta.getPersistentDataContainer().get(this.getPlugin().<Gronia>getPlugin().recipeKey, PersistentDataType.STRING);
    }

    private void onEnchantItem(PlayerInteractEvent event) {
        var block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        if (block.getType() != Material.ENCHANTING_TABLE) {
            return;
        }

        var item = event.getItem();
        assert item != null;

        var recipe = this.getPlugin().<Gronia>getPlugin().getCustomShapelessRecipe(item);
        if (recipe == null) {
            return;
        }

        int count = recipe.getCount();
        var all = event.getPlayer().isSneaking();
        var required = ItemRegistry.createItem(recipe.getIngredient());
        var inventory = event.getPlayer().getInventory();
        var inventoryCount = getCount(inventory, required);

        if (inventoryCount < count) {
            return;
        }

        int applyCount = 1;
        if (all) {
            applyCount = Math.floorDiv(inventoryCount, count);
        }

        required.setAmount(count * applyCount);
        event.getPlayer().getInventory().removeItem(required);

        var stack = ItemRegistry.createItem(recipe.getResult());
        stack.setAmount(applyCount);
        this.getPlugin().getSubPlugin(PouchPlugin.class).getUtils().pickItem(event.getPlayer(), stack);

        event.setCancelled(true);
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

    private int getCount(PlayerInventory inventory, ItemStack stack) {
        int count = 0;

        List<ItemStack> output = new ArrayList<>();
        var meta = stack.getItemMeta();
        assert meta != null;
        for (var s : inventory.getStorageContents()) {
            if (s == null) {
                continue;
            }

            if (!s.isSimilar(stack)) {
                continue;
            }

            count += s.getAmount();
        }

        return count;
    }

    private CustomShapedRecipe getRecipe(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        var recipeName = getUltraEnchantedRecipeName(recipe.getResult());
        if (recipeName == null) {
            return null;
        }

        return this.getPlugin().<Gronia>getPlugin().getCustomShapedRecipe(recipeName);
    }

    private boolean isNotConsumable(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(this.getPlugin().<Gronia>getPlugin().getKey("not_consumable"), PersistentDataType.INTEGER);
    }

    private boolean isNotPlaceable(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(this.getPlugin().<Gronia>getPlugin().getKey("not_placeable"), PersistentDataType.INTEGER);
    }
}
