package dev.name.transformer.transformers.deobf.future;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Field;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.nodes.Accessor;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.nodes.Invoke;
import dev.name.asm.ir.types.Node;
import dev.name.transformer.Transformer;
import dev.name.util.java.ClassPool;

import java.util.ArrayList;
import java.util.List;

public class ConstantFixer extends Transformer {
    @Override
    public String name() {
        return "lol";
    }

    @Override
    public void transform(final ClassPool pool) {
        final List<Class> removed = new ArrayList<>();

        for (final Class klass : pool) {
            final String wrapper = klass.name + "$0";
            final String desc = String.format("L%s;", wrapper);
            final String lambda = klass.name + "$$Lambda$1";
            final Field f = klass.fields.stream().filter(field -> field.desc.equals(desc)).findFirst().orElse(null);
            if (f == null) continue;
            final Class __lambda = pool.get(lambda);
            final Class __wrapper = pool.get(wrapper);
            if (__lambda == null || __wrapper == null) throw new IllegalStateException("impossible: " + desc);

            for (final Method method : klass.methods) {
                for (final Node node : method.instructions.toArray()) {
                    if (!(node instanceof Accessor accessor)) continue;
                    if (!accessor.desc.equals(desc)) continue;
                    if (!(accessor.next instanceof Invoke invoke)) throw new IllegalStateException("impossible: " + desc);
                    final Method constant = __lambda.getMethod(invoke.name, invoke.desc);
                    if (constant == null) throw new IllegalStateException("impossible: " + desc);
                    final Node first = constant.instructions.first;
                    if (!(first instanceof Constant cst)) throw new IllegalStateException("impossible: " + desc);
                    accessor.delete();
                    invoke.replace(new Constant(cst.cst));
                }
            }

            removed.add(__lambda);
            removed.add(__wrapper);
            klass.fields.remove(f);
        }

        removed.forEach(pool::remove);
    }
}