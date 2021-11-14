package org.gronia.disabled.sp;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.SubListener;


public class SuperPlanterListener extends SubListener<SuperPlanterPlugin> {
    public SuperPlanterListener(SuperPlanterPlugin plugin) {
        super(plugin);
    }


    @EventHandler
    public void onPlanterClicked(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null) {
            return;
        }

        checkPlanterClicked(itemStack, event.getPlayer(), event);
    }

    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        var stand = event.getRightClicked();
        var slot = event.getSlot();
        if (slot != EquipmentSlot.HEAD) {
            if (stand.getPersistentDataContainer().has(this.getPlugin().<Gronia>getPlugin().getKey("auto-generated"), PersistentDataType.INTEGER)) {
                event.setCancelled(true);
            }

            return;
        }
    }

    private void checkPlanterClicked(ItemStack stack, Player player, PlayerInteractEvent event) {
        if (stack.getType() != Material.PLAYER_HEAD) {
            return;
        }

        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        if (!meta.getDisplayName().contains("Planter")) {
            return;
        }

        if (!meta.hasEnchant(Enchantment.LURE)) {
            return;
        }

        if (event.hasBlock()) {
            var block = event.getClickedBlock();
            assert block != null;
            event.setCancelled(true);

            World world = block.getWorld();
            Block highest = world.getHighestBlockAt(block.getLocation());
            if (!(highest.getX() == block.getX() && highest.getY() == block.getY() && highest.getZ() == block.getZ())) {
                return;
            }

            var armorStand = (ArmorStand) world.spawnEntity(
                    this.getPlugin().getUtils().getLookAt(highest.getLocation().add(0, 1, 0), player.getEyeLocation()), // todo 10
                    EntityType.ARMOR_STAND,
                    true
            );

            var equipment = armorStand.getEquipment();
            assert equipment != null;
            var newStack = stack.clone();
            newStack.setAmount(1);
            equipment.setHelmet(newStack);

            stack.setAmount(stack.getAmount() - 1);
            armorStand.setCollidable(false);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);

            armorStand.getPersistentDataContainer().set(this.getPlugin().<Gronia>getPlugin().getKey("auto-generated"), PersistentDataType.INTEGER, 1);

//            armorStand.setVisible(false); // todo
            return;
        }

        event.setCancelled(true);
    }
}
