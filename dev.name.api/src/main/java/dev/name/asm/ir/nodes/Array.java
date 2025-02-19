package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class Array extends Node {
    public String desc;
    public int dimensions = -1;

    public Array() {
        super(MULTIANEWARRAY);
    }

    public Array(final String desc, final int dimensions) {
        super(MULTIANEWARRAY);
        this.desc = desc;
        this.dimensions = dimensions;
    }

    public Array(final int dimensions) {
        super(MULTIANEWARRAY);
        this.dimensions = dimensions;
    }

    public Array(final String desc) {
        super(MULTIANEWARRAY);
        this.desc = desc;
    }

    @Override
    public int type() {
        return Node.ARRAY;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (dimensions < 1) throw new IndexOutOfBoundsException();
        if (desc == null) throw new IllegalStateException();
        visitor.visitMultiANewArrayInsn(this.desc, this.dimensions);
        super.annotations(visitor);
    }

    public static class Primitive extends Node implements Opcodes {
        public int type;

        public Primitive(final int type) {
            super(NEWARRAY);
            this.type = type;
        }

        @Override
        public int type() {
            return Node.PRIM_ARRAY;
        }

        @Override
        public void accept(final MethodVisitor visitor) {
            visitor.visitIntInsn(NEWARRAY, this.type);
        }
    }
}