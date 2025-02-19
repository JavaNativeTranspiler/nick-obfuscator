package dev.name.asm.ir.types;

import dev.name.asm.ir.nodes.Label;
import org.objectweb.asm.MethodVisitor;

public class Local {
    public String name, desc, signature;
    public Label begin, end;
    public int index;

    public Local(final String name, final String desc, final String signature, final Label begin, final Label end, final int index) {
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.begin = begin;
        this.end = end;
        this.index = index;
    }

    public Local(final String name, final String desc, final String signature) {
        this.name = name;
        this.desc = desc;
        this.signature = signature;
    }

    public Local(final int index) {
        this.index = index;
    }

    public Local(final Label begin, final Label end) {
        this.begin = begin;
        this.end = end;
    }

    public void accept(final MethodVisitor visitor) {
        if (begin == null || end == null) throw new IllegalStateException();
        if (index < 0) throw new IllegalStateException();
        visitor.visitLocalVariable(this.name, this.desc, this.signature, this.begin.form(), this.end.form(), this.index);
    }
}