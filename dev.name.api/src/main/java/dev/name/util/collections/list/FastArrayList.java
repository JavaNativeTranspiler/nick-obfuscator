package dev.name.util.collections.list;

import dev.name.util.collections.AbstractCollection;

@SuppressWarnings("unused")
public final class FastArrayList<T> extends AbstractArrayList<T, FastArrayList<T>> {
    public FastArrayList() {}

    public FastArrayList(AbstractArrayList<T, ?> clone) {
        addFrom(clone);
    }

    @SafeVarargs
    public FastArrayList(T... elements) {
        addAll(elements);
    }

    @Override
    public FastArrayList<T> intersection(AbstractCollection<? extends T, FastArrayList<T>> other) {
        FastArrayList<T> arr = new FastArrayList<>();
        arr.addAll(this);
        arr.removeIf(o -> !other.contains(o));
        return arr;
    }
}