package dev.name.asm.ir.types;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

public class Bootstrap {
    public int tag;
    public String owner, name, desc;
    public boolean _interface;

    public Bootstrap(final int tag, final String owner, final String name, final String desc, final boolean _interface) {
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this._interface = _interface;
    }

    public Bootstrap(final int tag, final String owner, final String name, final String desc) {
        this(tag, owner, name, desc, tag == Opcodes.H_INVOKEINTERFACE);
    }

    public static Bootstrap of(final Handle handle) {
        return new Bootstrap(handle.getTag(), handle.getOwner(), handle.getName(), handle.getDesc(), handle.isInterface());
    }

    public Handle form() {
        return new Handle(this.tag, this.owner, this.name, this.desc, this._interface);
    }
}