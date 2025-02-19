package dev.name.asm.ir.instructions;

import com.google.common.collect.ImmutableSet;
import dev.name.asm.ir.components.Global;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.pattern.Pattern;
import dev.name.asm.ir.types.Node;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class Instructions implements Iterable<Node> {
    private static final boolean CHECK = false;
    private final List<Node> instructions = new ObjectArrayList<>();
    @Getter private Method method;
    public Node first;
    public Node last;

    public Instructions(final Node... nodes) {
        for (final Node node : nodes)
            add(node);
    }

    public ImmutableSet<Pattern.Range> match(final Pattern pattern) {
        if (pattern == null) throw new NullPointerException();
        return pattern.match_all(this);
    }

    public void setMethod(final Method method) {
        if (method == null) throw new RuntimeException("Shouldn't happen.");
        this.method = method;
        for (final Node node : this)
            node.method = method;
    }

    public void add(final Node node) {
        if (CHECK && contains(node)) throw new IllegalStateException();
        node.method = method;
        node.parent = this;
        if (first == null) first = node;
        else {
            assert last != null : "impossible";
            last.next = node;
            node.previous = last;
        }
        last = node;
        instructions.add(node);
    }

    public void add(final Node... nodes) {
        for (final Node node : nodes)
            add(node);
    }

    public void add(final Instructions instructions) {
        if (instructions == null) throw new NullPointerException();
        for (final Node node : instructions) {
            if (CHECK && contains(node)) throw new IllegalStateException();
            add(node);
        }
    }

    public Node get(final int index) {
        if (index < 0 || index > size()) throw new IllegalArgumentException();
        return instructions.get(index);
    }

    public void set(final Node node, final Node newNode) {
        if (node == null || newNode == null) throw new IllegalArgumentException();
        if (CHECK && (!contains(node) || contains(newNode))) throw new IllegalArgumentException();
        final int index = indexOf(node);
        newNode.method = method;
        newNode.parent = this;
        newNode.next = node.next;
        newNode.previous = node.previous;
        if (node.previous != null) node.previous.next = newNode;
        else first = newNode;
        if (node.next != null) node.next.previous = newNode;
        else last = newNode;
        instructions.set(index, newNode);
        node.method = null;
        node.parent = null;
        node.previous = null;
        node.next = null;
    }

    public void set(final Node node, final Instructions instructions) {
        insertBefore(node, instructions);
        remove(node);
    }

    public void set(final int index, final Node newNode) {
        set(get(index), newNode);
    }

    public void remove(final Node node) {
        if (node == null) throw new IllegalArgumentException();
        if (CHECK && !contains(node)) throw new IllegalArgumentException();
        node.method = null;
        node.parent = null;
        if (node.previous != null) node.previous.next = node.next;
        else first = node.next;
        if (node.next != null) node.next.previous = node.previous;
        else last = node.previous;
        node.previous = null;
        node.next = null;
        instructions.remove(node);

        if (instructions.isEmpty()) {
            first = null;
            last = null;
        }
    }

    public void remove(final int index) {
        remove(get(index));
    }

    public void remove(final Node... nodes) {
        for (final Node node : nodes)
            remove(node);
    }

    public void remove(final Node start, final Node end) {
        if (start == null || end == null) throw new NullPointerException();
        if (CHECK && (!contains(start) || !contains(end))) throw new IllegalArgumentException();
        for (Node curr = start; curr != end; curr = curr.next) remove(curr);
    }

    public void remove(final int start, final int end) {
        remove(get(start), get(end));
    }

    public void removeIf(final Predicate<Node> condition) {
        forEach(condition, Node::delete);
    }

    public void swap(final Node first, final Node second) {
        if (first == null || second == null) throw new NullPointerException();
        if (CHECK && (!contains(first) || !contains(second))) throw new IllegalStateException();
        if (first == second) return;

        if (this.first == first) this.first = second;
        else if (this.first == second) this.first = first;

        if (this.last == first) this.last = second;
        else if (this.last == second) this.last = first;

        final Node f_prev = first.previous, f_next = first.next, s_prev = second.previous, s_next = second.next;

        if (f_next == second) {
            first.previous = second;
            first.next = s_next;
            second.previous = f_prev;
            second.next = first;
            if (s_next != null) s_next.previous = first;
            if (f_prev != null) f_prev.next = second;
        } else if (s_next == first) {
            second.previous = first;
            second.next = f_next;
            first.previous = s_prev;
            first.next = second;
            if (f_next != null) f_next.previous = second;
            if (s_prev != null) s_prev.next = first;
        } else {
            first.previous = s_prev;
            first.next = s_next;
            second.previous = f_prev;
            second.next = f_next;

            if (s_prev != null) s_prev.next = first;
            if (s_next != null) s_next.previous = first;
            if (f_prev != null) f_prev.next = second;
            if (f_next != null) f_next.previous = second;
        }

        final int f_index = indexOf(first), s_index = indexOf(second);

        instructions.set(f_index, second);
        instructions.set(s_index, first);
    }


    public void swap(final int first, final int second) {
        swap(get(first), get(second));
    }

    public void insert(final Node node) {
        if (first == null) add(node);
        else insertBefore(first, node);
    }

    public void insert(final int index, final Node node) {
        if (node == null || index < 0 || index > size() || (CHECK && contains(node))) throw new IllegalStateException();
        node.method = method;
        node.parent = this;
        instructions.add(index, node);
        if (first == null) {
            first = node;
            last = node;
        } else {
            node.next = first;
            first.previous = node;
            first = node;
        }
    }

    public void insert(final Instructions instructions) {
        insertBefore(first, instructions);
    }

    public void insertBefore(final Node existing, final Node node) {
        if (existing == null || node == null) throw new IllegalArgumentException();
        if (CHECK && !contains(existing)) throw new IllegalArgumentException();
        final int index = indexOf(existing);
        instructions.add(index, node);
        node.method = method;
        node.parent = this;
        node.previous = existing.previous;
        node.next = existing;
        if (existing.previous != null) existing.previous.next = node;
        else first = node;
        existing.previous = node;
    }

    public void insertBefore(final Node existing, final Instructions instructions) {
        if (instructions == null) throw new NullPointerException();
        for (final Node node : instructions)
            insertBefore(existing, node);
    }

    public void insertBefore(final int index, final Node node) {
        insertBefore(get(index), node);
    }

    public void insertBefore(final int index, final Instructions instructions) {
        insertBefore(get(index), instructions);
    }

    public void insertAfter(final Node existing, final Node node) {
        if (existing == null || node == null) throw new IllegalArgumentException();
        if (CHECK && !contains(existing)) throw new IllegalArgumentException();
        final int index = indexOf(existing) + 1;
        instructions.add(index, node);
        node.method = method;
        node.parent = this;
        node.previous = existing;
        node.next = existing.next;
        if (existing.next != null) existing.next.previous = node;
        else last = node;
        existing.next = node;
    }

    public void insertAfter(Node existing, final Instructions instructions) {
        if (instructions == null) throw new NullPointerException();

        for (final Node node : instructions) {
            insertAfter(existing, node);
            existing = node;
        }
    }

    public void insertAfter(final int index, final Node node) {
        insertAfter(get(index), node);
    }

    public void insertAfter(final int index, final Instructions instructions) {
        insertAfter(get(index), instructions);
    }

    public void replace(final Pattern pattern, final Instructions instructions) {
        if (instructions == null) throw new NullPointerException();
        for (final Pattern.Range range : match(pattern))
            range.replace(instructions);
    }

    public void replace(final Pattern pattern, final Function<Pattern.Range, Instructions> replacement) {
        if (replacement == null) throw new NullPointerException();
        for (final Pattern.Range range : match(pattern))
            range.replace(replacement.apply(range));
    }

    public void forEach(final Predicate<Node> predicate, final Consumer<Node> action) {
        for (final Node node : this)
            if (predicate.test(node))
                action.accept(node);
    }

    public void forEachMatch(final Pattern pattern, final Consumer<Pattern.Range> action) {
        if (action == null) throw new NullPointerException();
        for (final Pattern.Range range : match(pattern))
            action.accept(range);
    }

    /*
    Broken
    public void reverse() {
        if (instructions.isEmpty()) return;
        Collections.reverse(instructions);
        relink();
    }

    public void relink() {
        if (instructions.isEmpty()) return;
        final int size = size();
        last = get(0);
        first = get(size - 1);
        final int last = size - 1;
        for (int i = 0; i < size; i++) {
            final Node current = get(i);
            current.previous = (i > 0) ? get(i - 1) : null;
            current.next = (i < last) ? get(i + 1) : null;
        }
    }*/

    // TODO
    public Instructions copy() {
        throw new UnsupportedOperationException();
    }

    // check for out of range label copying..
    public Instructions copy(final Node begin, final Node end) {
        throw new UnsupportedOperationException();
    }

    public Instructions copy(final int begin, final int end) {
        return copy(get(begin), get(end));
    }

    public void trim(final Predicate<Node> predicate) {
        for (final Node node : this)
            if (!predicate.test(node))
                remove(node);
    }

    public void clear() {
        this.instructions.clear();
        this.first = null;
        this.last = null;
    }

    public int count(final Predicate<Node> predicate) {
        int count = 0;

        for (final Node node : this)
            if (predicate.test(node))
                count++;

        return count;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(final Node node) {
        return instructions.contains(node);
    }

    public int indexOf(final Node node) {
        return instructions.indexOf(node);
    }

    public int size() {
        return instructions.size();
    }

    public void accept(final MethodVisitor visitor) {
        if (Global.POSTPROCESSING) Processor.process(this, Processor.Mode.POST);
        for (final Node node : this)
            node.accept(visitor);
    }

    public Node[] toArray() {
        return instructions.toArray(new Node[0]);
    }

    public void toArray(final Node[] arr) {
        instructions.toArray(arr);
    }

    @NotNull
    @Override
    public Iterator<Node> iterator() {
        return new Iterator<>() {
            private Node current = first;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Node next() {
                if (!hasNext()) throw new IllegalStateException("not good");
                final Node temp = current;
                current = current.next;
                return temp;
            }
        };
    }
}