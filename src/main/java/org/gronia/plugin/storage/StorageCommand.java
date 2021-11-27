package org.gronia.plugin.storage;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEditor;
import com.comphenix.packetwrapper.WrapperPlayServerTileEntityData;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.sack.SackPlugin;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.menu.*;

import java.util.*;

public class StorageCommand extends SubCommandExecutor<StoragePlugin> {
    public StorageCommand(StoragePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length < 2) {
            return this.warnUser(player);
        }

        String command = args[0];

        if (handleExternalCommands(player, command, args)) {
            return true;
        }

        String password = args[1];
        if (!this.getPlugin().getPassword().equalsIgnoreCase(password)) {
            return false;
        }

        if (command.equalsIgnoreCase("list")) {
            this.getPlugin().showInventory(player);
            return true;
        }

        if (!command.equalsIgnoreCase("open") && !command.equalsIgnoreCase("take")) {
            return this.warnUser(player);
        }

        if (args.length < 3) {
            return this.warnUser(player);
        }

        String materialName = args[2].toLowerCase();
        if (!(command.equalsIgnoreCase("open") && materialName.equalsIgnoreCase("deposit")) && !ItemRegistry.isValidMaterialName(materialName)) {
            return this.warnUser(player);
        }

        if (command.equalsIgnoreCase("open")) {
            this.openInventory(player, materialName);
            return true;
        }

        if (command.equalsIgnoreCase("take")) {
            var serializableList = this.getPlugin().getSerializableItemList();
            var notTakableList = this.getPlugin().getNotTakableItemList();
            if (serializableList.contains(materialName) || notTakableList.contains(materialName)) {
                return this.warnUser(player, "You cannot take this items. Use " + ChatColor.GREEN + "/storage open " + materialName + " " + ChatColor.DARK_PURPLE + ChatColor.MAGIC + "A" + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + "CLICK" + ChatColor.DARK_PURPLE + ChatColor.MAGIC + "A", "/storage open " + materialName);
            }

            if (args.length == 4) {
                int count = Integer.parseInt(args[3]);
                this.takeItem(player, materialName, count);
            } else {
                return this.warnUser(player);
            }
        }

