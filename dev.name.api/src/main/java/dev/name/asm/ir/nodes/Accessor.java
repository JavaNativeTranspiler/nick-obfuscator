package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

public final class Accessor extends Node {
    public String owner, name, desc;

    public Accessor(final int opcode) {
        super(opcode);
    }

    public Accessor(final int opcode, final String owner, final String name, final String desc) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public int type() {
        return Node.FIELD;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (owner == null || name == null || desc == null) throw new IllegalStateException();
        visitor.visitFieldInsn(this.opcode, this.owner, this.name, this.desc);
        super.annotations(visitor);
    }
}