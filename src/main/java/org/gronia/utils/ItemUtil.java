package org.gronia.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public class ItemUtil {
    public static boolean isSafeToBreak(Block block) {
        if (block.isLiquid()) {
            return false;
        }

        if (!block.getType().isSolid()) {
            return false;
        }

        final var notAllowedList = List.of(
                Material.DISPENSER,
                Material.SPAWNER,
                Material.CHEST,
                Material.FURNACE,
                Material.JUKEBOX,
                Material.ENDER_CHEST,
                Material.BEACON,
                Material.TRAPPED_CHEST,
                Material.HOPPER,
                Material.DROPPER,
                Material.BREWING_STAND,
                Material.ANVIL,
                Material.NOTE_BLOCK,
                Material.CRAFTING_TABLE,
                Material.LEVER,
                Material.REPEATER,
                Material.ENCHANTING_TABLE,
                Material.COMPARATOR,
                Material.DAYLIGHT_DETECTOR,
                Material.OBSERVER,
                Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK,
                Material.IRON_DOOR, Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR, Material.OAK_DOOR, Material.SPRUCE_DOOR,
                Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE,
                Material.IRON_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR,
                Material.WHITE_BED, Material.ORANGE_BED, Material.MAGENTA_BED, Material.LIGHT_BLUE_BED, Material.YELLOW_BED, Material.LIME_BED, Material.PINK_BED, Material.GRAY_BED, Material.LIGHT_GRAY_BED, Material.CYAN_BED, Material.PURPLE_BED, Material.BLUE_BED, Material.BROWN_BED, Material.GREEN_BED, Material.RED_BED, Material.BLACK_BED,
                Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.BLACK_SHULKER_BOX,
                Material.BARRIER, Material.BEDROCK, Material.BUBBLE_COLUMN, Material.DRAGON_BREATH, Material.DRAGON_EGG, Material.END_CRYSTAL, Material.END_GATEWAY, Material.END_PORTAL, Material.END_PORTAL_FRAME, Material.LAVA, Material.STRUCTURE_VOID, Material.STRUCTURE_BLOCK, Material.WATER, Material.PISTON_HEAD, Material.MOVING_PISTON, Material.AIR, Material.CAVE_AIR, Material.VOID_AIR
        );

        return !notAllowedList.contains(block.getType());
    }
}