        return true;
    }

    private boolean handleExternalCommands(Player player, String command, String[] args) {
        if (!command.equalsIgnoreCase("category") && !command.equalsIgnoreCase("stackable") && !command.equalsIgnoreCase("serializable") && !command.equalsIgnoreCase("takable") && !command.equalsIgnoreCase("not_takable")) {
            return false;
        }

        if (command.equalsIgnoreCase("category")) {
            if (!this.handleCategoryCommands(player, args)) {
                return this.warnUser(player);
            }

            return true;
        }

        String materialName = args[1].toLowerCase();
        if (!ItemRegistry.isValidMaterialName(materialName)) {
            return this.warnUser(player);
        }

        if (command.equalsIgnoreCase("stackable")) {
            List<String> serializableList = this.getPlugin().getSerializableItemList();
            if (!serializableList.contains(materialName)) {
                return this.warnUser(player);
            }

            serializableList.remove(materialName);
            this.getPlugin().getConfig().set("serializable_items", serializableList);
            this.getPlugin().getConfig().setDirty();
            return true;
        }

        if (command.equalsIgnoreCase("serializable")) {
            List<String> serializableList = this.getPlugin().getSerializableItemList();
            if (serializableList.contains(materialName)) {
                return this.warnUser(player);
            }

            serializableList.add(materialName);
            this.getPlugin().getConfig().set("serializable_items", serializableList);
            this.getPlugin().getConfig().setDirty();
            return true;
        }

        if (command.equalsIgnoreCase("takable")) {
            List<String> notTakableList = this.getPlugin().getNotTakableItemList();
            if (!notTakableList.contains(materialName)) {
                return this.warnUser(player);
            }

            notTakableList.remove(materialName);
            this.getPlugin().getConfig().set("not_takable_list", notTakableList);
            this.getPlugin().getConfig().setDirty();
            return true;
        }

        if (command.equalsIgnoreCase("not_takable")) {
            List<String> notTakableList = this.getPlugin().getNotTakableItemList();
            if (notTakableList.contains(materialName)) {
                return this.warnUser(player);
            }

            notTakableList.add(materialName);
            this.getPlugin().getConfig().set("not_takable_list", notTakableList);
            this.getPlugin().getConfig().setDirty();
            return true;
        }

        return true;
    }

    private boolean handleCategoryCommands(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String subCommand = args[1];
        switch (subCommand) {
            case "icon" -> {
                if (args.length != 4) {
                    return false;
                }

                String categoryId = args[2];
                String materialName = args[3].toUpperCase();

                try {
                    Material.valueOf(materialName);
                } catch (Exception ignored) {
                    return false;
                }

                var category = this.getPlugin().getCategorySection(categoryId);
                if (category == null) {
                    return false;
                }

                category.set("icon", materialName);
                this.getPlugin().getConfig().setDirty();
            }
            case "name" -> {
                if (args.length != 4) {
                    return false;
                }

                String categoryId = args[2];
                String name = args[3];

                var category = this.getPlugin().getCategorySection(categoryId);
                if (category == null) {
                    return false;
                }

                category.set("name", name);
                this.getPlugin().getConfig().setDirty();
            }
            case "toggle" -> {
                if (args.length != 3) {
                    return false;
                }

                String categoryId = args[2];

                var category = this.getPlugin().getCategorySection(categoryId);
                if (category == null) {
                    return false;
                }

                category.set("enabled", !category.getBoolean("enabled"));
                this.getPlugin().getConfig().setDirty();
            }
            case "list" -> {
                if (args.length != 5) {
                    return false;
                }

                String categoryId = args[2];
                String lastCommand = args[3];
                if (!lastCommand.equalsIgnoreCase("add") && !lastCommand.equalsIgnoreCase("remove")) {
                    return false;
                }

                String materialName = args[4].toLowerCase();

                if (!ItemRegistry.isValidMaterialName(materialName)) {
                    return false;
                }

                var category = this.getPlugin().getCategorySection(categoryId);
                if (category == null) {
                    return false;
                }

                var list = category.getStringList("list");
                if (lastCommand.equalsIgnoreCase("add") && list.contains(materialName)) {
                    return false;
                } else if (lastCommand.equalsIgnoreCase("remove") && !list.contains(materialName)) {
                    return false;
                }

                if (lastCommand.equalsIgnoreCase("add")) {
                    list.add(materialName);
                } else {
                    list.remove(materialName);
                }

                category.set("list", list);
                this.getPlugin().getConfig().setDirty();
            }
        }

        return true;
    }

    public void openInventory(final HumanEntity ent, String materialName) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Storage " + materialName);
        ent.openInventory(inv);
    }

    public boolean warnUser(HumanEntity player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "Storage " + ChatColor.RESET + "Type " + ChatColor.GREEN + "/help storage" + ChatColor.WHITE + " for help");
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean warnUser(HumanEntity player, String msg, String command) {
        TextComponent textComponent = new TextComponent(ChatColor.DARK_PURPLE + "Storage " + ChatColor.RESET + ChatColor.RED + msg);
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
        textComponent.setClickEvent(clickEvent);

        player.spigot().sendMessage(textComponent);
        return true;
    }

    public void takeItem(final HumanEntity ent, String materialName, int count) {
        var stack = ItemRegistry.createItem(materialName);
        stack.setAmount(count);

        this.getPlugin().getSubPlugin(SackPlugin.class).getUtils().pickItemToPlayer(ent, stack, true);

        Map<String, Integer> changes = new HashMap<>();
        changes.put(materialName, -count);
        this.getPlugin().applyStackable(ent.getName(), changes);
    }
}
