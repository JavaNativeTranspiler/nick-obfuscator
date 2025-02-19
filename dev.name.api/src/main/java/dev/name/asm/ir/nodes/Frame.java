package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;

public final class Frame extends Node {
    public int type;
    public Object[] local, stack;

    public Frame() {
        super(-1);
    }

    public Frame(final int type, final int numLocal, final Object[] local, final int numStack, final Object[] stack) {
        super(-1);
        this.type = type;
        this.local = Arrays.copyOf(local, numLocal);
        this.stack = type == F_SAME1 ? new Object[]{stack[0]} : Arrays.copyOf(stack, numStack);
    }

    @Override
    public int type() {
        return Node.FRAME;
    }

    @Override
    public void accept(MethodVisitor methodVisitor) {
        switch (type) {
            case F_NEW, F_FULL -> methodVisitor.visitFrame(type, local.length, local, stack.length, stack);
            case F_APPEND -> methodVisitor.visitFrame(type, local.length, local, 0, null);
            case F_CHOP -> methodVisitor.visitFrame(type, local.length, null, 0, null);
            case F_SAME -> methodVisitor.visitFrame(type, 0, null, 0, null);
            case F_SAME1 -> methodVisitor.visitFrame(type, 0, null, 1, stack);
        }
    }
}