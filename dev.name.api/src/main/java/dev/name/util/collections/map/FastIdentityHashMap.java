package dev.name.util.collections.map;

import java.util.IdentityHashMap;
import java.util.Map;

public class FastIdentityHashMap<K, V> extends IdentityHashMap<K, V> implements AbstractMap<K, V> {
    public FastIdentityHashMap() {}

    public FastIdentityHashMap(Map<K, V> clone) {
        this.putAll(clone);
    }
}