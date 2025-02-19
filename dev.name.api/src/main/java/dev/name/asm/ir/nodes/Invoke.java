package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

public final class Invoke extends Node {
    public String owner, name, desc;
    public boolean _interface;

    public Invoke(final int opcode) {
        super(opcode);
    }

    public Invoke(final int opcode, final String owner, final String name, final String desc, final boolean _interface) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this._interface = _interface;
    }

    public Invoke(final int opcode, final String owner, final String name, final String desc) {
        super(opcode);
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this._interface = opcode == INVOKEINTERFACE;
    }

    @Override
    public int type() {
        return Node.METHOD;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (owner == null || name == null || desc == null) throw new IllegalStateException();
        visitor.visitMethodInsn(this.opcode, this.owner, this.name, this.desc, this._interface);
        super.annotations(visitor);
    }
}