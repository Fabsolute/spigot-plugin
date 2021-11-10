package org.gronia.plugin.disenchant;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.SubCommandExecutor;

import java.util.Arrays;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;

public class DisenchantCommand extends SubCommandExecutor<DisenchantPlugin> {
    public DisenchantCommand(DisenchantPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!cmd.getName().equals("disenchant")) {
            return false;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();

        ItemStack itemInHand = inventory.getItemInMainHand();

        if (args.length == 0) {
            return this.handleDisenchant(inventory, itemInHand);
        }

        String command = args[0];
        if (command.equalsIgnoreCase("split")) {
            return this.handleSplit(inventory, itemInHand);
        }

        if (command.equalsIgnoreCase("merge")) {
            return this.handleMerge(inventory, itemInHand);
        }

        return false;
    }

    private boolean handleMerge(PlayerInventory inventory, ItemStack itemInHand) {
        if (itemInHand.getType() != Material.ENCHANTED_BOOK) {
            return true;
        }

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemInHand.getItemMeta();
        assert meta != null;

        if (meta.getStoredEnchants().entrySet().size() != 1) {
            return true;
        }

        var first = meta.getStoredEnchants().entrySet().iterator().next();

        if (first.getKey().getMaxLevel() == first.getValue()) {
            return true;
        }

        boolean upgrade = false;

        for (var item : inventory.getContents()) {
            if (item == null || item == itemInHand) { // todo item == itemInHand always false
                continue;
            }

            if (item.getType() != Material.ENCHANTED_BOOK) {
                continue;
            }

            EnchantmentStorageMeta otherMeta = (EnchantmentStorageMeta) item.getItemMeta();
            assert otherMeta != null;

            if (otherMeta.getStoredEnchants().entrySet().size() != 1) {
                continue;
            }

            var otherFirst = meta.getStoredEnchants().entrySet().iterator().next();

            if (otherFirst.getKey() != first.getKey() || !Objects.equals(otherFirst.getValue(), first.getValue())) {
                continue;
            }

            inventory.removeItem(item);
            upgrade = true;
            break;
        }

        if (upgrade) {
            meta.addStoredEnchant(first.getKey(), first.getValue() + 1, true);
            itemInHand.setItemMeta(meta);
        }

        return true;
    }

    private boolean handleSplit(PlayerInventory inventory, ItemStack itemInHand) {
        if (itemInHand.getType() != Material.ENCHANTED_BOOK) {
            return false;
        }

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemInHand.getItemMeta();
        assert meta != null;

        for (var entry : meta.getStoredEnchants().entrySet()) {
            ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta enchantedMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
            assert enchantedMeta != null;

            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            enchantedMeta.addStoredEnchant(enchant, level, true);
            enchantedBook.setItemMeta(enchantedMeta);
            inventory.addItem(enchantedBook);
        }

        inventory.removeItem(itemInHand);
        return true;
    }

    private boolean handleDisenchant(PlayerInventory inventory, ItemStack itemInHand) {
        if (itemInHand.getEnchantments().isEmpty() || itemInHand.getType() == Material.ENCHANTED_BOOK) {
            return false;
        }

        ItemMeta meta = itemInHand.getItemMeta();
        assert meta != null;


        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantedMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
        assert enchantedMeta != null;

        for (var entry : meta.getEnchants().entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();
            enchantedMeta.addStoredEnchant(enchant, level, true);
            meta.removeEnchant(enchant);
        }

        enchantedBook.setItemMeta(enchantedMeta);
        inventory.addItem(enchantedBook);
        itemInHand.setItemMeta(meta);

        return true;
    }
}
