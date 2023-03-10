package org.gronia.plugin.uei;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.plugin.Gronia;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomItem {
    private static final List<ChatColor> tierColors = List.of(
            ChatColor.WHITE,
            ChatColor.GREEN,
            ChatColor.BLUE,
            ChatColor.LIGHT_PURPLE,
            ChatColor.GOLD,
            ChatColor.AQUA
    );

    private final Material baseItem;
    private final String internalName;
    private final String name;

    public CustomItem(Material baseItem, String internalName, String name) {
        this.baseItem = baseItem;
        this.internalName = internalName;
        this.name = name;
    }

    public Material getBaseType() {
        return baseItem;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getName() {
        var tier = 0;
        if (this instanceof TierableItem tierableItem) {
            tier = tierableItem.getTier();
        }

        return tierColors.get(tier) + name;
    }

    public boolean isPlaceable() {
        return true;
    }

    public boolean isConsumable() {
        return true;
    }

    public boolean isEnchanted() {
        return false;
    }

    public void onMetaCreate(ItemMeta meta) {
        if (!isPlaceable()) {
            meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_placeable"), PersistentDataType.INTEGER, 1);
        }

        if (!isConsumable()) {
            meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("not_consumable"), PersistentDataType.INTEGER, 1);
        }
    }

    public void beforeCreate(ItemStack stack) {
    }

    public ItemStack create() {
        var tier = 0;
        if (this instanceof TierableItem tierableItem) {
            tier = tierableItem.getTier();
        }

        var type = this.getBaseType();
        var stack = new ItemStack(type);
        beforeCreate(stack);
        var meta = stack.getItemMeta();

        assert meta != null;

        if (isEnchanted()) {
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        meta.setDisplayName(this.getName());

        onMetaCreate(meta);

        var container = meta.getPersistentDataContainer();
        container.set(Gronia.getInstance().getKey("tier"), PersistentDataType.INTEGER, tier);
        container.set(Gronia.getInstance().recipeKey, PersistentDataType.STRING, internalName);

        stack.setItemMeta(meta);

        return stack;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public static boolean isBroken(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(Gronia.getInstance().getKey("broken"), PersistentDataType.INTEGER);
    }

    public static void setBroken(ItemStack item) {
        var meta = item.getItemMeta();
        assert meta != null;

        meta.getPersistentDataContainer().set(Gronia.getInstance().getKey("broken"), PersistentDataType.INTEGER, 1);
        var ll = meta.lore();
        if (ll == null) {
            ll = new ArrayList<>();
        }

        var l = new ArrayList<>(ll);
        l.add(0, Component.text("Broken", NamedTextColor.DARK_RED));
        meta.lore(l);

        item.setItemMeta(meta);
    }

    public static void setRepaired(ItemStack item) {
        var meta = item.getItemMeta();
        assert meta != null;

        meta.getPersistentDataContainer().remove(Gronia.getInstance().getKey("broken"));
        var ll = meta.lore();
        if (ll == null) {
            ll = new ArrayList<>();
        }

        var l = new ArrayList<>(ll);
        l.remove(0);
        meta.lore(l);

        item.setItemMeta(meta);
    }
}
