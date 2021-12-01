package org.gronia.items.upgrader;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.items.ItemNames;
import org.gronia.items.ShulkerSack;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.sack.SackPlugin;
import org.gronia.plugin.uei.CustomItem;
import org.gronia.plugin.uei.CustomShapedRecipe;

import java.util.List;

public abstract class ShulkerSackUpgrader extends UpgraderBase {
    public ShulkerSackUpgrader(String internalName, String name) {
        super(internalName, name, "{SkullOwner:{Id:[I;1283138652,2026588190,-1123594741,2095845784],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFhOTEyZTMzMmZjMDAxMGJlYmQwZjkzYTE0ZDhlM2VhNjVkMTMwMTEwMGNlYTNmYzVhZTcxOTkwZDk4NTgwNyJ9fX0=\"}]}}}");
    }

    public static List<CustomItem> getAll() {
        return List.of(
                new Expander(),
                new EnderChest(),
                new CraftingTable(),
                new Storage()
        );
    }

    public boolean upgrade(ItemStack stack) {
        if (!(ItemRegistry.getCustomItem(stack) instanceof ShulkerSack shulkerSack)) {
            return false;
        }

        return upgrade(stack, shulkerSack);
    }

    public abstract boolean upgrade(ItemStack stack, ShulkerSack shulkerSack);

    public static class Expander extends ShulkerSackUpgrader {
        public Expander() {
            super(ItemNames.SACK_EXPAND_UPGRADE, "Sack Expand Upgrade");
        }

        public static int getSize(ItemStack stack) {
            if (!(ItemRegistry.getCustomItem(stack) instanceof ShulkerSack shulkerSack)) {
                return 0;
            }

            var meta = stack.getItemMeta();
            var size = meta.getPersistentDataContainer().get(shulkerSack.sizeKey, PersistentDataType.INTEGER);
            if (size == null) {
                size = 1;
            }

            return size;
        }

        @Override
        public boolean upgrade(ItemStack stack, ShulkerSack shulkerSack) {
            int size = getSize(stack) + 1;

            var meta = stack.getItemMeta();
            meta.lore(List.of(
                    Component.empty(),
                    Component.text("Size: ", NamedTextColor.LIGHT_PURPLE).append(Component.text(size, NamedTextColor.BLUE)),
                    Component.empty(),
                    Component.text("Per Item: " + size * Gronia.getInstance().getSubPlugin(SackPlugin.class).PER_COUNT, NamedTextColor.AQUA)
            ));
            meta.getPersistentDataContainer().set(shulkerSack.sizeKey, PersistentDataType.INTEGER, size);
            stack.setItemMeta(meta);
            return true;
        }

        @Override
        public void fillRecipe(CustomShapedRecipe recipe) {
            recipe.shape("CCC", "CUC", "CCC");
            recipe.setIngredient('C', ItemNames.SUPER_ENCHANTED_COBBLESTONE);
            recipe.setIngredient('U', ItemNames.UPGRADE_CRYSTAL);
        }
    }

    public static class EnderChest extends ShulkerSackUpgrader {
        public EnderChest() {
            super(ItemNames.SACK_ENDER_CHEST_UPGRADE, "Sack Ender Chest Upgrade");
        }

        @Override
        public boolean upgrade(ItemStack stack, ShulkerSack shulkerSack) {
            var meta = stack.getItemMeta();
            var container = meta.getPersistentDataContainer();
            if (container.has(shulkerSack.enderChestKey, PersistentDataType.INTEGER)) {
                return false;
            }

            container.set(shulkerSack.enderChestKey, PersistentDataType.INTEGER, 1);
            stack.setItemMeta(meta);

            return true;
        }

        @Override
        public void fillRecipe(CustomShapedRecipe recipe) {
            recipe.shape("CCC", "CUC", "CCC");
            recipe.setIngredient('C', Material.ENDER_CHEST);
            recipe.setIngredient('U', ItemNames.UPGRADE_CRYSTAL);
        }
    }

    public static class CraftingTable extends ShulkerSackUpgrader {
        public CraftingTable() {
            super(ItemNames.SACK_CRAFTING_TABLE_UPGRADE, "Sack Crafting Table Upgrade");
        }

        @Override
        public boolean upgrade(ItemStack stack, ShulkerSack shulkerSack) {
            var meta = stack.getItemMeta();
            var container = meta.getPersistentDataContainer();
            if (container.has(shulkerSack.craftingTableKey, PersistentDataType.INTEGER)) {
                return false;
            }

            container.set(shulkerSack.craftingTableKey, PersistentDataType.INTEGER, 1);
            stack.setItemMeta(meta);

            return true;
        }

        @Override
        public void fillRecipe(CustomShapedRecipe recipe) {
            recipe.shape("CCC", "CUC", "CCC");
            recipe.setIngredient('C', Material.CRAFTING_TABLE);
            recipe.setIngredient('U', ItemNames.UPGRADE_CRYSTAL);
        }
    }

    public static class Storage extends ShulkerSackUpgrader {
        public Storage() {
            super(ItemNames.SACK_STORAGE_UPGRADE, "Sack Storage Upgrade");
        }

        @Override
        public boolean upgrade(ItemStack stack, ShulkerSack shulkerSack) {
            var meta = stack.getItemMeta();
            var container = meta.getPersistentDataContainer();
            if (container.has(shulkerSack.storageKey, PersistentDataType.INTEGER)) {
                return false;
            }

            container.set(shulkerSack.storageKey, PersistentDataType.INTEGER, 1);
            stack.setItemMeta(meta);

            return true;
        }

        @Override
        public void fillRecipe(CustomShapedRecipe recipe) {
            recipe.shape("TTT", "TUT", "TTT");
            recipe.setIngredient('T', ItemNames.TELEPORTER);
            recipe.setIngredient('U', ItemNames.UPGRADE_CRYSTAL);
        }
    }
}