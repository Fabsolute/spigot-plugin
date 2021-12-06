package org.gronia.plugin.repair;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.uei.CustomItem;

import java.util.logging.Level;

public class RepairCommand extends SubCommandExecutor<RepairPlugin> {
    public RepairCommand(RepairPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        String password = args[0];
        if (!this.getPlugin().getPassword().equalsIgnoreCase(password)) {
            return false;
        }

        boolean unsafe = false;
        if (args.length > 1) {
            String command = args[1];
            if (command.equalsIgnoreCase("unsafe")) {
                unsafe = true;
            }
        }

        for (ItemStack stack : player.getInventory()) {
            if (stack == null) {
                continue;
            }

            Material type = stack.getType();

            if (type.getMaxDurability() > 0) {
                ItemMeta meta = stack.getItemMeta();
                if (meta instanceof Damageable damageable) {
                    float multiplier = 4f;
                    if (!meta.hasEnchant(Enchantment.MENDING)) {
                        if (!unsafe) {
                            continue;
                        }
                    } else {
                        multiplier = 0.5f;
                    }

                    int damage = damageable.getDamage();
                    var exp = this.getTotalExperience(player);
                    int xp = Math.min(exp, (int)(damage * multiplier));
                    player.giveExp(-xp);
                    damageable.setDamage(damage - (int)(xp / multiplier));
                    stack.setItemMeta(damageable);

                    if (CustomItem.isBroken(stack)) {
                        CustomItem.setRepaired(stack);
                    }
                }
            }
        }

        return true;
    }

    public int getTotalExperience(Player player) {
        int level = player.getLevel();
        int levelSquare = level * level;
        int totalExperience = player.getTotalExperience();

        if (level < 17) {
            return levelSquare + (6 * level) + totalExperience;
        }

        if (level < 32) {
            return (int) ((2.5 * levelSquare) - (40.5 * level) + 360) + totalExperience;
        }

        return (int) ((4.5 * levelSquare) - (162.5 * level) + 2220) + totalExperience;
    }
}
