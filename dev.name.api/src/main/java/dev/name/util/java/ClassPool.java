package dev.name.util.java;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.types.Flags;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

@Getter
public class ClassPool implements Iterable<Class> {
    private final ArrayList<Class> classes = new ArrayList<>();
    private final ArrayList<Class> libs = new ArrayList<>();

    public void add(final Class klass) {
        classes.add(klass);
    }

    public void remove(final Class klass) {
        classes.remove(klass);
    }

    public void addLib(final Class klass) {
        libs.add(klass);
        klass.flags.set(Flags.Class.LIBRARY, true);
    }

    public void removeLib(final Class klass) {
        libs.remove(klass);
    }

    public Class get(final String name) {
        return Stream.concat(classes.stream(), libs.stream()).filter(node -> node.name.equals(name)).findFirst().orElse(null);
    }

    public Class getCommonSuperclass(final Class k1, final Class k2) {
        if (k1 == null || k2 == null) throw new IllegalStateException("invalid commonsuperclass");
        if (k1.equals(k2)) return k1;

        Class s1 = get(k1.superName), s2 = get(k2.superName);
        if (s1 == null || s2 == null) throw new IllegalStateException();

        while (!s1.equals(s2)) {

        }

        return null;
    }

    public boolean has(final String name) {
        return get(name) != null;
    }

    @NotNull
    @Override
    public Iterator<Class> iterator() {
        return classes.iterator();
    }

    @Override
    public String toString() {
        return classes.toString();
    }
}