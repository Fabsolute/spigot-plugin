package org.gronia.plugin;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.hf.HyperFurnacePlugin;
import org.gronia.plugin.pouch.PouchPlugin;
import org.gronia.plugin.ptp.PerfectTPPlugin;
import org.gronia.plugin.storage.StoragePlugin;
import org.gronia.plugin.uei.UltraEnchantedItemPlugin;
import org.gronia.plugin.ti.TeleportItemPlugin;
import org.gronia.utils.GroniaMysqlConfiguration;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Gronia extends JavaPlugin {
    private final Map<String, NamespacedKey> keys = new HashMap<>();
    private final Map<String, Recipe> recipeMap = new HashMap<>();
    public final NamespacedKey recipeKey = this.getKey("recipe_name");

    public final Map<String,ItemStack> customItems = new HashMap<>();

    private final SubPlugin<?>[] plugins = new SubPlugin[]{
            new HyperFurnacePlugin(this),
            new TeleportItemPlugin(this),
            new PouchPlugin(this),
            new UltraEnchantedItemPlugin(this),
            new PerfectTPPlugin(this),
            new StoragePlugin(this)
    };

    private static Gronia instance;

    public Gronia(){
        instance = this;
    }

    public static Gronia getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
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
    }

    @Override
    public void onDisable() {
        for (SubPlugin<?> plugin : this.plugins) {
            plugin.onDisable();
        }
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

    public boolean addRecipe(Recipe recipe) {
        var item = recipe.getResult();
        var meta = item.getItemMeta();
        assert meta != null;

        this.recipeMap.put(meta.getPersistentDataContainer().get(recipeKey, PersistentDataType.STRING), recipe);
        return this.getServer().addRecipe(recipe);
    }

    public <T extends Recipe> T getOriginalRecipe(String name) {
        return (T) this.recipeMap.get(name);
    }
}
