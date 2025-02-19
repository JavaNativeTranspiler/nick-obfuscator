package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

public final class Label extends Node {
    private final org.objectweb.asm.Label inst;

    public Label() {
        super(-1);
        this.inst = new org.objectweb.asm.Label();
    }

    public Label(final org.objectweb.asm.Label inst) {
        super(-1);
        this.inst = inst;
    }

    @Override
    public int type() {
        return Node.LABEL;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (inst == null) throw new IllegalStateException();
        visitor.visitLabel(this.form());
    }

    public Jump jump(final int opcode) {
        return new Jump(opcode, this);
    }

    public org.objectweb.asm.Label form() {
        return this.inst;
    }
}