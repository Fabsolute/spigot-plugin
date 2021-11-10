package org.gronia.plugin.griefing;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.SubListener;
import org.gronia.plugin.SubPlugin;
import org.gronia.plugin.SubTabCompleter;

import java.util.HashMap;
import java.util.Map;

public class GriefingPlugin extends SubPlugin<GriefingPlugin> {
    public final Map<EntityType, String> entityTypeSettingLookup = new HashMap<>();
    public GriefingPlugin(JavaPlugin plugin) {
        super(plugin);

        entityTypeSettingLookup.put(EntityType.CREEPER, "creeper");
        entityTypeSettingLookup.put(EntityType.FIREBALL, "ghast");
        entityTypeSettingLookup.put(EntityType.WITHER_SKULL, "wither");
        entityTypeSettingLookup.put(EntityType.WITHER, "wither");
        entityTypeSettingLookup.put(EntityType.ENDER_DRAGON, "ender-dragon");
        entityTypeSettingLookup.put(EntityType.PRIMED_TNT, "tnt");
        entityTypeSettingLookup.put(EntityType.MINECART_TNT, "tnt");
        entityTypeSettingLookup.put(EntityType.SHEEP, "sheep");
        entityTypeSettingLookup.put(EntityType.RABBIT, "rabbit");
        entityTypeSettingLookup.put(EntityType.RAVAGER, "ravager");
        entityTypeSettingLookup.put(EntityType.ENDERMAN, "enderman");
        entityTypeSettingLookup.put(EntityType.SILVERFISH, "silverfish");
        entityTypeSettingLookup.put(EntityType.ZOMBIE, "zombie");
        entityTypeSettingLookup.put(EntityType.SNOWMAN, "snowman");
    }

    @Override
    public String getName() {
        return "griefing";
    }

    @Override
    public SubListener<GriefingPlugin> getListener() {
        return new GriefingListener(this);
    }

    @Override
    public SubCommandExecutor<GriefingPlugin> getExecutor() {
        return new GriefingCommand(this);
    }

    @Override
    public SubTabCompleter<GriefingPlugin> getTabCompleter() {
        return new GriefingTabCompleter(this);
    }
}
