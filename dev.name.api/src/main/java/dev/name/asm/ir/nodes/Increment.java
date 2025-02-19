package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

public final class Increment extends Node {
    public Variable local;
    public int amount;

    public Increment(final Variable local, final int amount) {
        super(IINC);
        this.local = local;
        this.amount = amount;
    }

    @Override
    public int type() {
        return Node.INCREMENT;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (local.index < 0) throw new IndexOutOfBoundsException();
        visitor.visitIincInsn(this.local.index, this.amount);
        super.annotations(visitor);
    }
}