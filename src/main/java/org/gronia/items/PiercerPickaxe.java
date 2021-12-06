package org.gronia.items;

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
import org.bukkit.persistence.PersistentDataType;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.fatigue.FatiguePlugin;
import org.gronia.plugin.uei.*;
import org.gronia.utils.ItemUtil;
import org.gronia.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class PiercerPickaxe extends CustomItem implements TierableItem, CraftableItem<CustomShapedRecipe>, EventListenerItem {
    private NamespacedKey piercerKey;
    private final List<Block> blocks = new ArrayList<>();

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

        if (blocks.contains(event.getBlock())) {
            blocks.remove(event.getBlock());
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

        others = others.stream().filter(ItemUtil::isSafeToBreak).toList();
        if (others.size() == 0) {
            return;
        }

        var fatigueUtil = Gronia.getInstance().getSubPlugin(FatiguePlugin.class).getUtil();
        var canBreak = fatigueUtil.canBreak(player, others.size());
        if (!canBreak) {
            return;
        }

        fatigueUtil.changeRestness(player, -others.size());
        blocks.addAll(others);

        for (var b : others) {
            player.breakBlock(b);
        }
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
}
