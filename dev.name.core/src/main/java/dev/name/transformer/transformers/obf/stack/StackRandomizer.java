package dev.name.transformer.transformers.obf.stack;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import dev.name.util.asm.Bytecode;
import dev.name.transformer.Transformer;
import dev.name.transformer.transformers.obf.stack.impl.StackRandomizerPOP;
import dev.name.util.java.ClassPool;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.HashMap;
import java.util.Map;

public class StackRandomizer extends Transformer {
    @Override
    public String name() {
        return "StackRandomizer";
    }


    @Override
    @SneakyThrows
    public void transform(ClassPool pool) {
        for (Class klass : pool.getClasses()) {
            for (Method method : klass.methods) {
                final Frame<BasicValue>[] frames = Bytecode.analyze(method);
                final Map<Node, Frame<BasicValue>> frame_map = new HashMap<>();
                final Node[] arr = method.instructions.toArray();

                for (int i = 0; i < frames.length; i++) frame_map.put(arr[i], frames[i]);

                LocalPool locals = new LocalPool(method);
                for (Node node : method.instructions) {
                    StackRandomizerPOP.apply(node,frame_map,locals);
                }
            }
        }
    }
}
