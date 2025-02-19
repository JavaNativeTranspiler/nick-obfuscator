package dev.name.asm.ir.types;

import dev.name.asm.ir.components.Annotation;
import dev.name.asm.ir.nodes.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

public class Block {
    public List<Annotation.Type> annotations = new ArrayList<>();

    public Label start, end, handler;
    public String type;

    public Block(final String type) {
        this.type = type;
    }

    public Block(final Label start, final Label end, final Label handler, final String type) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }

    public void index(final int index) {
        final int ref = 0x42000000 | (index << 8);
        for (final Annotation.Type annotation : annotations) annotation.ref = ref;
    }

    public void accept(final MethodVisitor visitor) {
        visitor.visitTryCatchBlock(this.start.form(), this.end.form(), this.handler.form(), this.type);
        for (final Annotation.Type annotation : annotations) annotation.accept(visitor.visitInsnAnnotation(annotation.ref, annotation.path, annotation.desc, annotation.visible));
    }
}