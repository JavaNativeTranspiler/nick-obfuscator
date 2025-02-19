package dev.name.asm.ir.extensions.processor.processors.methods;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.nodes.Variable;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import org.objectweb.asm.Type;

import java.util.HashSet;
import java.util.Set;

import static dev.name.util.asm.Bytecode.resolveParameterIndexes;

public final class LocalInitializerProcessor extends Processor.MethodProcessor {
    @Override public void pre(final Method method) {}

    @Override
    public void post(final Method method) {
        final Instructions instructions = method.instructions;
        final Node first = instructions.first;

        final Set<Integer> visited = new HashSet<>();
        int base = method.access.isStatic() ? 0 : 1;
        if (base != 0) visited.add(0);
        for (final int n : resolveParameterIndexes(Type.getArgumentTypes(method.desc))) visited.add(base + n);

        for (final Node node : instructions) {
            if (!(node instanceof Variable variable)) continue;
            if (visited.contains(variable.index)) continue;

            first.insertBefore(new Constant(LocalPool.initializer(variable.type)));
            first.insertBefore(new Variable(LocalPool.store(variable.type), variable.index));

            visited.add(variable.index);
        }
    }
}