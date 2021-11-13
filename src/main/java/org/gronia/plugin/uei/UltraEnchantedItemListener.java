package org.gronia.plugin.uei;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemUtils;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.pouch.PouchPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UltraEnchantedItemListener extends SubListener<UltraEnchantedItemPlugin> {
    private final Map<Material, Material> cropList = Map.of(
            Material.WHEAT,
            Material.WHEAT_SEEDS,
            Material.POTATOES,
            Material.POTATO,
            Material.CARROTS,
            Material.CARROT,
            Material.BEETROOTS,
            Material.BEETROOT
    );

    private final ItemStack superHoe;
    private final ItemStack enchantedBakedPotato;

    public UltraEnchantedItemListener(UltraEnchantedItemPlugin plugin) {
        super(plugin);
        this.superHoe = ItemUtils.createSuperHoe();
        this.enchantedBakedPotato = ItemUtils.createEnchantedBakedPotato();
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

        if (this.getPlugin().enchantConfigs.containsKey(type)) {
            onEnchantItem(event);
            return;
        }
    }

    @EventHandler
    public void onEnchantedItemConsume(PlayerItemConsumeEvent event) {
        var type = event.getItem().getType();

        if (type == Material.BAKED_POTATO) {
            onBakedPotatoEat(event);
            return;
        }

        if (isNotConsumable(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().isSimilar(enchantedBakedPotato)) {
            event.setCancelled(true);
        }
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

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        var block = event.getBlock();
        final BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Ageable)) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.isSimilar(superHoe)) {
            return;
        }

        final Material material = block.getType();
        final Player player = event.getPlayer();
        if (!this.cropList.containsKey(material)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(this.getPlugin().getPlugin(), () -> {
            block.setType(material);
            player.getInventory().removeItem(new ItemStack(this.cropList.get(material)));
        }, 1L);
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

        if (!this.getPlugin().enchantConfigs.containsKey(item.getType())) {
            return;
        }

        var meta = item.getItemMeta();
        assert meta != null;

        var tierKey = this.getPlugin().<Gronia>getPlugin().getKey("tier");
        assert tierKey != null;

        var rawLevel = meta.getPersistentDataContainer().get(tierKey, PersistentDataType.INTEGER);
        if (rawLevel == null) {
            rawLevel = 0;
        }

        int level = rawLevel + 1;

        var current = this.getPlugin().enchantConfigs.get(item.getType()).get(level);
        if (current == null) {
            return;
        }

        var old = this.getPlugin().enchantConfigs.get(item.getType()).get(level - 1);

        int count = current.p2();
        var all = event.getPlayer().isSneaking();
        var required = ItemUtils.createItem(old.p1());
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

        var stack = ItemUtils.createItem(current.p1());
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

    private float clamp(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }

    private int clamp(int value, int min, int max) {
        return (int) this.clamp((float) value, min, max);
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


    private UltraEnchantedShapedRecipe getRecipe(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        var recipeName = getUltraEnchantedRecipeName(recipe.getResult());
        if (recipeName == null) {
            return null;
        }

        var originalRecipe = this.getPlugin().<Gronia>getPlugin().getOriginalRecipe(recipeName);

        if (!(originalRecipe instanceof UltraEnchantedShapedRecipe shapedRecipe)) {
            return null;
        }

        return shapedRecipe;
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
