package org.gronia.plugin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.gronia.plugin.uei.*;

import java.util.*;
import java.util.function.Consumer;

public class ItemRegistry {
    private static final Map<String, CustomItem> customItems = new HashMap<>();
    private static final List<NamespacedKey> keyList = new ArrayList<>();
    private static final Map<Class<? extends Event>, List<Consumer<Event>>> listeners = new HashMap<>();

    public static void register(CustomItem customItem) {
        if (customItem instanceof CraftableItem<?>) {
            registerRecipe(customItem);
        }

        if (customItem instanceof EventListenerItem eventListenerItem) {
            for (var p : eventListenerItem.getEventConsumers()) {
                if (!listeners.containsKey(p.p1())) {
                    listeners.put(p.p1(), new ArrayList<>());
                }

                listeners.get(p.p1()).add((Consumer<Event>) p.p2());
            }
        }

        customItems.put(customItem.getInternalName(), customItem);

        customItem.onEnable();
    }

    public static void deregister(CustomItem customItem) {
        customItems.remove(customItem.getInternalName());
        // todo deregister recipe
        customItem.onDisable();
    }

    private static void registerRecipe(CustomItem item) {
        if (!(item instanceof CraftableItem craftableItem)) {
            return;
        }

        var key = Gronia.getInstance().getKey(item.getInternalName());
        keyList.add(key);

        CustomRecipe recipe;

        if (craftableItem.isShaped()) {
            recipe = new CustomShapedRecipe(key, item.create());
        } else {
            recipe = new CustomShapelessRecipe(item.getInternalName());
        }

        //noinspection unchecked
        craftableItem.fillRecipe(recipe);
        Gronia.getInstance().addRecipe(item.getInternalName(), recipe, craftableItem.isShaped());
    }

    public static void deregisterAll() {
        for (var key : keyList) {
            Gronia.getInstance().getServer().removeRecipe(key);
        }

        for (var ci : customItems.values()) {
            deregister(ci);
        }

        listeners.clear();
    }

    public static String getItemName(String internalName) {
        return ItemRegistry.customItems.get(internalName).getName();
    }

    public static ItemStack createItem(String name) {
        name = name.toLowerCase();
        var internal = customItems.get(name);
        if (internal == null) {
            return new ItemStack(Material.valueOf(name.toUpperCase()));
        }

        return internal.create();
    }

    public static boolean isValidMaterialName(String name) {
        name = name.toLowerCase();
        if (customItems.containsKey(name)) {
            return true;
        }

        try {
            Material.valueOf(name.toUpperCase());
            return true;
        } catch (IllegalArgumentException ignored) {
        }

        return false;
    }

    public static Material getMaterialFor(String name) {
        name = name.toLowerCase();
        if (customItems.containsKey(name)) {
            return customItems.get(name).getBaseType();
        }

        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static String getInternalName(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        var name = stack.getType().name().toLowerCase();
        var meta = stack.getItemMeta();
        if (meta != null) {
            var recipeName = meta.getPersistentDataContainer().get(Gronia.getInstance().recipeKey, PersistentDataType.STRING);
            if (recipeName != null) {
                name = recipeName;
            }
        }

        return name;
    }

    public static List<String> getCustomItems() {
        var output = new ArrayList<>(Arrays.stream(Material.values()).map(m -> m.name().toLowerCase()).toList());
        output.addAll(customItems.keySet());
        return output;
    }

    public static CustomItem getCustomItem(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        var internalName = getInternalName(stack);
        return customItems.get(internalName);
    }

    public static void fireEvent(Event event) {
        for (var c : listeners.getOrDefault(event.getClass(), new ArrayList<>())) {
            c.accept(event);
        }
    }
}
