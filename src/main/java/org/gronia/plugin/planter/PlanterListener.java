package org.gronia.plugin.planter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.storage.StoragePlugin;

public class PlanterListener extends SubListener<PlanterPlugin> {
    public PlanterListener(PlanterPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDispense(BlockDispenseEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!this.getPlugin().getConfig().getBoolean("enabled")) {
            return;
        }

        if (!event.getBlock().getType().equals(Material.DISPENSER)) {
            return;
        }
        Dispenser dispenser = (Dispenser) event.getBlock().getState();
        this.fillIfRequired(dispenser);

        ItemStack item = null;
        for (ItemStack stack : dispenser.getInventory().getContents()) {
            if (stack != null) {
                item = stack;
                break;
            }
        }

        if (item == null) {
            return;
        }

        if (!isFarmable(item)) {
            return;
        }

        final ItemStack finalItem = item;
        event.setCancelled(true);
        (new BukkitRunnable() {
            public void run() {
                if (!isUp(event.getBlock())) {
                    return;
                }
                Block[] nineBlock = getUpNineBlock(event.getBlock().getLocation());
                for (Block block : nineBlock) {
                    if (finalItem.getAmount() == 0) {
                        continue;
                    }

                    if (block.getType() != Material.FARMLAND) {
                        if (block.getType() != Material.SOUL_SAND || finalItem.getType() != Material.NETHER_WART) {
                            continue;
                        }
                    }

                    Block upperBlock = block.getRelative(BlockFace.UP);
                    if (upperBlock.getType() != Material.AIR) {
                        continue;
                    }

                    upperBlock.setType(Farmables.getFarmable(finalItem).getBlockMaterial());
                    finalItem.setAmount(finalItem.getAmount() - 1);
                }
            }
        }).runTaskLater(this.getPlugin().getPlugin(), 1L);
    }

    private boolean isFarmable(ItemStack i) {
        return Farmables.getFarmable(i) != null;
    }

    private boolean isUp(Block b) {
        String s = b.getBlockData().toString();
        return s.contains("up");
    }

    private Block[] getUpNineBlock(Location location) {
        World world = location.getWorld();
        assert world != null;

        int x = location.getBlockX() - 1;
        int y = location.getBlockY() + 1;
        int z = location.getBlockZ() - 1;
        Block[] output = new Block[9];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                output[i * 3 + j] = world.getBlockAt(x + i, y, z + j);
            }
        }

        return output;
    }


    private void fillIfRequired(Dispenser dispenser) {
        this.getPlugin().getSubPlugin(StoragePlugin.class).getAPI().handlePuller(dispenser, dispenser.getCustomName());
    }
}
