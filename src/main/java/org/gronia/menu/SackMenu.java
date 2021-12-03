package org.gronia.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.items.ShulkerSack;
import org.gronia.plugin.Gronia;
import org.gronia.plugin.ItemRegistry;
import org.gronia.plugin.sack.SackPlugin;
import org.gronia.plugin.storage.StoragePlugin;
import org.jetbrains.annotations.Nullable;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;
import xyz.janboerman.guilib.api.menu.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class SackMenu extends MenuHolder<Gronia> {
    private boolean editMode = false;
    private final ItemStack head;
    private final ConfigurationSection inventory;

    public SackMenu(Gronia plugin, ItemStack head, ConfigurationSection inventory) {
        super(plugin, 54, "Shulker Sack");
        this.head = head;
        this.inventory = inventory;
        this.initialize();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        var item = event.getCurrentItem();
        if (item == null) {
            return;
        }

        if (!editMode || event.getClickedInventory() != event.getView().getBottomInventory()) {
            return;
        }

        if (inventory.isInt(item.getType().name())) {
            return;
        }

        if (inventory.getKeys(false).size() >= 25) {
            return;
        }

        if (item.getType().getMaxDurability() > 0) {
            return;
        }

        var meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        var customItem = ItemRegistry.getCustomItem(item);
        if (customItem != null) {
            return;
        }

        inventory.set(item.getType().name(), 0);
        event.getView().close();
    }

    private void initialize() {
        var greenWool = new ItemBuilder(Material.GREEN_WOOL).name("Edit Mode Enabled").build();
        var whiteWool = new ItemBuilder(Material.WHITE_WOOL).name("Edit Mode Disabled").build();

        var applyAllItem = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(
                applyAllItem,
                "{SkullOwner:{Id:[I;1645403077,-1355199707,-1482328559,-500862078],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWZkOGJlZDJkZmU0YzMyMTY4Yzk3MjE1NGVlYTMzNWE4MDQyZTlkNjRiODUwNzY3YzZlYTA0Y2U4Zjg1ZjEyYSJ9fX0=\"}]}}}"
        );

        applyAllItem = new ItemBuilder(applyAllItem).name("Apply All").lore(this.getLore(-999999)).build();

        this.setButton(0, new ToggleButton<MenuHolder<Gronia>>(whiteWool) {
            @Override
            public void afterToggle(MenuHolder<Gronia> menuHolder, InventoryClickEvent event) {
                super.afterToggle(menuHolder, event);
                editMode = !editMode;
            }

            @Override
            public ItemStack updateIcon(MenuHolder<Gronia> menuHolder, InventoryClickEvent event) {
                return isEnabled() ? greenWool : whiteWool;
            }
        });

        var shulkerSack = (ShulkerSack) ItemRegistry.getCustomItem(head);

        if (this.hasEnderChest(shulkerSack)) {
            var enderChestItem = new ItemBuilder(Material.ENDER_CHEST).build();
            this.setButton(
                    53,
                    new ItemButton<MenuHolder<Gronia>>(enderChestItem) {
                        @Override
                        public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                            super.onClick(holder, event);
                            if (editMode) {
                                return;
                            }

                            event.getWhoClicked().openInventory(event.getWhoClicked().getEnderChest());
                        }
                    }
            );
        }

        if (this.hasFlush(shulkerSack)) {
            var flushItem = new ItemStack(Material.PLAYER_HEAD);
            Bukkit.getUnsafe().modifyItemStack(
                    flushItem,
                    "{SkullOwner:{Id:[I;-1864701848,-334870135,-2029676573,613468765],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzI0MzE5MTFmNDE3OGI0ZDJiNDEzYWE3ZjVjNzhhZTQ0NDdmZTkyNDY5NDNjMzFkZjMxMTYzYzBlMDQzZTBkNiJ9fX0=\"}]}}}"
            );

            flushItem = new ItemBuilder(flushItem).name("Transfer to Storage").build();
            this.setButton(
                    26,
                    new ItemButton<MenuHolder<Gronia>>(flushItem) {
                        @Override
                        public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                            super.onClick(holder, event);
                            if (editMode) {
                                return;
                            }

                            Gronia.getInstance().getSubPlugin(SackPlugin.class).executeFlushCommand(event.getWhoClicked(), false);
                            event.getView().close();
                        }
                    }
            );
        }

        if (this.hasStorage(shulkerSack)) {
            var storageItem = new ItemBuilder(Material.BARREL).name("Storage").build();
            this.setButton(
                    35,
                    new ItemButton<MenuHolder<Gronia>>(storageItem) {
                        @Override
                        public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                            super.onClick(holder, event);
                            if (editMode) {
                                return;
                            }

                            Gronia.getInstance().getSubPlugin(StoragePlugin.class).executeListCommand(event.getWhoClicked(), false);
                        }
                    }
            );
        }

        this.setButton(
                45,
                new ItemButton<MenuHolder<Gronia>>(applyAllItem) {
                    @Override
                    public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                        super.onClick(holder, event);
                        if (editMode) {
                            return;
                        }

                        for (int j = 0; j < 5; j++) {
                            for (int i = 0; i < 5; i++) {
                                var slot = j * 9 + (i + 2);
                                var btn = (MenuButton<MenuHolder<Gronia>>) holder.getButton(slot);
                                if (btn == null) {
                                    continue;
                                }

                                var newEvent = new InventoryClickEvent(event.getView(), event.getSlotType(), slot, event.getClick(), event.getAction());
                                btn.onClick(holder, newEvent);
                            }
                        }
                    }
                }
        );

        if (this.hasCraftingTable(shulkerSack)) {
            var craftingTable = new ItemBuilder(Material.CRAFTING_TABLE).build();
            this.setButton(
                    8,
                    new ItemButton<MenuHolder<Gronia>>(craftingTable) {
                        @Override
                        public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                            super.onClick(holder, event);
                            if (editMode) {
                                return;
                            }

                            event.getWhoClicked().openWorkbench(null, true);
                        }
                    }
            );
        }

        var shulkerInventory = this.inventory.getKeys(false).stream().toList();

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                var index = j * 5 + i;
                if (shulkerInventory.size() <= index) {
                    continue;
                }

                var name = shulkerInventory.get(index);
                Material material = Material.valueOf(name);
                ItemStack item = new ItemStack(material);

                setMeta(item);
                this.setButton(
                        j * 9 + (i + 2),
                        new ItemButton<MenuHolder<Gronia>>(item) {
                            @Override
                            public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                                super.onClick(holder, event);
                                onItemClick(event.getWhoClicked(), event.getCurrentItem(), event.isLeftClick(), editMode);
                                if (editMode) {
                                    event.getView().close();
                                }
                            }
                        }
                );
            }
        }

    }

    private boolean hasCraftingTable(ShulkerSack sack) {
        return this.head.getItemMeta().getPersistentDataContainer().has(sack.craftingTableKey, PersistentDataType.INTEGER);
    }

    private boolean hasEnderChest(ShulkerSack sack) {
        return this.head.getItemMeta().getPersistentDataContainer().has(sack.enderChestKey, PersistentDataType.INTEGER);
    }

    private boolean hasFlush(ShulkerSack sack) {
        return this.head.getItemMeta().getPersistentDataContainer().has(sack.flushKey, PersistentDataType.INTEGER);
    }

    private boolean hasStorage(ShulkerSack sack) {
        return this.head.getItemMeta().getPersistentDataContainer().has(sack.storageKey, PersistentDataType.INTEGER);
    }

    private List<String> getLore(int count) {
        var lore = new ArrayList<String>();

        if (count != -999999) {
            lore.add("Count: " + ChatColor.AQUA + count);
        }

        lore.add(ChatColor.RED + "↑ Right Click");
        lore.add(ChatColor.GREEN + "↓ Left Click");

        return lore;
    }

    private void setMeta(ItemStack item) {
        var material = item.getType();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        int count = inventory.getInt(material.name(), 0);
        if (count > 0) {
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeEnchant(Enchantment.LURE);
        }

        meta.setLore(getLore(count));
        item.setItemMeta(meta);
    }

    private void onItemClick(HumanEntity player, @Nullable ItemStack current, boolean isLeftClick, boolean editMode) {
        assert current != null;
        var type = current.getType();

        var plugin = Gronia.getInstance().getSubPlugin(SackPlugin.class);

        if (editMode) {
            if (!isLeftClick) {
                plugin.getUtils().tryRemoveItem(player, type);
            }

            return;
        }

        if (isLeftClick) {
            plugin.getUtils().fillPlayer(player, type);
        } else {
            plugin.getUtils().emptyPlayer(head, player, type);
        }

        setMeta(current);
    }
}
