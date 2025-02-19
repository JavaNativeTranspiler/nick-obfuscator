package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("unused")
public final class Type extends Node {
    public String desc;

    public Type(final int opcode) {
        super(opcode);
    }

    public Type(final int opcode, final String desc) {
        super(opcode);
        this.desc = desc;
    }

    @Override
    public int type() {
        return Node.TYPE;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (desc == null) throw new IllegalStateException();
        visitor.visitTypeInsn(this.opcode, this.desc);
        super.annotations(visitor);
    }
}