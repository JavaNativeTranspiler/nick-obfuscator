package dev.name.asm.ir.types;

import org.objectweb.asm.MethodVisitor;

@SuppressWarnings("unused")
public final class Parameter {
    public String name;
    public Access access;

    public Parameter(final String name, final int access) {
        this.name = name;
        this.access = new Access(access);
    }

    public Parameter(final String name, final Access access) {
        this.access = access;
    }

    public Parameter(final String name) {
        this.name = name;
    }

    public Parameter(final int access) {
        this.access = new Access(access);
    }

    public Parameter(final Access access) {
        this.access = access;
    }

    public void accept(final MethodVisitor visitor) {
        if (this.access == null) throw new IllegalStateException();
        visitor.visitParameter(this.name, this.access.getAccess());
    }
}