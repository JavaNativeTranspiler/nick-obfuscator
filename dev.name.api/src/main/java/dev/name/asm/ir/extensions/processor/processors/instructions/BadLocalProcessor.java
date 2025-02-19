package dev.name.asm.ir.extensions.processor.processors.instructions;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Variable;
import dev.name.asm.ir.types.Node;
import dev.name.util.asm.Bytecode;

public final class BadLocalProcessor extends Processor.InstructionProcessor {
    @Override
    public void pre(final Instructions instructions) {
        process(instructions);
    }

    @Override
    public void post(final Instructions instructions) {
        process(instructions);
    }

    private void process(final Instructions instructions) {
        for (final Node node : instructions) {
            if (!(node instanceof Variable load)) continue;
            if (!(node.next instanceof Variable store)) continue;
            if (load.index != store.index) continue;
            if (Bytecode.isLoad(store) || Bytecode.isStore(load)) continue;
            load.delete();
            store.delete();
        }
    }
}