package org.gronia.plugin.items;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.fatigue.FatiguePlugin;
import org.gronia.plugin.uei.*;
import org.gronia.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class PiercerPickaxe extends CustomItem implements TierableItem, CraftableItem<CustomShapedRecipe>, EventListenerItem {
    private NamespacedKey piercerKey;

    public PiercerPickaxe() {
        super(Material.NETHERITE_PICKAXE, ItemNames.PIERCER_PICKAXE, "Piercer Pickaxe");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.piercerKey = Gronia.getInstance().getKey("piercer_mode");
    }

    @Override
    public boolean isShaped() {
        return true;
    }

    @Override
    public int getTier() {
        return 4;
    }

    @Override
    public void fillRecipe(CustomShapedRecipe recipe) {
        recipe.shape("CCC", " O ", " O ");
        recipe.setIngredient('C', ItemNames.SUPER_ENCHANTED_NETHERRACK);
        recipe.setIngredient('O', ItemNames.ULTRA_ENCHANTED_OBSIDIAN);
    }

    @Override
    public void onMetaCreate(ItemMeta meta) {
        super.onMetaCreate(meta);
        meta.addEnchant(Enchantment.DURABILITY, 4, true);
        if (piercerKey != null) {
            meta.getPersistentDataContainer().set(piercerKey, PersistentDataType.INTEGER, 0);
        }

        meta.setLore(this.getLore(0));
    }

    void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        var item = event.getItem();
        if (item == null || ItemRegistry.getCustomItem(item) != this) {
            return;
        }

        var meta = item.getItemMeta();
        assert meta != null;

        var mode = meta.getPersistentDataContainer().get(piercerKey, PersistentDataType.INTEGER);

        if (mode == null) {
            mode = 0;
        }

        mode = (mode + 1) % 3;
        meta.getPersistentDataContainer().set(piercerKey, PersistentDataType.INTEGER, mode);

        meta.setLore(this.getLore(mode));
        item.setItemMeta(meta);

        event.getPlayer().sendTitle(
                mode == 0 ? (ChatColor.GREEN + "TALL") : (mode == 1 ? (ChatColor.BLUE + "LONG") : (ChatColor.RED + "WIDE")),
                ChatColor.GOLD + (mode == 0 ? "Tall" : (mode == 1 ? "Long" : "Wide")) + " activated.",
                1,
                20,
                1);
    }

    private void onBlockBreak(BlockBreakEvent event) {
        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (ItemRegistry.getCustomItem(item) != this) {
            return;
        }

        if (player.hasMetadata("pierce.breaking")) {
            return;
        }

        var meta = item.getItemMeta();
        assert meta != null;

        var mode = meta.getPersistentDataContainer().get(piercerKey, PersistentDataType.INTEGER);

        if (mode == null) {
            mode = 0;
        }

        List<Block> others = new ArrayList<>();
        var block = event.getBlock();
        switch (mode) {
            case 0 -> {
                others.add(block.getRelative(BlockFace.DOWN));
                others.add(block.getRelative(BlockFace.UP));
            }
            case 1 -> {
                var direction = getCardinalDirection(player.getLocation().getYaw());
                var next = block.getRelative(direction);
                others.add(next);
                others.add(next.getRelative(direction));
            }
            case 2 -> {
                var direction = getCardinalDirection(player.getLocation().getYaw());
                if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
                    others.add(block.getRelative(BlockFace.EAST));
                    others.add(block.getRelative(BlockFace.WEST));
                } else {
                    others.add(block.getRelative(BlockFace.NORTH));
                    others.add(block.getRelative(BlockFace.SOUTH));
                }
            }
        }

        others = others.stream().filter(this::isSafeToBreak).toList();
        if (others.size() == 0) {
            return;
        }

        var fatigueUtil = Gronia.getInstance().getSubPlugin(FatiguePlugin.class).getUtil();
        var canBreak = fatigueUtil.canBreak(player, others.size());
        if (!canBreak) {
            return;
        }

        fatigueUtil.changeFatigue(player, -others.size());

        player.setMetadata("pierce.breaking", new FixedMetadataValue(Gronia.getInstance(), true));
        for (var b : others) {
            player.breakBlock(b);
        }
        Bukkit.getScheduler().runTaskLater(Gronia.getInstance(), () -> player.removeMetadata("pierce.breaking", Gronia.getInstance()), 1L);
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(PlayerInteractEvent.class, this::onRightClick),
                Pair.of(BlockBreakEvent.class, this::onBlockBreak)
        );
    }

    private List<String> getLore(int mode) {
        return List.of("", ChatColor.LIGHT_PURPLE + "Mode: " + ChatColor.RED + " " + (mode == 0 ? "Tall" : (mode == 1 ? "Long" : "Wide")));
    }

    private BlockFace getCardinalDirection(float yaw) {
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        yaw %= 360.0f;
        final double i = (yaw + 8.0f) / 18.0f;
        BlockFace direction;
        if (i >= 18.0 || i < 3.0) {
            direction = BlockFace.SOUTH;
        } else if (i < 8.0) {
            direction = BlockFace.WEST;
        } else if (i < 13.0) {
            direction = BlockFace.NORTH;
        } else {
            direction = BlockFace.EAST;
        }
        return direction;
    }

    private boolean isSafeToBreak(Block block) {
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
