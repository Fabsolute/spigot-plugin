package org.gronia.plugin.uei;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.sack.SackPlugin;

import java.util.ArrayList;
import java.util.List;

public class UltraEnchantedItemListener extends SubListener<UltraEnchantedItemPlugin> {
    public UltraEnchantedItemListener(UltraEnchantedItemPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemRegistry.fireEvent(event);
        onEnchantItem(event);
    }

    @EventHandler
    public void onPlayerBreakItem(PlayerItemBreakEvent event) {
        ItemRegistry.fireEvent(event);
        var item = event.getBrokenItem();
        if (ItemRegistry.getCustomItem(item) == null) {
            return;
        }

        if (!CustomItem.isBroken(item)) {
            CustomItem.setBroken(item);
        }

        event.getPlayer().getInventory().addItem(item);
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
        var item = event.getPlayer().getInventory().getItemInMainHand();
        if (ItemRegistry.getCustomItem(item) != null) {
            if (CustomItem.isBroken(item)) {
                event.setCancelled(true);
                return;
            }
        }

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
        ItemRegistry.fireEvent(event);

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
        if (item == null) {
            return;
        }

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
        this.getPlugin().getSubPlugin(SackPlugin.class).getUtils().pickItemToPlayer(event.getPlayer(), stack, true);

        event.setCancelled(true);
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
