package dev.name.asm.ir.components;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.nodes.Accessor;
import dev.name.asm.ir.types.Access;
import dev.name.asm.ir.types.Flags;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.List;

public class Field extends FieldVisitor implements Opcodes {
    public Class klass;
    public Access access;
    public String name, desc, signature;
    public Object value;
    //
    public List<Annotation> annotations = new ArrayList<>();
    public List<Annotation.Type> typeAnnotations = new ArrayList<>();
    public List<Attribute> attributes = new ArrayList<>();
    //
    public Flags flags = new Flags();

    public Field() {
        super(ASM9);
    }

    public Field(final Access access, final String name, final String desc, final String signature, final Object value) {
        super(ASM9);
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.value = value;
    }

    public Field(final int access, final String name, final String desc, final String signature, final Object value) {
        this(new Access(access), name, desc, signature, value);
    }

    public Handle getterHandle() {
        return new Handle(this.access.isStatic() ? H_GETSTATIC : H_PUTSTATIC, this.klass.name, this.name, this.desc, this.klass.access.isInterface());
    }

    public Handle setterHandle() {
        return new Handle(this.access.isStatic() ? H_PUTSTATIC : H_PUTFIELD, this.klass.name, this.name, this.desc, this.klass.access.isInterface());
    }

    public Accessor getter() {
        return new Accessor(this.access.isStatic() ? GETSTATIC : GETFIELD, this.klass.name, this.name, this.desc);
    }

    public Accessor setter() {
        return new Accessor(this.access.isStatic() ? PUTSTATIC : PUTFIELD, this.klass.name, this.name, this.desc);
    }

    public boolean matches(final Accessor accessor) {
        return accessor.name.equals(this.name) && accessor.desc.equals(this.desc) && accessor.owner.equals(this.klass.name);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        final Annotation annotation = new Annotation(desc, visible);
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
        if (visitor == null || access == null) throw new IllegalStateException();
        final FieldVisitor field = visitor.visitField(access.getAccess(), name, desc, signature, value);
        if (field == null) throw new IllegalStateException();
        for (final Annotation annotation : annotations) annotation.accept(field.visitAnnotation(annotation.desc, annotation.visible));
        for (final Annotation.Type typeAnnotation : typeAnnotations) typeAnnotation.accept(field.visitTypeAnnotation(typeAnnotation.ref, typeAnnotation.path, typeAnnotation.desc, typeAnnotation.visible));
        for (final Attribute attribute : attributes) field.visitAttribute(attribute);
        field.visitEnd();
    }
}