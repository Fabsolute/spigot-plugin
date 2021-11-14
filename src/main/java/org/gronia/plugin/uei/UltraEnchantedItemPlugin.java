package org.gronia.plugin.uei;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;
import org.gronia.utils.Pair;

import java.util.List;
import java.util.Map;

public class UltraEnchantedItemPlugin extends SubPlugin<UltraEnchantedItemPlugin> {
    public final Map<String, List<Pair.Pair3<String, String, Integer>>> enchantConfigs = Map.of(
            Material.COBBLESTONE.name(),
            List.of(
                    Pair.Pair3.of("cobblestone", "nothing", 1),
                    Pair.Pair3.of("enchanted_cobblestone", "cobblestone", 4),
                    Pair.Pair3.of("extra_enchanted_cobblestone", "enchanted_cobblestone", 4),
                    Pair.Pair3.of("ultra_enchanted_cobblestone", "extra_enchanted_cobblestone", 4),
                    Pair.Pair3.of("super_enchanted_cobblestone", "ultra_enchanted_cobblestone", 8),
                    Pair.Pair3.of("enchanted_obsidian", "super_enchanted_cobblestone", 2)
            ),
            Material.OBSIDIAN.name(),
            List.of(
                    Pair.Pair3.of("obsidian", "nothing", 1),
                    Pair.Pair3.of("extra_enchanted_obsidian", "enchanted_obsidian", 4),
                    Pair.Pair3.of("ultra_enchanted_obsidian", "extra_enchanted_obsidian", 4),
                    Pair.Pair3.of("super_enchanted_obsidian", "ultra_enchanted_obsidian", 4)
            ),
            Material.CARROT.name(),
            List.of(
                    Pair.Pair3.of("carrot", "nothing", 1),
                    Pair.Pair3.of("enchanted_carrot", "carrot", 4),
                    Pair.Pair3.of("extra_enchanted_carrot", "enchanted_carrot", 4),
                    Pair.Pair3.of("ultra_enchanted_carrot", "extra_enchanted_carrot", 4),
                    Pair.Pair3.of("super_enchanted_carrot", "ultra_enchanted_carrot", 8)
            ),
            Material.BAKED_POTATO.name(),
            List.of(
                    Pair.Pair3.of("baked_potato", "nothing", 1),
                    Pair.Pair3.of("enchanted_baked_potato", "baked_potato", 4)
            )
    );


    public UltraEnchantedItemPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "uei";
    }

    @Override
    public SubListener<UltraEnchantedItemPlugin> getListener() {
        return new UltraEnchantedItemListener(this);
    }

    @Override
    public SubCommandExecutor<UltraEnchantedItemPlugin> getExecutor() {
        return null;
    }

    @Override
    public SubTabCompleter<UltraEnchantedItemPlugin> getTabCompleter() {
        return null;
    }
}
