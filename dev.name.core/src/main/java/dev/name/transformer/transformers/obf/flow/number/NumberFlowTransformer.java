package dev.name.transformer.transformers.obf.flow.number;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.types.LocalPool;
import dev.name.transformer.Transformer;
import dev.name.util.java.ClassPool;
import dev.name.util.math.Random;

public class NumberFlowTransformer extends Transformer implements Random {
    @Override
    public String name() {
        return "Number Flow Transformer";
    }

    @Override
    public void transform(final ClassPool pool) {
        for (final Class klass : pool)
            for (final Method method : klass.methods) {
                if (method.access.isAbstract() || method.access.isNative()) continue;
                if (method.instructions.size() <= 0) continue;
                if (method.instructions.count(node -> node instanceof Constant constant && constant.cst instanceof Number) == 0) continue;
                NumberFlow.apply(method, new LocalPool(method).allocate(LocalPool.LONG).index, nextLong());
            }
    }
}