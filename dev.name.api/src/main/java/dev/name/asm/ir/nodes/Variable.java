package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("unused")
public final class Variable extends Node {
    public int index;
    public int type;

    public Variable(final int opcode, final int index) {
        super(opcode);
        this.index = index;
        this.type = LocalPool.translate(opcode);
    }

    @Override
    public int type() {
        return Node.VARIABLE;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (index < 0) throw new IndexOutOfBoundsException();
        visitor.visitVarInsn(this.opcode, this.index);
        super.annotations(visitor);
    }
}