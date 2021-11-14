package org.gronia.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.utils.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemUtils {
    public static final List<ChatColor> tierColors = List.of(
            ChatColor.WHITE,
            ChatColor.GREEN,
            ChatColor.BLUE,
            ChatColor.LIGHT_PURPLE,
            ChatColor.GOLD
    );

    private static final Map<String, Pair<Material, Integer, String, Supplier<ItemStack>>> itemNames = new HashMap<>() {
        {
            put("hyper_furnace", Pair.of(Material.FURNACE, 3, "Hyper Furnace", ItemUtils::createHyperFurnace));
            put("super_hoe", Pair.of(Material.GOLDEN_HOE, 4, "Super Hoe", ItemUtils::createSuperHoe));

            put("nice_pickaxe", Pair.of(Material.GOLDEN_PICKAXE, 1, "Nice Pickaxe", ItemUtils::createNicePickaxe));
            put("ultra_pickaxe", Pair.of(Material.GOLDEN_PICKAXE, 2, "Ultra Pickaxe", ItemUtils::createUltraPickaxe));
            put("hyper_pickaxe", Pair.of(Material.GOLDEN_PICKAXE, 3, "Hyper Pickaxe", ItemUtils::createHyperPickaxe));
            put("super_pickaxe", Pair.of(Material.NETHERITE_PICKAXE, 4, "Super Pickaxe", ItemUtils::createSuperPickaxe));

            put("enchanted_baked_potato", Pair.of(Material.BAKED_POTATO, 1, "Enchanted Baked Potato", ItemUtils::createEnchantedBakedPotato));

            put("enchanted_cobblestone", Pair.of(Material.COBBLESTONE, 1, "Enchanted Cobblestone", ItemUtils::createEnchantedCobblestone));
            put("extra_enchanted_cobblestone", Pair.of(Material.COBBLESTONE, 2, "Extra Enchanted Cobblestone", ItemUtils::createExtraEnchantedCobblestone));
            put("ultra_enchanted_cobblestone", Pair.of(Material.COBBLESTONE, 3, "Ultra Enchanted Cobblestone", ItemUtils::createUltraEnchantedCobblestone));
            put("super_enchanted_cobblestone", Pair.of(Material.COBBLESTONE, 4, "Super Enchanted Cobblestone", ItemUtils::createSuperEnchantedCobblestone));

            put("enchanted_carrot", Pair.of(Material.CARROT, 1, "Enchanted Carrot", ItemUtils::createEnchantedCarrot));
            put("extra_enchanted_carrot", Pair.of(Material.CARROT, 2, "Extra Enchanted Carrot", ItemUtils::createExtraEnchantedCarrot));
            put("ultra_enchanted_carrot", Pair.of(Material.CARROT, 3, "Ultra Enchanted Carrot", ItemUtils::createUltraEnchantedCarrot));
            put("super_enchanted_carrot", Pair.of(Material.CARROT, 4, "Super Enchanted Carrot", ItemUtils::createSuperEnchantedCarrot));

            put("enchanted_obsidian", Pair.of(Material.OBSIDIAN, 1, "Enchanted Obsidian", ItemUtils::createEnchantedObsidian));
            put("extra_enchanted_obsidian", Pair.of(Material.OBSIDIAN, 2, "Extra Enchanted Obsidian", ItemUtils::createExtraEnchantedObsidian));
            put("ultra_enchanted_obsidian", Pair.of(Material.OBSIDIAN, 3, "Ultra Enchanted Obsidian", ItemUtils::createUltraEnchantedObsidian));
            put("super_enchanted_obsidian", Pair.of(Material.OBSIDIAN, 4, "Super Enchanted Obsidian", ItemUtils::createSuperEnchantedObsidian));
        }
    };

    public static String getHyperFurnaceName() {
        return getItemName("hyper_furnace");
    }

    public static ItemStack createHyperFurnace() {
        return createTierItem("hyper_furnace");
    }

    public static ItemStack createSuperHoe() {
        return createTierItem("super_hoe", meta -> meta.setUnbreakable(true));
    }

    public static ItemStack createEnchantedBakedPotato() {
        return createTierItem("enchanted_baked_potato");
    }

    public static ItemStack createNicePickaxe() {
        return createTierItem("nice_pickaxe", meta -> meta.setUnbreakable(true));
    }

    public static ItemStack createUltraPickaxe() {
        return createTierItem("ultra_pickaxe", meta -> {
            meta.addEnchant(Enchantment.DIG_SPEED,1,true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setUnbreakable(true);
        });
    }

    public static ItemStack createHyperPickaxe() {
        return createTierItem("hyper_pickaxe", meta -> {
            meta.addEnchant(Enchantment.DIG_SPEED,4,true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setUnbreakable(true);
        });
    }

    public static ItemStack createSuperPickaxe() {
        return createTierItem("super_pickaxe", meta -> {
            // todo mode
            meta.addEnchant(Enchantment.DIG_SPEED,6,true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setUnbreakable(true);
        });
    }

    public static ItemStack createEnchantedCobblestone() {
        return createTierItem("enchanted_cobblestone", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createExtraEnchantedCobblestone() {
        return createTierItem("extra_enchanted_cobblestone", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createUltraEnchantedCobblestone() {
        return createTierItem("ultra_enchanted_cobblestone", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createSuperEnchantedCobblestone() {
        return createTierItem("super_enchanted_cobblestone", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createEnchantedObsidian() {
        return createTierItem("enchanted_obsidian", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createExtraEnchantedObsidian() {
        return createTierItem("extra_enchanted_obsidian", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createUltraEnchantedObsidian() {
        return createTierItem("ultra_enchanted_obsidian", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createSuperEnchantedObsidian() {
        return createTierItem("super_enchanted_obsidian", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createEnchantedCarrot() {
        return createTierItem("enchanted_carrot", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_consumable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createExtraEnchantedCarrot() {
        return createTierItem("extra_enchanted_carrot", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_consumable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createUltraEnchantedCarrot() {
        return createTierItem("ultra_enchanted_carrot", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_consumable"), PersistentDataType.INTEGER, 1));
    }

    public static ItemStack createSuperEnchantedCarrot() {
        return createTierItem("super_enchanted_carrot", meta -> meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_consumable"), PersistentDataType.INTEGER, 1));
    }

    public static String getItemName(String internalName) {
        var pair = ItemUtils.itemNames.get(internalName);
        return ItemUtils.tierColors.get(pair.p2()) + pair.p3();
    }

    private static ItemStack createTierItem(String internalName) {
        return createTierItem(internalName, null);
    }

    private static ItemStack createTierItem(String internalName, Consumer<ItemMeta> metaConsumer) {
        var tier = itemNames.get(internalName).p2();
        var type = getMaterialFor(internalName);
        var stack = new ItemStack(type);
        var meta = stack.getItemMeta();

        assert meta != null;

        meta.addEnchant(Enchantment.LURE, tier, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(getItemName(internalName));

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
        name = name.toLowerCase();
        var internal = itemNames.get(name);
        if (internal == null) {
            return new ItemStack(Material.valueOf(name.toUpperCase()));
        }

        return internal.p4().get();
    }

    public static boolean isValidMaterialName(String name) {
        name = name.toLowerCase();
        if (itemNames.containsKey(name)) {
            return true;
        }

        try {
            Material.valueOf(name.toUpperCase());
            return true;
        } catch (IllegalArgumentException ignored) {
        }

        return false;
    }

    public static Material getMaterialFor(String name) {
        name = name.toLowerCase();
        if (itemNames.containsKey(name)) {
            return itemNames.get(name).p1();
        }

        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static String getInternalName(ItemStack stack) {
        var name = stack.getType().name().toLowerCase();
        var meta = stack.getItemMeta();
        if (meta != null) {
            var recipeName = meta.getPersistentDataContainer().get(Gronia.getInstance().recipeKey, PersistentDataType.STRING);
            if (recipeName != null) {
                name = recipeName;
            }
        }

        return name;
    }

    public static List<String> getItemNames() {
        var output = new ArrayList<>(Arrays.stream(Material.values()).map(m -> m.name().toLowerCase()).toList());
        output.addAll(itemNames.keySet());
        return output;
    }
}
