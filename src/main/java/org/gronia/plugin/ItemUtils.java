package org.gronia.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ItemUtils {
    public static final List<ChatColor> tierColors = List.of(
            ChatColor.WHITE,
            ChatColor.GREEN,
            ChatColor.BLUE,
            ChatColor.LIGHT_PURPLE,
            ChatColor.GOLD
    );

    private static final int HYPER_FURNACE_TIER = 3;

    private static final Map<String, Pair<String, Supplier<ItemStack>>> itemNames = new HashMap<>() {
        {
            put("hyper_furnace", Pair.of("Hyper Furnace", ItemUtils::createHyperFurnace));
            put("super_hoe", Pair.of("Super Hoe", ItemUtils::createSuperHoe));

            put("enchanted_baked_potato", Pair.of("Enchanted Baked Potato", ItemUtils::createEnchantedBakedPotato));

            put("enchanted_cobblestone", Pair.of("Enchanted Cobblestone", ItemUtils::createEnchantedCobblestone));
            put("extra_enchanted_cobblestone", Pair.of("Extra Enchanted Cobblestone", ItemUtils::createExtraEnchantedCobblestone));
            put("ultra_enchanted_cobblestone", Pair.of("Ultra Enchanted Cobblestone", ItemUtils::createUltraEnchantedCobblestone));
            put("super_enchanted_cobblestone", Pair.of("Super Enchanted Cobblestone", ItemUtils::createSuperEnchantedCobblestone));

            put("enchanted_carrot", Pair.of("Enchanted Carrot", ItemUtils::createEnchantedCarrot));
            put("extra_enchanted_carrot", Pair.of("Extra Enchanted Carrot", ItemUtils::createExtraEnchantedCarrot));
            put("ultra_enchanted_carrot", Pair.of("Ultra Enchanted Carrot", ItemUtils::createUltraEnchantedCarrot));
            put("super_enchanted_carrot", Pair.of("Super Enchanted Carrot", ItemUtils::createSuperEnchantedCarrot));
        }
    };

    public static String getHyperFurnaceName() {
        return getItemName("hyper_furnace", HYPER_FURNACE_TIER);
    }

    public static ItemStack createHyperFurnace() {
        return createTierItem(Material.FURNACE, HYPER_FURNACE_TIER, "hyper_furnace");
    }

    public static ItemStack createSuperHoe() {
        return createTierItem(Material.GOLDEN_HOE, 4, "super_hoe", meta -> meta.setUnbreakable(true));
    }

    public static ItemStack createEnchantedBakedPotato() {
        return createTierItem(Material.BAKED_POTATO, 1, "enchanted_baked_potato");
    }

    public static ItemStack createEnchantedCobblestone() {
        return createTierItem(Material.COBBLESTONE, 1, "enchanted_cobblestone", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createExtraEnchantedCobblestone() {
        return createTierItem(Material.COBBLESTONE, 2, "extra_enchanted_cobblestone", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createUltraEnchantedCobblestone() {
        return createTierItem(Material.COBBLESTONE, 3, "ultra_enchanted_cobblestone", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createSuperEnchantedCobblestone() {
        return createTierItem(Material.COBBLESTONE, 4, "super_enchanted_cobblestone", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createEnchantedCarrot() {
        return createTierItem(Material.CARROT, 1, "enchanted_carrot",meta-> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_consumable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createExtraEnchantedCarrot() {
        return createTierItem(Material.CARROT, 2, "extra_enchanted_carrot",meta-> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_consumable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createUltraEnchantedCarrot() {
        return createTierItem(Material.CARROT, 3, "ultra_enchanted_carrot",meta-> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_consumable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createSuperEnchantedCarrot() {
        return createTierItem(Material.CARROT, 4, "super_enchanted_carrot",meta-> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_consumable"), PersistentDataType.INTEGER, 1));
    }

    public static String getItemName(String internalName, int tier) {
        Bukkit.getLogger().log(Level.WARNING, internalName);
        return ItemUtils.tierColors.get(tier) + ItemUtils.itemNames.get(internalName).p1();
    }

    private static ItemStack createTierItem(Material type, int tier, String internalName) {
        return createTierItem(type, tier, internalName, null);
    }

    private static ItemStack createTierItem(Material type, int tier, String internalName, Consumer<ItemMeta> metaConsumer) {
        var stack = new ItemStack(type);
        var meta = stack.getItemMeta();

        assert meta != null;

        meta.addEnchant(Enchantment.LURE, tier, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(getItemName(internalName, tier));

        if (metaConsumer != null) {
            metaConsumer.accept(meta);
        }

        var container = meta.getPersistentDataContainer();
        container.set(Gronia.getInstance().getKey("tier"), PersistentDataType.INTEGER, tier);
        container.set(Gronia.getInstance().recipeKey, PersistentDataType.STRING, internalName);

        stack.setItemMeta(meta);

        return stack;
    }

    public static ItemStack createItem(String name) {
        var internal = itemNames.get(name);
        if (internal == null) {
            return new ItemStack(Material.valueOf(name.toUpperCase()));
        }

        return internal.p2().get();
    }
}
