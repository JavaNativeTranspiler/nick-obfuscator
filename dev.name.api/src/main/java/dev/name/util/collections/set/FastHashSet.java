package dev.name.util.collections.set;

import dev.name.util.collections.AbstractCollection;

import java.util.HashSet;
import java.util.Iterator;

@SuppressWarnings("unused")
public final class FastHashSet<T> extends HashSet<T> implements AbstractCollection<T, FastHashSet<T>> {
    public FastHashSet() {}

    public FastHashSet(FastHashSet<T> clone) {
        addFrom(clone);
    }

    @SafeVarargs
    public FastHashSet(T... elements) {
        addAll(elements);
    }

    public T getFirst() {
        Iterator<T> iterator = iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public FastHashSet<T> intersection(AbstractCollection<? extends T, FastHashSet<T>> other) {
        FastHashSet<T> set = new FastHashSet<>();
        set.addAll(this);
        set.removeIf(o -> !other.contains(o));
        return set;
    }
}