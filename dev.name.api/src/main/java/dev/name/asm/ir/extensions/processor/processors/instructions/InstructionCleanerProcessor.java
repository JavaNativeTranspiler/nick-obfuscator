package dev.name.asm.ir.extensions.processor.processors.instructions;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Frame;
import dev.name.asm.ir.nodes.Line;
import dev.name.asm.ir.types.Node;

public final class InstructionCleanerProcessor extends Processor.InstructionProcessor {
    @Override
    public void pre(final Instructions instructions) {
        process(instructions);
    }

    @Override
    public void post(final Instructions instructions) {
        process(instructions);
    }

    private void process(final Instructions instructions) {
        for (final Node node : instructions)
            if (node instanceof Line || node instanceof Frame || node.opcode == NOP)
                node.delete();
    }
}