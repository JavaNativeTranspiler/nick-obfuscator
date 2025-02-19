package dev.name.transformer.transformers.obf.generic;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.transformer.Transformer;
import dev.name.util.java.ClassPool;
import org.objectweb.asm.Type;

public class ParameterHiderTransformer extends Transformer {
    @Override
    public String name() {
        return "Parameter Hider Transformer";
    }

    @Override
    public void transform(final ClassPool pool) {
        for (final Class klass : pool) {

        }
    }

    private Instructions generator(final Type[] args) {
return null;
    }

    private void fixInvokes(final Instructions generator) {

    }

    private void apply(final Method method) {
        final String desc = method.desc;
        final Type[] types = Type.getArgumentTypes(desc);
        method.desc = "([Ljava/lang/Object;)";

        for (final Type type : types) {
            if (type.equals(Type.LONG_TYPE) || type.equals(Type.DOUBLE_TYPE));
        }
    }
}