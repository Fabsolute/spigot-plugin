package org.gronia.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
import java.util.function.Consumer;
import java.util.function.Function;

public class SackMenu extends MenuHolder<Gronia> {
    private boolean editMode = false;
    private boolean lockMode = false;
    private final ItemStack head;
    private final ConfigurationSection inventory;

    private static final ItemStack editModeItem;
    private static final ItemStack editModeDisableItem;
    private static final ItemStack applyAllItem;
    private static final ItemStack lockItem;
    private static final ItemStack unlockItem;
    private static final ItemStack flushItem;

    static {
        editModeItem = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(editModeItem, "{SkullOwner:{Id:[I;-1964715142,1349865085,-1306510920,278633160],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDQ5YTZkMzMwNDY1YmM5NGQ1NzM3MTY0MjhkYzY2MmNjMTZhY2QzYjgwNTM1YjUyMDI4YzY2NWY0ZGFmNjgyZSJ9fX0=\"}]}}}");

        var meta = editModeItem.getItemMeta();
        meta.displayName(Component.text("Edit Mode"));
        editModeItem.setItemMeta(meta);

        editModeDisableItem = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(editModeDisableItem, "{SkullOwner:{Id:[I;1220651740,763711424,-1176308628,483742326],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUwNjQ5NjI2YzQxMDEzNTJjNTk5NWM1M2I0OGJmZjYwYTkzODIxMmI3Y2U5MDI0MTVmZWI3NmVhMjczYjM1ZiJ9fX0=\"}]}}}");

        meta = editModeDisableItem.getItemMeta();
        meta.displayName(Component.text("Edit Mode"));
        editModeDisableItem.setItemMeta(meta);

        applyAllItem = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(applyAllItem, "{SkullOwner:{Id:[I;1645403077,-1355199707,-1482328559,-500862078],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWZkOGJlZDJkZmU0YzMyMTY4Yzk3MjE1NGVlYTMzNWE4MDQyZTlkNjRiODUwNzY3YzZlYTA0Y2U4Zjg1ZjEyYSJ9fX0=\"}]}}}");

        meta = applyAllItem.getItemMeta();
        meta.displayName(Component.text("Apply All"));
        meta.lore(getLore(-999999, false));
        applyAllItem.setItemMeta(meta);

        lockItem = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(lockItem, "{SkullOwner:{Id:[I;1369704657,1020281584,-2076494119,1936977119],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzRmYzYyY2Y0Nzc3YWJmMzU2NWE3MDk4NTI3NzhlMjQ4YWFhZTkzNmZkNTE1N2MzMWRiMmEzYzI0NzBhNjY1YyJ9fX0=\"}]}}}");

        meta = lockItem.getItemMeta();
        meta.displayName(Component.text("Lock Mode"));
        lockItem.setItemMeta(meta);

        unlockItem = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(unlockItem, "{SkullOwner:{Id:[I;-182574028,1517636810,-1400693412,1778210169],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGIxZWFjNDI5MDAwZWIyYzQxOGJlNDZlYTA2YWNkZWZlZDRiNzA1M2EwMGUzZTdmMTEyNzI4MzAwNjhmMjEifX19\"}]}}}");

        meta = unlockItem.getItemMeta();
        meta.displayName(Component.text("Lock Mode"));
        unlockItem.setItemMeta(meta);

        flushItem = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(flushItem, "{SkullOwner:{Id:[I;-1864701848,-334870135,-2029676573,613468765],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzI0MzE5MTFmNDE3OGI0ZDJiNDEzYWE3ZjVjNzhhZTQ0NDdmZTkyNDY5NDNjMzFkZjMxMTYzYzBlMDQzZTBkNiJ9fX0=\"}]}}}");

        meta = flushItem.getItemMeta();
        meta.displayName(Component.text("Transfer to Storage"));
        flushItem.setItemMeta(meta);
    }

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

    private static ToggleButton<MenuHolder<Gronia>> createToggleButton(ItemStack disabledItem, ItemStack enabledItem, Function<Boolean, Boolean> onToggle) {
        return new ToggleButton<>(disabledItem) {
            @Override
            public boolean beforeToggle(MenuHolder<Gronia> menuHolder, InventoryClickEvent event) {
                return onToggle.apply(isEnabled());
            }

            @Override
            public ItemStack updateIcon(MenuHolder<Gronia> menuHolder, InventoryClickEvent event) {
                return isEnabled() ? enabledItem : disabledItem;
            }
        };
    }

