package dev.name.util.collections.set;

import dev.name.util.collections.AbstractCollection;

import java.util.Iterator;
import java.util.LinkedHashSet;

@SuppressWarnings("unused")
public final class LinkedFastHashSet<T> extends LinkedHashSet<T> implements AbstractCollection<T, LinkedFastHashSet<T>> {
    public LinkedFastHashSet() {}

    public LinkedFastHashSet(LinkedFastHashSet<T> clone) {
        addFrom(clone);
    }

    @SafeVarargs
    public LinkedFastHashSet(T... elements) {
        addAll(elements);
    }

    public T getFirst() {
        Iterator<T> iterator = iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    @Override
    public LinkedFastHashSet<T> intersection(AbstractCollection<? extends T, LinkedFastHashSet<T>> other) {
        LinkedFastHashSet<T> set = new LinkedFastHashSet<>();
        set.addAll(this);
        set.removeIf(o -> !other.contains(o));
        return set;
    }
}