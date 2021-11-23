package org.gronia.plugin.sack;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubUtil;
import org.gronia.plugin.items.ShulkerSack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SackUtil extends SubUtil<SackPlugin> {
    public SackUtil(SackPlugin plugin) {
        super(plugin);
    }

    public void pickItem(Player player, ItemStack stack) {
        var inventory = this.getInventory(player);
        PlayerInventory playerInventory = player.getInventory();

        if (inventory.getKeys(false).contains(stack.getType().name())) {
            var playerHeads = playerInventory.all(Material.PLAYER_HEAD);
            for (var head : playerHeads.values()) {
                if (!(ItemRegistry.getCustomItem(head) instanceof ShulkerSack)) {
                    continue;
                }

                pickItemToHead(head, player, stack, true);
                return;
            }
        }

        pickItemToPlayer(player, stack, true);
    }

    public HashMap<Integer, ItemStack> pickItemToPlayer(HumanEntity player, ItemStack stack, boolean drop) {
        PlayerInventory inventory = player.getInventory();
        ItemStack offhand = inventory.getItemInOffHand();
        if (offhand.getType() == stack.getType()) {
            int amount = offhand.getAmount() + stack.getAmount();
            int maxAmount = offhand.getType().getMaxStackSize();
            if (amount > maxAmount) {
                offhand.setAmount(maxAmount);
                amount -= maxAmount;
                stack.setAmount(amount);
            } else {
                offhand.setAmount(amount);
                return new HashMap<>();
            }
        }

        var drops = inventory.addItem(stack);
        if (drop) {
            dropItems(player, drops);
            return new HashMap<>();
        }

        return drops;
    }

    public void openSack(Player player, ItemStack head) {
        var menu = new SackMenu(Gronia.getInstance(), head, getInventory(player));
        player.openInventory(menu.getInventory());
    }

    public void fillPlayer(HumanEntity player, Material material) {
        var inventory = this.getInventory(player);
        var name = material.name();
        int count = inventory.getInt(name, 0);
        if (count <= 0) {
            return;
        }

        var drops = this.pickItemToPlayer(player, new ItemStack(material, count), false);
        count = 0;
        for (ItemStack item : drops.values()) {
            count += item.getAmount();
        }

        inventory.set(name, count);
        this.getPlugin().getConfig().setDirty();
    }

    public void tryRemoveItem(HumanEntity player, Material material) {
        var inventory = this.getInventory(player);
        var name = material.name();
        int count = inventory.getInt(name, 0);
        if (count != 0) {
            player.sendMessage(ChatColor.RED + "Slot is not empty.");
            return;
        }

        inventory.set(name, null);
    }

    public void addDebt(String player, Material material, int count) {
        count = -count;

        var inventory = this.getInventory(player);
        var name = material.name();
        count += inventory.getInt(name, 0);
        inventory.set(name, count);
        this.getPlugin().getConfig().setDirty();
    }

    public void emptyPlayer(ItemStack head, HumanEntity player, Material material) {
        var playerInventory = player.getInventory();

        for (var item : playerInventory.all(material).values()) {
            if (Objects.requireNonNull(item.getItemMeta()).hasEnchants()) {
                continue;
            }

            int dropCount = pickItemToHead(head, player, item, false);

            if (dropCount == 0) {
                playerInventory.removeItem(item);
            } else {
                item.setAmount(dropCount);
                break;
            }
        }

        this.getPlugin().getConfig().setDirty();
    }

    private int pickItemToHead(ItemStack head, HumanEntity player, ItemStack stack, boolean drop) {
        var size = ShulkerSack.getSize(head);
        var MAX_COUNT = this.getPlugin().PER_COUNT * size;

        ConfigurationSection configurationSection = this.getInventory(player);
        String name = stack.getType().name();
        int count = configurationSection.getInt(name, 0);
        count += stack.getAmount();
        int drops = 0;
        if (count > MAX_COUNT) {
            drops = count - MAX_COUNT;
            count = MAX_COUNT;
        }

        configurationSection.set(name, count);

        if (drop) {
            if (drops > 0) {
                stack.setAmount(drops);
                pickItemToPlayer(player, stack, true);
                return 0;
            }
        }

        this.getPlugin().getConfig().setDirty();

        return drops;
    }

    private void dropItems(HumanEntity player, Map<Integer, ItemStack> drops) {
        for (ItemStack item : drops.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item).setOwner(player.getUniqueId());
        }
    }

    private ConfigurationSection getInventory(HumanEntity player) {
        return getInventory(player.getName());
    }

    private ConfigurationSection getInventory(String player) {
        var playerConfiguration = this.getPlugin().getConfig().getConfigurationSection(player);
        if (playerConfiguration == null) {
            playerConfiguration = this.getPlugin().createSackConfiguration(player);
        }

        return playerConfiguration;
    }
}
