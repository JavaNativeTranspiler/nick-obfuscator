package dev.name.asm.ir.extensions.attr;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassWriter;

public class HashAttribute extends Attribute {
    private final ByteVector vector;

    public HashAttribute(final ByteVector vector) {
        super("Hash");
        this.vector = vector;
    }

    @Override
    protected ByteVector write(final ClassWriter cw, final byte[] code, final int len, final int stack, final int locals) {
        return vector;
    }

    public static void main(String[] args) {
        System.out.println(-0x2e23f061d7a4e98cL);
    }
}