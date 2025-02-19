package dev.name.asm.ir.extensions.processor.processors.instructions;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Instruction;
import dev.name.asm.ir.types.Node;

public final class StackFolderProcessor extends Processor.InstructionProcessor {
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
            final Node next = node.next;
            if (next == null) continue;
            switch (node.opcode) {
                case DUP -> {
                    switch (next.opcode) {
                        case POP -> instructions.remove(node, next);
                        case POP2 -> { node.delete(); next.replace(new Instruction(POP)); }
                        case SWAP -> next.delete();
                    }
                }
                case POP -> {
                    if (next.opcode != POP) continue;
                    node.delete();
                    next.replace(new Instruction(POP2));
                }
                case SWAP -> {
                    if (next.opcode != SWAP) continue;
                    instructions.remove(node, next);
                }
            }
        }
    }
}