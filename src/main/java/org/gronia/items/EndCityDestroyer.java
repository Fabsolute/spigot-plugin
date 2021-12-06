package org.gronia.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.uei.*;
import org.gronia.utils.ItemUtil;
import org.gronia.utils.Pair;

import java.util.*;

public class EndCityDestroyer extends CustomItem implements TierableItem, CraftableItem<CustomShapedRecipe>, EventListenerItem {
    private int destroyer;
    private final Map<Player, Queue<Block>> pairs = new HashMap<>();
    private final List<Block> blocks = new ArrayList<>();

    public EndCityDestroyer() {
        super(Material.GOLDEN_PICKAXE, ItemNames.END_CITY_DESTROYER, "End City Destroyer");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.destroyer = Bukkit.getScheduler().scheduleSyncRepeatingTask(Gronia.getInstance(), this::destroy, 1, 1);
    }

    @Override
    public boolean isEnchanted() {
        return true;
    }

    @Override
    public void onMetaCreate(ItemMeta meta) {
        super.onMetaCreate(meta);
        meta.setUnbreakable(true);
    }

    private void destroy() {
        if (pairs.size() > 0) {
            for (var p : pairs.entrySet()) {
                var player = p.getKey();
                var blocks = p.getValue();
                if (blocks.size() > 0) {
                    var block = blocks.remove();
                    if (player.isOnline()) {
                        player.breakBlock(block);
                    }
                } else {
                    pairs.remove(player);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Bukkit.getScheduler().cancelTask(this.destroyer);
    }

    @Override
    public void fillRecipe(CustomShapedRecipe recipe) {
        recipe.shape("UUU", "UPU", "UUU");
        recipe.setIngredient('U', ItemNames.UPGRADE_CRYSTAL);
        recipe.setIngredient('P', ItemNames.PIERCER_PICKAXE);
    }

    @Override
    public boolean isShaped() {
        return true;
    }

    private void onBlockBreak(BlockBreakEvent event) {
        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (ItemRegistry.getCustomItem(item) != this) {
            return;
        }

        if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
            player.sendMessage(Component.text("This item only works in the end.", NamedTextColor.RED));
            event.setCancelled(true);
            return;
        }

        if (isAlreadyBreaking(player)) {
            if (this.blocks.contains(event.getBlock())) {
                this.blocks.remove(event.getBlock());
                return;
            }

            player.sendMessage(Component.text("You are already breaking.", NamedTextColor.RED));
            event.setCancelled(true);
            return;
        }

        if (event.getBlock().getType() != Material.PURPUR_BLOCK) {
            player.sendMessage(Component.text("The first item should be purpur_block", NamedTextColor.RED));
            event.setCancelled(true);
            return;
        }

        if (isShulkerNearBy(player)) {
            player.sendMessage(Component.text("You cannot use this item near by shulker", NamedTextColor.RED));

            event.setCancelled(true);
            return;
        }

        Queue<Block> others = new LinkedList<>();
        var block = event.getBlock();

        addNearBlocks(others, block);

        if (others.size() == 0) {
            player.sendMessage(Component.text("There is no neighbour block", NamedTextColor.RED));
            event.setCancelled(true);
            return;
        }

        player.sendMessage(Component.text("Destroying city. Block size: " + others.size(), NamedTextColor.GREEN));
        pairs.put(player, others);
        blocks.addAll(others);
    }

    private boolean isShulkerNearBy(Player player) {
        for (var ent : player.getNearbyEntities(100, 100, 100)) {
            if (ent.getType() == EntityType.SHULKER) {
                return true;
            }
        }

        return false;
    }

    private void addNearBlocks(Queue<Block> others, Block block) {
        var up = block.getRelative(BlockFace.UP);
        var down = block.getRelative(BlockFace.DOWN);
        var north = block.getRelative(BlockFace.NORTH);
        var south = block.getRelative(BlockFace.SOUTH);
        var east = block.getRelative(BlockFace.EAST);
        var west = block.getRelative(BlockFace.WEST);
        checkItem(others, up, down, north, south, east, west);
    }

    private void checkItem(Queue<Block> others, Block... blocks) {
        for (var block : blocks) {
            if (ItemUtil.isSafeToBreak(block) && block.getType() != Material.END_STONE && block.getType() != Material.OBSIDIAN) {
                if (!others.contains(block)) {
                    others.add(block);
                    addNearBlocks(others, block);
                }
            }
        }
    }

    private boolean isAlreadyBreaking(Player player) {
        return this.pairs.containsKey(player);
    }

    @Override
    public List<Pair<? extends Event>> getEventConsumers() {
        return List.of(
                Pair.of(BlockBreakEvent.class, this::onBlockBreak)
        );
    }

    @Override
    public int getTier() {
        return 5;
    }
}
