package dev.name.asm.ir.extensions.processor.processors.methods;

import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.Node;

public final class JunkExceptionProcessor extends Processor.MethodProcessor {
    @Override
    public void pre(Method method) {
        apply(method);
    }

    @Override
    public void post(Method method) {
        apply(method);
    }

    private void apply(Method method) {
        for (Block block : method.blocks.toArray(new Block[0])) {
            Label handler = block.handler;
            if (handler == null) continue; // ?
            Node start = handler.next;
            if (start == null || start.opcode != ATHROW) continue;
            method.blocks.remove(block);
        }
    }
}