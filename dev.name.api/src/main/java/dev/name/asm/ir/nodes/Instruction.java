package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

public final class Instruction extends Node {
    public Instruction(final int opcode) {
        super(opcode);
    }

    @Override
    public int type() {
        return Node.INSTRUCTION;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (opcode < 0) throw new IndexOutOfBoundsException();
        visitor.visitInsn(this.opcode);
        super.annotations(visitor);
    }
}