package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.JumpInsnNode;

public final class Jump extends Node {
    // asm doesn't optimize this out for some reason
    private static final int GOTO_W = 200;
    public Label label;

    public Jump(final int opcode) {
        super(opcode);
    }

    public Jump(final int opcode, final Label label) {
        super(opcode == GOTO_W ? GOTO : opcode);
        this.label = label;
    }

    public boolean conditional() {
        return opcode != GOTO && opcode != GOTO_W;
    }

    public boolean unconditional() {
        return opcode == GOTO || opcode == GOTO_W;
    }

    @Override
    public int type() {
        return Node.JUMP;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (label == null) throw new IllegalStateException();
        visitor.visitJumpInsn(this.opcode, this.label.form());
        super.annotations(visitor);
    }
}