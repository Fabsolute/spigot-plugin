package org.gronia.menu;

import org.gronia.menu.iterator.WareHouseIterator;
import org.gronia.plugin.warehouse.WareHousePlugin;

import java.util.Map;

public class WareHouseMenu {
    public static CustomPageMenu create(WareHousePlugin plugin, WareHouseIterator iterator) {
        return CustomPageMenu.create(plugin.getPlugin(), iterator);
    }

    public static CustomPageMenu create(WareHousePlugin plugin, String caseName, Map<String, Integer> items) {
        return create(plugin, new WareHouseIterator(caseName, items));
    }
}
