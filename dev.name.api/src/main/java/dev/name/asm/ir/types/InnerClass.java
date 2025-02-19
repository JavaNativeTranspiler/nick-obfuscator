package dev.name.asm.ir.types;

import org.objectweb.asm.ClassVisitor;

public class InnerClass {
    public String name, outer, inner;
    public Access access;

    public InnerClass(final String name, final String outer, final String inner, final Access access) {
        this.name = name;
        this.outer = outer;
        this.inner = inner;
        this.access = access;
    }

    public InnerClass(final String name, final String outer, final String inner, final int access) {
        this(name, outer, inner, new Access(access));
    }

    public void accept(final ClassVisitor visitor) {
        if (access == null) throw new IllegalStateException();
        visitor.visitInnerClass(name, outer, inner, access.getAccess());
    }
}