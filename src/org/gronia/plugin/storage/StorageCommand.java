package org.gronia.plugin.storage;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gronia.plugin.SubCommandExecutor;

import java.util.*;
import java.util.stream.Collectors;

public class StorageCommand extends SubCommandExecutor<StoragePlugin> {
    private final StorageUtil utils;

    public StorageCommand(StoragePlugin plugin) {
        super(plugin);
        this.utils = new StorageUtil(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            return this.warnUser(player);
        }

        String command = args[0];

        if (utils.handleCommand(sender, args)) {
            return true;
        }

        if (command.equalsIgnoreCase("list")) {
            if (args.length == 1) {
                this.showInventory(player, -1);
            } else {
                try {
                    this.showInventory(player, Integer.parseInt(args[1]));
                } catch (Exception e) {
                    this.showInventory(player, -1);
                }
            }
            return true;
        }

        if (!command.equalsIgnoreCase("open") && !command.equalsIgnoreCase("take") && !command.equalsIgnoreCase("stackable") && !command.equalsIgnoreCase("serializable")) {
            return this.warnUser(player);
        }

        if (command.equalsIgnoreCase("open") && args.length < 2) {
            this.openInventory(player, "Deposit");
            return true;
        }

        String materialName = args[1].toLowerCase();
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this.warnUser(player);
        }

        if (command.equalsIgnoreCase("stackable")) {
            List<String> serializableList = this.getPlugin().getSerializableItemList();
            if (!serializableList.contains(materialName)) {
                return this.warnUser(player);
            }

            serializableList.remove(materialName);
            this.getPlugin().getConfig().set("serializable_items", serializableList);
            this.getPlugin().saveConfig();
            return true;
        }

        if (command.equalsIgnoreCase("serializable")) {
            List<String> serializableList = this.getPlugin().getSerializableItemList();
            if (serializableList.contains(materialName)) {
                return this.warnUser(player);
            }

            serializableList.add(materialName);
            this.getPlugin().getConfig().set("serializable_items", serializableList);
            this.getPlugin().saveConfig();
            return true;
        }

        if (command.equalsIgnoreCase("open")) {
            this.openInventory(player, materialName);
            return true;
        }

        if (command.equalsIgnoreCase("take")) {
            List<String> serializableList = this.getPlugin().getSerializableItemList();
            if (serializableList.contains(materialName)) {
                return this.warnUser(player);
            }

            if (args.length == 3) {
                int count = Integer.parseInt(args[2]);
                this.takeItem(player, materialName, material, count);
            } else {
                return this.warnUser(player);
            }
        }

        return true;
    }

    public void showInventory(final HumanEntity ent, int page) {
        ConfigurationSection section = this.getPlugin().getStackableConfig();
        Set<String> keys = section.getKeys(false);
        if (keys.size() == 0) {
            return;
        }

        if (page == -1 && keys.size() > 54) {
            ent.sendMessage(ChatColor.RED + "Page count: " + (int) (Math.ceil(keys.size() / 54f)));
            return;
        }

        page -= 1;
        if (page < 0) {
            page = 0;
        }

        int slotCount = Math.min((int) Math.ceil(keys.size() / 9.0) * 9, 54);

        Inventory inv = Bukkit.createInventory(null, slotCount, "[Storage] View");

        Map<String, Integer> items = new HashMap<>();
        for (String key : keys) {
            items.put(key, section.getInt(key));
        }

        List<Map.Entry<String, Integer>> list = items.entrySet().stream().sorted(Map.Entry.comparingByValue()).skip(page * 54).limit(54).collect(Collectors.toList());
        for (Map.Entry<String, Integer> e : list) {
            ItemStack stack = new ItemStack(Material.valueOf(e.getKey().toUpperCase()));
            List<String> lore = new ArrayList<>();
            int count = e.getValue();
            if (count > 0) {
                lore.add(ChatColor.GREEN + "Count: " + count);
            } else {
                lore.add(ChatColor.RED + "Count: " + count);
            }
            ItemMeta meta = stack.getItemMeta();
            meta.setLore(lore);
            stack.setItemMeta(meta);
            inv.addItem(stack);
        }

        ent.openInventory(inv);
    }

    public void openInventory(final HumanEntity ent, String materialName) {
        Inventory inv = Bukkit.createInventory(null, 54, "[Storage] " + materialName);
        ent.openInventory(inv);
    }

    public boolean warnUser(Player player) {
        player.sendMessage("[Storage] " + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/help storage" + ChatColor.WHITE + " for help");
        return true;
    }

    public void takeItem(final HumanEntity ent, String materialName, Material material, int count) {
        addItemToPlayer(ent, count, material);

        Map<String, Integer> changes = new HashMap<>();
        changes.put(materialName, -count);

        this.getPlugin().getAPI().applyStackable(ent.getName(), changes);
    }

    private void addItemToPlayer(HumanEntity player, int count, Material mat) {
        ItemStack stack = new ItemStack(mat, count);
        HashMap<Integer, ItemStack> drops = player.getInventory().addItem(stack);
        for (ItemStack item : drops.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }
}
