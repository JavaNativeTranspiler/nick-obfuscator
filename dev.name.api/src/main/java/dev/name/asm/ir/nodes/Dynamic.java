package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Bootstrap;
import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

public final class Dynamic extends Node {
    public String name, desc;
    public Bootstrap bootstrap;
    public Object[] args;

    public Dynamic() {
        super(INVOKEDYNAMIC);
    }

    public Dynamic(final String name, final String desc, final Bootstrap bootstrap, Object[] args) {
        super(INVOKEDYNAMIC);
        this.name = name;
        this.desc = desc;
        this.bootstrap = bootstrap;
        this.args = args;
    }

    public Dynamic(final String name, final String desc) {
        this(name, desc, null, null);
    }

    public Dynamic(final Bootstrap bootstrap) {
        this(null, null, bootstrap, null);
    }

    public Dynamic(final Object[] args) {
        this(null, null, null, args);
    }

    @Override
    public int type() {
        return Node.DYNAMIC;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (name == null || desc == null) throw new IllegalStateException();
        if (args == null || bootstrap == null) throw new IllegalStateException();
        if (bootstrap.owner == null || bootstrap.name == null || bootstrap.desc == null) throw new IllegalStateException();
        visitor.visitInvokeDynamicInsn(this.name, this.desc, this.bootstrap.form(), this.args);
        super.annotations(visitor);
    }
}