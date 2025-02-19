package dev.name.util.collections.map;

import java.util.HashMap;
import java.util.Map;

public final class FastHashMap<K, V> extends HashMap<K, V> implements AbstractMap<K, V> {
    public FastHashMap() {}

    public FastHashMap(Map<K, V> clone) {
        this.putAll(clone);
    }
}