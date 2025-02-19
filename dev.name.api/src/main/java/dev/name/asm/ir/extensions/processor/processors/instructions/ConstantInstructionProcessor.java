package dev.name.asm.ir.extensions.processor.processors.instructions;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.types.Node;
import dev.name.util.asm.Bytecode;

public final class ConstantInstructionProcessor extends Processor.InstructionProcessor {
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
            if (!Bytecode.isConstant(node)) continue;
            if (node instanceof Constant) continue;
            Constant constant = rewrite(node);
            patch(constant);
            node.replace(constant);
        }
    }

    private static void patch(final Constant constant) {
        constant.cst = constant.cst instanceof Boolean b ? (b ? 1 : 0) : constant.cst instanceof Character c ? (int) c : constant.cst;
    }

    private static Constant rewrite(final Node node) {
        return new Constant(switch (node.opcode) {
            case ACONST_NULL -> null;
            case ICONST_M1 -> -1;
            case ICONST_0 -> 0;
            case ICONST_1 -> 1;
            case ICONST_2 -> 2;
            case ICONST_3 -> 3;
            case ICONST_4 -> 4;
            case ICONST_5 -> 5;
            case LCONST_0 -> 0L;
            case LCONST_1 -> 1L;
            case FCONST_0 -> 0F;
            case FCONST_1 -> 1F;
            case FCONST_2 -> 2F;
            case DCONST_0 -> 0D;
            case DCONST_1 -> 1D;
            default -> throw new IllegalStateException("shouldnt reach here: " + node.opcode);
        });
    }
}