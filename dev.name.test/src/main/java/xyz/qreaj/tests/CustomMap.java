package xyz.qreaj.tests;

import java.util.*;

public class CustomMap<K, V> {
    private Map<K, V> map = new HashMap<>();
    private transient EntrySet entrySet;

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet result = entrySet;
        return result != null ? result : (entrySet = new EntrySet());
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return map.entrySet().iterator();
        }

        @Override
        public int size() {
            return map.size();
        }
    }
}