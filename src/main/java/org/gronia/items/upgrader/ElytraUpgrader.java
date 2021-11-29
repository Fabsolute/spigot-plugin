package org.gronia.items;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.uei.CustomShapedRecipe;

import java.util.UUID;

public class ElytraUpgrader extends UpgraderBase {
    public ElytraUpgrader() {
        super(ItemNames.ELYTRA_UPGRADER, "Elytra Upgrader", "{SkullOwner:{Id:[I;1908231727,445925176,-1123350085,-1083950265],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmVjODAzMTM0MDRhYWE2MGNiYWMxMDk4ZTNlMDVmMTA3YjlmNjY1MzQxMDNlOWI1OTRlOTIzMGVhMjI2YjVjZSJ9fX0=\"}]}}}");
    }

    public boolean upgrade(ItemStack itemStack) {
        if (itemStack.getType() != Material.ELYTRA) {
            return false;
        }

        var meta = itemStack.getItemMeta();
        assert meta != null;
        var container = meta.getPersistentDataContainer();
        var upgraderKey = Gronia.getInstance().getKey("elytra.upgrader");
        if (container.get(upgraderKey, PersistentDataType.INTEGER) != null) {
            return false;
        }

        container.set(upgraderKey, PersistentDataType.INTEGER, 1);

        meta.addEnchant(Enchantment.DURABILITY, 4, true);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        meta.addEnchant(Enchantment.THORNS, 3, true);
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "GENERIC_ARMOR", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "GENERIC_ARMOR_TOUGHNESS", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "GENERIC_KNOCKBACK_RESISTANCE", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        itemStack.setItemMeta(meta);

        return true;
    }

    @Override
    public void fillRecipe(CustomShapedRecipe recipe) {
        recipe.shape("CCC", "TOT", "CCC");
        recipe.setIngredient('C', ItemNames.ENCHANTED_OBSIDIAN);
        recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipe.setIngredient('O', Material.NETHERITE_CHESTPLATE);
    }
}