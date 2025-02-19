package dev.name.transpiler.builder;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.transpiler.types.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TranspiledClass implements Iterable<TranspiledMethod> {
    private final List<TranspiledMethod> methods = new ArrayList<>();
    private final Class klass;
    public final Registry registry;

    private TranspiledClass(final Class klass) {
        for (Method method : klass.methods) {
            if (method.name.equals("<clinit>") || method.name.equals("<init>")) continue;
            methods.add(TranspiledMethod.create(this, method));
        }

        this.klass = klass;
        this.registry = Registry.create(this);
    }

    public static TranspiledClass create(final Class klass) {
        return new TranspiledClass(klass);
    }

    @NotNull
    @Override
    public Iterator<TranspiledMethod> iterator() {
        return methods.iterator();
    }
}