package org.gronia.plugin.pouch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubUtil;
import org.gronia.plugin.items.ShulkerSack;

import java.util.*;

public class PouchUtil extends SubUtil<PouchPlugin> {
    public PouchUtil(PouchPlugin plugin) {
        super(plugin);
    }

    public void pickItem(Player player, ItemStack stack) {
        PlayerInventory playerInventory = player.getInventory();

        if (PouchPlugin.pouchableItems.contains(stack.getType())) {
            var playerHeads = playerInventory.all(Material.PLAYER_HEAD);
            for (var head : playerHeads.values()) {
                if (!(ItemRegistry.getCustomItem(head) instanceof ShulkerSack)) {
                    continue;
                }

                pickItemToHead(player, stack, true);
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

    public ItemStack createEmptyItem(Material material) {
        ItemStack output = new ItemStack(material);
        ItemMeta meta = output.getItemMeta();
        assert meta != null;
        meta.setDisplayName(" ");
        output.setItemMeta(meta);
        return output;
    }

    public void openPouch(Player player, Inventory inventory) {

        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 9; i++) {
                if (i >= 2 && i <= 6 && j <= 4) {
                    continue;
                }

                if ((i == 8 || i == 0) && j == 5) {
                    continue;
                }

                inventory.setItem(
                        j * 9 + i,
                        this.getPlugin().lockedAreaItem
                );
            }
        }


        inventory.setItem(
                53,
                this.getPlugin().enderChestItem
        );


        inventory.setItem(
                45,
                this.getPlugin().applyAllItem
        );

        inventory.setItem(
                8,
                this.getPlugin().craftingTable
        );

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                Material material = PouchPlugin.pouchableItems.get(i * 5 + j);
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;

                meta.setLore(getLore(player, material));
                item.setItemMeta(meta);

                inventory.setItem(
                        j * 9 + (i + 2),
                        item
                );
            }
        }

        player.openInventory(inventory);
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

    public void addDebt(String player, Material material, int count) {
        count = -count;

        var inventory = this.getInventory(player);
        var name = material.name();
        count += inventory.getInt(name, 0);
        inventory.set(name, count);
        this.getPlugin().getConfig().setDirty();
    }

    public void emptyPlayer(HumanEntity player, Material material) {
        var playerInventory = player.getInventory();

        for (var item : playerInventory.all(material).values()) {
            if (Objects.requireNonNull(item.getItemMeta()).hasEnchants()) {
                continue;
            }

            int dropCount = pickItemToHead(player, item, false);

            if (dropCount == 0) {
                playerInventory.remove(item);
            } else {
                item.setAmount(dropCount);
                break;
            }
        }

        this.getPlugin().getConfig().setDirty();
    }

    public List<String> getLore(HumanEntity player, Material material) {
        var lore = new ArrayList<String>();

        if (player != null) {
            var headInventory = this.getInventory(player);
            int count = headInventory.getInt(material.name(), 0);
            lore.add("Count: " + ChatColor.AQUA + count);
        }

        lore.add(ChatColor.RED + "↑ Right Click");
        lore.add(ChatColor.GREEN + "↓ Left Click");

        return lore;
    }

    private int pickItemToHead(HumanEntity player, ItemStack stack, boolean drop) {
        ConfigurationSection configurationSection = this.getInventory(player);
        String name = stack.getType().name();
        int count = configurationSection.getInt(name, 0);
        count += stack.getAmount();
        int drops = 0;
        if (count > this.getPlugin().MAX_COUNT) {
            drops = count - getPlugin().MAX_COUNT;
            count = getPlugin().MAX_COUNT;
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
            playerConfiguration = new MemoryConfiguration(this.getPlugin().getConfig());
            this.getPlugin().getConfig().set(player, playerConfiguration);
        }

        return playerConfiguration;
    }
}
