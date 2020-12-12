package org.gronia.plugin.planter;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

enum Farmables {
    WHEAT(Material.WHEAT_SEEDS, Material.WHEAT),
    CARROT(Material.CARROT, Material.CARROTS),
    POTATO(Material.POTATO, Material.POTATOES),
    BEETROOT(Material.BEETROOT_SEEDS, Material.BEETROOTS),
    MELON(Material.MELON_SEEDS, Material.MELON_STEM),
    NETHER_WART(Material.NETHER_WART, Material.NETHER_WART),
    PUMPKIN(Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM);

    private final Material mat;
    private final Material bl;

    Farmables(Material mat, Material bl) {
        this.mat = mat;
        this.bl = bl;
    }

    public Material getItemMaterial() {
        return this.mat;
    }

    public Material getBlockMaterial() {
        return this.bl;
    }

    public static Farmables getFarmable(ItemStack item) {
        for (Farmables f : values()) {
            if (f.getItemMaterial().equals(item.getType())) {
                return f;
            }
        }

        return null;
    }
}
