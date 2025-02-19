package dev.name.asm.ir.components;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.types.Flags;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.List;

public class Record extends RecordComponentVisitor implements Opcodes {
    public Class klass;
    public String name, desc, signature;
    //
    public List<Annotation> annotations = new ArrayList<>();
    public List<Annotation.Type> typeAnnotations = new ArrayList<>();
    public List<Attribute> attributes = new ArrayList<>();
    //
    public Flags flags = new Flags();

    public Record() {
        super(ASM9);
    }

    public Record(final String name, final String desc, final String signature) {
        super(ASM9);
        this.name = name;
        this.desc = desc;
        this.signature = signature;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        final Annotation annotation = new Annotation(descriptor, visible);
        annotations.add(annotation);
        return annotation;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int ref, final TypePath path, final String desc, final boolean visible) {
        final Annotation.Type annotation = new Annotation.Type(ref, path, desc, visible);
        typeAnnotations.add(annotation);
        return annotation;
    }

    @Override
    public void visitAttribute(final Attribute attribute) {
        attributes.add(attribute);
    }

    @Override
    public void visitEnd() {
        if (Global.PREPROCESSING) Processor.process(this, Processor.Mode.PRE);
    }

    public void accept(final ClassVisitor visitor) {
        if (Global.POSTPROCESSING) Processor.process(this, Processor.Mode.POST);
        if (visitor == null) throw new IllegalStateException();
        final RecordComponentVisitor record = visitor.visitRecordComponent(name, desc, signature);
        if (record == null) throw new IllegalStateException();
        for (final Annotation annotation : annotations) annotation.accept(record.visitAnnotation(annotation.desc, annotation.visible));
        for (final Annotation.Type typeAnnotation : typeAnnotations) typeAnnotation.accept(record.visitTypeAnnotation(typeAnnotation.ref, typeAnnotation.path, typeAnnotation.desc, typeAnnotation.visible));
        for (final Attribute attribute : attributes) record.visitAttribute(attribute);
        record.visitEnd();
    }
}