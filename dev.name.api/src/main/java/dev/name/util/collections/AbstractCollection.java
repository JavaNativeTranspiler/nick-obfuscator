package dev.name.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A base interface for creating custom collections with enhanced functionality.
 * This interface provides methods for fluent operations like concatenation,
 * union, intersection, and difference, designed for speed and ease of use.
 *
 * @param <T> the type of elements in this collection
 * @param <R> the type of the concrete implementation extending this interface
 */
@SuppressWarnings({"unchecked", "unused"})
public interface AbstractCollection<T, R extends AbstractCollection<? extends T, R>> extends Iterable<T> {

    /**
     * Returns the current instance cast to the implementation type.
     * This allows for fluent method chaining.
     *
     * @return the current instance as {@code R}
     */
    default R self() {
        return (R) this;
    }

    /**
     * Adds all specified elements to the collection.
     *
     * @param elements the elements to add
     * @return the current instance, for chaining
     */
    default R concat(T... elements) {
        return addAll(elements);
    }

    /**
     * Adds all specified elements from another collection to this collection.
     *
     * @param other the collection containing the elements to add
     * @return the current instance, for chaining
     */
    default R concat(AbstractCollection<? extends T, R> other) {
        other.forEach(this::add);
        return self();
    }

    /**
     * Adds all specified elements from another collection to this collection.
     *
     * @param other the collection containing the elements to add
     * @return the current instance, for chaining
     */
    default R concat(Collection<? extends T> other) {
        return addFrom(other);
    }

    /**
     * Adds multiple elements to the collection.
     * Uses a simple loop for maximum speed.
     *
     * @param elements the elements to add
     * @return the current instance, for chaining
     */
    default R addAll(T... elements) {
        for (T element : elements) {
            add(element);
        }

        return self();
    }

    /**
     * Adds multiple elements from another collection to this collection.
     * Uses a simple loop for maximum speed.
     *
     * @param other the collection containing the elements to add
     * @return the current instance, for chaining
     */
    default R addFrom(Collection<? extends T> other) {
        if (other == null) return self();

        for (T element : other) {
            add(element);
        }

        return self();
    }

    /**
     * Removes multiple elements from the collection.
     * Uses a simple loop for maximum speed.
     *
     * @param elements the elements to remove
     * @return the current instance, for chaining
     */
    default R removeAll(T... elements) {
        for (T element : elements) {
            remove(element);
        }

        return self();
    }

    /**
     * Performs a difference operation, removing all elements from this collection
     * that are present in the specified collection.
     *
     * @param other the other collection
     * @return the current instance, for chaining
     */
    default R difference(Collection<? extends T> other) {
        if (other == null) return self();
        this.remove(other::contains);
        return self();
    }

    /**
     * Performs a union operation, adding all elements from the specified collection
     * to this collection. Duplicate elements are ignored.
     *
     * @param other the other collection
     * @return the current instance, for chaining
     */
    default R union(AbstractCollection<? extends T, R> other) {
        if (other == null) return self();
        other.forEach(this::add);
        return self();
    }

    /**
     * Performs an intersection operation, retaining only elements that are present
     * in both this collection and the specified collection.
     *
     * @param other the other collection
     * @return the current instance, for chaining
     */
    R intersection(AbstractCollection<? extends T, R> other);

    /**
     * Performs a difference operation, removing all elements from this collection
     * that are present in the specified collection.
     *
     * @param other the other collection
     * @return the current instance, for chaining
     */
    default R difference(AbstractCollection<? extends T, R> other) {
        if (other == null) return self();
        this.remove(other::contains);
        return self();
    }

    /**
     * Adds an element to the collection.
     *
     * @param element the element to add
     * @return {@code true} if the collection changed as a result
     */
    boolean add(T element);

    /**
     * Removes an element from the collection.
     *
     * @param element the element to remove
     * @return {@code true} if the collection changed as a result
     */
    boolean remove(T element);

    /**
     * Checks if an element is present in this collection.
     *
     * @param element the element to check if contained
     * @return {@code true} if the collection contains {@param element}
     */
    boolean contains(Object element);

    /**
     * Removes all elements matching the given predicate.
     * This method is intended for use by intersection and difference operations.
     *
     * @param filter a condition to apply to elements
     * @return {@code true} if any elements were removed
     */
    @SuppressWarnings("UnusedReturnValue")
    default boolean remove(Predicate<? super T> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<T> each = iterator();

        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }

        return removed;
    }
}