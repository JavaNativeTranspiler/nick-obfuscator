package dev.name.asm.ir.nodes;

import dev.name.asm.ir.types.Node;
import org.objectweb.asm.MethodVisitor;

public final class Line extends Node {
    public int line;
    public Label start;

    public Line() {
        super(-1);
    }

    public Line(final int line, final Label start) {
        super(-1);
        this.line = line;
        this.start = start;
    }

    public Line(final int line) {
        super(-1);
        this.line = line;
    }

    public Line(final Label start) {
        super(-1);
        this.start = start;
    }

    @Override
    public int type() {
        return Node.LINE;
    }

    @Override
    public void accept(final MethodVisitor visitor) {
        if (start == null) throw new IllegalStateException();
        visitor.visitLineNumber(this.line, this.start.form());
    }
}