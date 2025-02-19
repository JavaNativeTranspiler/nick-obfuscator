package dev.name.asm.ir.types;

import dev.name.asm.ir.components.Annotation;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class Node implements Opcodes {
    // these bit values have a future purpose probably
    public static final int
        FIELD       = 0b0000000000000001,
        ARRAY       = 0b0000000000000010,
        CONSTANT    = 0b0000000000000100,
        DYNAMIC     = 0b0000000000001000,
        FRAME       = 0b0000000000010000,
        INCREMENT   = 0b0000000000100000,
        INSTRUCTION = 0b0000000001000000,
        METHOD      = 0b0000000010000000,
        JUMP        = 0b0000000100000000,
        LABEL       = 0b0000001000000000,
        LINE        = 0b0000010000000000,
        LOOKUP      = 0b0000100000000000,
        TABLE       = 0b0001000000000000,
        TYPE        = 0b0010000000000000,
        VARIABLE    = 0b0100000000000000,
        PRIM_ARRAY  = 0b1000000000000000;

    public Method method;
    public Instructions parent;
    //
    public Node previous, next;
    public int opcode;
    public List<Annotation.Type> annotations = new ArrayList<>();
    //
    public Flags flags = new Flags();

    protected Node(final int opcode) {
        this.opcode = opcode;
    }

    public abstract void accept(final MethodVisitor visitor);
    public abstract int type();

    protected final void annotations(final MethodVisitor visitor) {
        for (final Annotation.Type annotation : annotations)
            annotation.accept(visitor.visitInsnAnnotation(annotation.ref, annotation.path, annotation.desc, annotation.visible));
    }

    public int index() {
        check();
        return parent.indexOf(this);
    }

    public void delete() {
        check();
        parent.remove(this);
    }

    public void replace(final Node replacement) {
        check();
        parent.set(this, replacement);
    }

    public void replace(final Instructions replacement) {
        check();
        parent.set(this, replacement);
    }

    public void insertBefore(final Node node) {
        check();
        parent.insertBefore(this, node);
    }

    public void insertAfter(final Node node) {
        check();
        parent.insertAfter(this, node);
    }

    public void insertBefore(final Instructions instructions) {
        check();
        parent.insertBefore(this, instructions);
    }

    public void insertAfter(final Instructions instructions) {
        check();
        parent.insertAfter(this, instructions);
    }

    private void check() {
        if (parent == null) throw new IllegalArgumentException("tried operating on a node that wasnt in a list...");
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Node node && this == node;
    }
}