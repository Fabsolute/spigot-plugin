package org.gronia.plugin.storage;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class StorageUtil {
    private final StoragePlugin plugin;
    private final List<Material> materials = new ArrayList<>();

    public StorageUtil(StoragePlugin plugin) {
        this.plugin = plugin;
    }

    private void dropDiffs(final HumanEntity ent) {
        for (Material material : materials) {
            ItemStack stack = new ItemStack(Material.BARREL, 1);
            ItemMeta meta = stack.getItemMeta();
            String name = material.name().toLowerCase().replace("_", " ");
            String[] words = name.split(" ");
            StringJoiner joiner = new StringJoiner(" ");
            for (String word : words) {
                joiner.add(Character.toUpperCase(word.charAt(0)) + word.substring(1));
            }

            meta.setDisplayName("[S] " + joiner.toString());
            stack.setItemMeta(meta);
            ent.getWorld().dropItemNaturally(ent.getLocation(), stack);
        }
    }

    private void calculateDiff(final HumanEntity ent) {
        materials.clear();
        for (Material mat : Material.values()) {
            if (mat.getMaxStackSize() < 16) {
                continue;
            }

            if (this.getPlugin().materials.getOrDefault(mat, false)) {
                continue;
            }

            materials.add(mat);
        }

        ent.sendMessage(ChatColor.AQUA + "Found: " + this.getPlugin().materials.size() + " Required: " + materials.size());
    }

    private void scanChunk(final HumanEntity ent) {
        Location location = ent.getLocation();
        Chunk chunk = location.getChunk();
        for (int x = 0; x < 16; x++) {
            for (int y = 57; y <= 60; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (block.getType() == Material.BARREL) {
                        Barrel barrel = (Barrel) block.getState();
                        String name = barrel.getCustomName();
                        if (name == null || (!name.startsWith("[S] ") && !name.startsWith("[Storage] "))) {
                            continue;
                        }

                        name = name.replace("[S] ", "").replace("[Storage] ", "").replace(" ", "_").toUpperCase();
                        try {
                            Material material = Material.valueOf(name);
                            this.getPlugin().materials.put(material, true);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }
        }
    }

    public boolean handleCommand(CommandSender sender, String[] args) {
        if (!this.getPlugin().getConfig().getBoolean("scanner-active", false)) {
            return false;
        }

        Player player = (Player) sender;
        String command = args[0];

        if (command.equalsIgnoreCase("clear")) {
            this.getPlugin().materials.clear();
            return true;
        }

        if (command.equalsIgnoreCase("scan")) {
            this.scanChunk(player);
            return true;
        }

        if (command.equalsIgnoreCase("diff")) {
            this.calculateDiff(player);
            return true;
        }

        if (command.equalsIgnoreCase("drop")) {
            this.dropDiffs(player);
            return true;
        }

        return false;
    }

    public StoragePlugin getPlugin() {
        return plugin;
    }
}
