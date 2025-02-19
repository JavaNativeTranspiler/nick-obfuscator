package dev.name.transformer.transformers.obf.generic;

import dev.name.asm.ir.nodes.Accessor;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.nodes.Invoke;
import dev.name.transformer.Transformer;
import dev.name.util.java.ClassPool;

public class MethodEntryDebugger extends Transformer {
    @Override
    public String name() {
        return "Initializer debugger";
    }

    @Override
    public void transform(final ClassPool pool) {
        pool.forEach(klass -> klass.methods.forEach(method -> {
            method.instructions.insert(new Invoke(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
            method.instructions.insert(new Constant(method.klass.name + "." + method.name + " " + method.desc));
            method.instructions.insert(new Accessor(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        }));
    }
}