    private void initialize() {
        this.setButton(0, createToggleButton(editModeItem, editModeDisableItem, isEnabled -> {
            if (lockMode) {
                return false;
            }

            editMode = isEnabled;
            return true;
        }));

        var shulkerSack = (ShulkerSack) ItemRegistry.getCustomItem(head);

        if (this.hasLock(shulkerSack)) {
            this.setButton(18, createToggleButton(lockItem, unlockItem, isEnabled -> {
                if (editMode) {
                    return false;
                }

                lockMode = isEnabled;
                return true;
            }));
        }

        if (this.hasEnderChest(shulkerSack)) {
            var enderChestItem = new ItemBuilder(Material.ENDER_CHEST).build();
            this.setButton(53, new ItemButton<MenuHolder<Gronia>>(enderChestItem) {
                @Override
                public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                    super.onClick(holder, event);
                    if (editMode || lockMode) {
                        return;
                    }

                    event.getWhoClicked().openInventory(event.getWhoClicked().getEnderChest());
                }
            });
        }

        if (this.hasFlush(shulkerSack)) {
            this.setButton(26, new ItemButton<MenuHolder<Gronia>>(flushItem) {
                @Override
                public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                    super.onClick(holder, event);
                    if (editMode || lockMode) {
                        return;
                    }

                    Gronia.getInstance().getSubPlugin(SackPlugin.class).executeFlushCommand(event.getWhoClicked(), false);
                    event.getView().close();
                }
            });
        }

        if (this.hasStorage(shulkerSack)) {
            var storageItem = new ItemBuilder(Material.BARREL).name("Storage").build();
            this.setButton(35, new ItemButton<MenuHolder<Gronia>>(storageItem) {
                @Override
                public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                    super.onClick(holder, event);
                    if (editMode || lockMode) {
                        return;
                    }

                    Gronia.getInstance().getSubPlugin(StoragePlugin.class).executeListCommand(event.getWhoClicked(), false);
                }
            });
        }

        this.setButton(45, new ItemButton<MenuHolder<Gronia>>(applyAllItem) {
            @Override
            public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                super.onClick(holder, event);
                if (editMode || lockMode) {
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
        });

        if (this.hasCraftingTable(shulkerSack)) {
            var craftingTable = new ItemBuilder(Material.CRAFTING_TABLE).build();
            this.setButton(8, new ItemButton<MenuHolder<Gronia>>(craftingTable) {
                @Override
                public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                    super.onClick(holder, event);
                    if (editMode || lockMode) {
                        return;
                    }

                    event.getWhoClicked().openWorkbench(null, true);
                }
            });
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
                this.setButton(j * 9 + (i + 2), new ItemButton<MenuHolder<Gronia>>(item) {
                    @Override
                    public void onClick(MenuHolder<Gronia> holder, InventoryClickEvent event) {
                        super.onClick(holder, event);
                        onItemClick(event.getWhoClicked(), event.getCurrentItem(), event.isLeftClick(), event.isShiftClick(), editMode, lockMode);
                        if (editMode || lockMode) {
                            event.getView().close();
                        }
                    }
                });
            }
        }
    }

    private boolean hasLock(ShulkerSack sack) {
        return this.head.getItemMeta().getPersistentDataContainer().has(sack.lockKey, PersistentDataType.INTEGER);
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

    private static List<Component> getLore(int count, boolean isLocked) {
        var lore = new ArrayList<Component>();

        if (count != -999999) {
            lore.add(Component.text("Count: ").append(Component.text(count, NamedTextColor.AQUA)));
            if (isLocked) {
                lore.add(Component.text("Locked", NamedTextColor.GOLD));
            }
        }

        lore.add(Component.text("↑ Right Click", NamedTextColor.RED));
        lore.add(Component.text("↓ Left Click", NamedTextColor.GREEN));

        return lore;
    }

    private void setMeta(ItemStack item) {
        var material = item.getType().name();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        int count = inventory.getInt(material, 0);
        if (count > 0) {
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeEnchant(Enchantment.LURE);
        }

        var lockConfig = Gronia.getInstance().getSubPlugin(SackPlugin.class).getLockConfig();

        meta.lore(getLore(count, lockConfig.isBoolean(material) && lockConfig.getBoolean(material)));
        item.setItemMeta(meta);
    }

    private void onItemClick(HumanEntity player, @Nullable ItemStack current, boolean isLeftClick, boolean isShiftClick, boolean editMode, boolean lockMode) {
        assert current != null;
        var type = current.getType();

        var plugin = Gronia.getInstance().getSubPlugin(SackPlugin.class);

        if (editMode) {
            if (!isLeftClick) {
                plugin.getUtils().tryRemoveItem(player, type);
            }

            return;
        }

        if (lockMode) {
            var lockConfig = Gronia.getInstance().getSubPlugin(SackPlugin.class).getLockConfig();
            lockConfig.set(type.name(), !(lockConfig.isBoolean(type.name()) && lockConfig.getBoolean(type.name())));
            return;
        }

        if (isLeftClick) {
            plugin.getUtils().fillPlayer(player, type, !isShiftClick);
        } else {
            plugin.getUtils().emptyPlayer(head, player, type);
        }

        setMeta(current);
    }
}
