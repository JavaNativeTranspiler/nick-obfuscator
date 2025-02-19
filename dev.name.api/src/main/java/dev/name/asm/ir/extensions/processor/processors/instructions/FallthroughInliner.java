package dev.name.asm.ir.extensions.processor.processors.instructions;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Jump;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.types.Node;

public class FallthroughInliner extends Processor.InstructionProcessor {
    @Override
    public void pre(Instructions type) {
        process(type);
    }

    @Override
    public void post(Instructions type) {
        process(type);
    }

    private void process(Instructions instructions) {
        instructions.forEach(node -> node instanceof Label, node -> {
            Label label = (Label) node;
            Node prev = label.previous;

            if (prev == null) return;
            if (!(prev instanceof Jump jump)) return;
            if (jump.opcode != GOTO) return;
            if (jump.label != label) return;
            jump.delete();
        });
    }
}