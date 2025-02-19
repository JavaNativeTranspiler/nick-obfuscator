package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

public final class Constant extends Node {
    public Object cst;

    public Constant(final Object cst) {
        super(-1);
        this.cst = cst;
    }

    @Override
    public int type() {
        return Node.CONSTANT;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (cst == null) {
            visitor.visitInsn(ACONST_NULL);
            super.annotations(visitor);
            return;
        }

        Object adjusted = cst instanceof Character c ? (int) c : cst instanceof Boolean b ? (b ? 1 : 0) : cst;

        if (!(adjusted instanceof Number num)) {
            visitor.visitLdcInsn(cst);
            super.annotations(visitor);
            return;
        }

        switch (num.getClass().getSimpleName()) {
            case "Integer", "Short", "Byte" -> {
                int v = num.intValue();

                switch (v) {
                    case -1 -> visitor.visitInsn(ICONST_M1);
                    case 0 -> visitor.visitInsn(ICONST_0);
                    case 1 -> visitor.visitInsn(ICONST_1);
                    case 2 -> visitor.visitInsn(ICONST_2);
                    case 3 -> visitor.visitInsn(ICONST_3);
                    case 4 -> visitor.visitInsn(ICONST_4);
                    case 5 -> visitor.visitInsn(ICONST_5);
                    default -> {
                        if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) visitor.visitIntInsn(BIPUSH, v);
                        else if (v >= Short.MIN_VALUE && v <= Short.MAX_VALUE) visitor.visitIntInsn(SIPUSH, v);
                        else visitor.visitLdcInsn(cst);
                    }
                }
            }
            case "Double" -> {
                double d = num.doubleValue();
                if (d == 0.0D) visitor.visitInsn(DCONST_0);
                else if (d == 1.0D) visitor.visitInsn(DCONST_1);
                else visitor.visitLdcInsn(cst);
            }
            case "Long" -> {
                long l = num.longValue();
                if (l == 0L) visitor.visitInsn(LCONST_0);
                else if (l == 1L) visitor.visitInsn(LCONST_1);
                else visitor.visitLdcInsn(cst);
            }
            case "Float" -> {
                float f = num.floatValue();
                if (f == 0.0F) visitor.visitInsn(FCONST_0);
                else if (f == 1.0F) visitor.visitInsn(FCONST_1);
                else if (f == 2.0F) visitor.visitInsn(FCONST_2);
                else visitor.visitLdcInsn(cst);
            }
        }

        super.annotations(visitor);
    }
}