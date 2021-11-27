package org.gronia.plugin;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.fatigue.FatiguePlugin;
import org.gronia.plugin.griefing.GriefingPlugin;
import org.gronia.items.*;
import org.gronia.plugin.npc.BlacksmithTrait;
import org.gronia.plugin.npc.StorageWorkerTrait;
import org.gronia.plugin.sack.SackPlugin;
import org.gronia.plugin.ptp.PerfectTPPlugin;
import org.gronia.plugin.repair.RepairPlugin;
import org.gronia.plugin.storage.StoragePlugin;
import org.gronia.plugin.uei.*;
import org.gronia.plugin.ti.TeleportItemPlugin;
import org.gronia.utils.GroniaMysqlConfiguration;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gronia extends JavaPlugin {
    private final Map<String, NamespacedKey> keys = new HashMap<>();
    private final Map<String, CustomShapedRecipe> shapedRecipeMap = new HashMap<>();
    private final Map<String, CustomShapelessRecipe> shapelessRecipeMap = new HashMap<>();
    public final NamespacedKey recipeKey = this.getKey("recipe_name");

    private final SubPlugin<?>[] plugins = new SubPlugin[]{
            new TeleportItemPlugin(this),
            new SackPlugin(this),
            new UltraEnchantedItemPlugin(this),
            new PerfectTPPlugin(this),
            new StoragePlugin(this),
            new RepairPlugin(this),
            new GriefingPlugin(this),
            new FatiguePlugin(this)
    };

    private final List<CustomItem> customItems = new ArrayList<>() {
        {
            addAll(BakedPotato.getAll());
            addAll(Carrot.getAll());
            addAll(Cobblestone.getAll());
            addAll(Obsidian.getAll());
            addAll(Netherrack.getAll());
            addAll(SweetBerries.getAll());
            addAll(SweetPotion.getAll());
            add(new PiercerPickaxe());
            add(new SuperHoe());
            add(new HyperFurnace());
            add(new Teleporter());
            add(new SuperPickaxe());
            add(new ShulkerSack());
            add(new ShulkerSack.Upgrader());
            add(new ElytraUpgrader());
        }
    };

    private static Gronia instance;

    public Gronia() {
        instance = this;
    }

    public static Gronia getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BlacksmithTrait.class).withName("Blacksmith"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(StorageWorkerTrait.class).withName("StorageWorker"));

        var config = this.getConfig();
        var url = config.getString("mysql_connection");
        assert url != null;
        GroniaMysqlConfiguration.initialize(() -> {
            try {
                return DriverManager.getConnection(url);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });

        for (SubPlugin<?> plugin : this.plugins) {
            plugin.onEnable();
        }

        for (CustomItem item : this.customItems) {
            ItemRegistry.register(item);
        }
    }

    @Override
    public void onDisable() {
        for (SubPlugin<?> plugin : this.plugins) {
            plugin.onDisable();
        }

        ItemRegistry.deregisterAll();
    }

    public <T extends SubPlugin<T>> T getSubPlugin(Class<T> clazz) {
        for (SubPlugin<?> plugin : this.plugins) {
            if (clazz.isInstance(plugin)) {
                return (T) plugin;
            }
        }

        return null;
    }

    public NamespacedKey getKey(String name) {
        if (!keys.containsKey(name)) {
            keys.put(name, new NamespacedKey(Gronia.this, name));
        }

        return keys.get(name);
    }

    public boolean addRecipe(String internalName, CustomRecipe recipe, boolean isShaped) {
        if (isShaped) {
            this.shapedRecipeMap.put(internalName, (CustomShapedRecipe) recipe);
            return this.getServer().addRecipe((CustomShapedRecipe) recipe);
        }

        this.shapelessRecipeMap.put(internalName, (CustomShapelessRecipe) recipe);

        return true;
    }

    public CustomShapedRecipe getCustomShapedRecipe(String name) {
        return this.shapedRecipeMap.get(name);
    }

    public CustomShapelessRecipe getCustomShapelessRecipe(ItemStack stack) {
        return getCustomShapelessRecipe(ItemRegistry.getInternalName(stack));
    }

    public CustomShapelessRecipe getCustomShapelessRecipe(String name) {
        for (var recipeEntry : this.shapelessRecipeMap.entrySet()) {
            if (recipeEntry.getValue().getIngredient().equalsIgnoreCase(name)) {
                return recipeEntry.getValue();
            }
        }

        return null;
    }
}
