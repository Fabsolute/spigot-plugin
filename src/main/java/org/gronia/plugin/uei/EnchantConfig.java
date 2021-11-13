package org.gronia.plugin.uei;

import org.bukkit.Material;

public record EnchantConfig(Material material,int level, int required, String name) {
}
