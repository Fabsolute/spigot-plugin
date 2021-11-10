package org.gronia.plugin;

import java.util.HashMap;

public class NumberMap<K> extends HashMap<K, Integer> {
    public Integer plus(K key, int count) {
        Integer old = this.getOrDefault(key, 0);
        old += count;
        return this.put(key, old);
    }
}
