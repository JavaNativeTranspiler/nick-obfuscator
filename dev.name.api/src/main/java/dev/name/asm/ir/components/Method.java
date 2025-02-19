package dev.name.asm.ir.components;

import com.google.common.collect.ImmutableSet;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.Frame;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.nodes.Type;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.pattern.Pattern;
import dev.name.asm.ir.types.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Method extends MethodVisitor implements Opcodes {
    public Class klass;
    public Access access;
    public String name, desc, signature;
    public Method superMethod;
    public boolean mutable;
    //
    public List<Attribute> attributes = new ArrayList<>();
    public List<Parameter> parameters = new ArrayList<>();
    public List<String> exceptions = new ArrayList<>();
    public List<Local> locals = new ArrayList<>();
    public List<Block> blocks = new ArrayList<>();
    //
    public Object annotationDefault;
    public List<Annotation> annotations = new ArrayList<>();
    public List<Annotation.Local> localAnnotations = new ArrayList<>();
    public List<List<Annotation>> parameterAnnotations = new LinkedList<>();
    public List<Annotation.Type> typeAnnotations = new ArrayList<>();
    //
    public Instructions instructions;
    //
    public int maxStack, maxLocals;
    //
    public Flags flags = new Flags();

    public Method() {
        super(ASM9);
    }

    public Method(final Access access, final String name, final String desc, final String signature, final List<String> exceptions) {
        super(ASM9);
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions.addAll(exceptions);
        setInstructions(new Instructions());
    }

    public Method(final Access access, final String name, final String desc, final String signature, final String[] exceptions) {
        this(access, name, desc, signature, exceptions != null ? List.of(exceptions) : List.of());
    }

    public Method(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        this(new Access(access), name, desc, signature, exceptions);
    }

    public void setInstructions(final Instructions instructions) {
        this.instructions = instructions;
        this.instructions.setMethod(this);
    }

    public ImmutableSet<Pattern.Range> match(final Pattern pattern) {
        return pattern.match_all(this);
    }

    public Handle handle() {
        final boolean isInterface = this.klass.access.isInterface();
        final boolean isStatic = this.access.isStatic();
        final boolean isConstructor = this.name.equals("<init>");
        return new Handle(isConstructor ? H_INVOKESPECIAL : isInterface ? H_INVOKEINTERFACE : isStatic ? H_INVOKESTATIC : H_INVOKEVIRTUAL, this.klass.name, this.name, this.desc, this.klass.access.isInterface());
    }

    public Invoke invoker() {
        final boolean isInterface = this.klass.access.isInterface();
        final boolean isStatic = this.access.isStatic();
        final boolean isConstructor = this.name.equals("<init>");
        final String owner = this.klass.name;
        final String name = this.name;
        final String desc = this.desc;
        final int opcode = isStatic ? INVOKESTATIC : isConstructor ? INVOKESPECIAL : isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL;
        return new Invoke(opcode, owner, name, desc, isInterface);
    }

    public boolean matches(final Invoke invoke) {
        return this.name.equals(invoke.name) && this.desc.equals(invoke.desc) && this.klass.name.equals(invoke.name);
    }

    @Override
    public void visitParameter(final String name, final int access) {
        parameters.add(new Parameter(name, access));
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new Annotation(new ObjectArrayList<>()
                {
                    @Override
                    public boolean add(final Object o) {
                        annotationDefault = o;
                        return super.add(o);
                    }
                }, true
        );
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

    @Override public void visitAnnotableParameterCount(final int parameterCount, final boolean visible) {}

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int param, final String desc, final boolean visible) {
        final Annotation annotation = new Annotation(desc, visible);
        while (parameterAnnotations.size() <= param) parameterAnnotations.add(new ArrayList<>());
        if (parameterAnnotations.get(param) == null) parameterAnnotations.set(param, new ArrayList<>());
        parameterAnnotations.get(param).add(annotation);
        return annotation;
    }

    @Override
    public void visitAttribute(final Attribute attribute) {
        attributes.add(attribute);
    }

    @Override public void visitCode() {}

    @Override
    public void visitFrame(final int type, final int numLocal, final Object[] local, final int numStack, final Object[] stack) {
        //instructions.add(new Frame(type, numLocal, local, numStack, stack));
    }

    @Override
    public void visitInsn(final int opcode) {
        instructions.add(new Instruction(opcode));
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        if (opcode == NEWARRAY) instructions.add(new Array.Primitive(operand));
        else instructions.add(new Constant(operand));
    }

    @Override
    public void visitVarInsn(final int opcode, final int index) {
        instructions.add(new Variable(opcode, index));
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        instructions.add(new Type(opcode, type));
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        instructions.add(new Accessor(opcode, owner, name, desc));
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean _interface) {
        instructions.add(new Invoke(opcode & ~256, owner, name, desc, _interface));
    }

    @Override
    public void visitInvokeDynamicInsn(final String name, final String desc, final Handle bootstrap, final Object... args) {
        instructions.add(new Dynamic(name, desc, Bootstrap.of(bootstrap), args));
    }

    @Override
    public void visitJumpInsn(final int opcode, final org.objectweb.asm.Label label) {
        instructions.add(new Jump(opcode, label(label)));
    }

    @Override
    public void visitLabel(final org.objectweb.asm.Label label) {
        instructions.add(label(label));
    }

    @Override
    public void visitLdcInsn(final Object value) {
        instructions.add(new Constant(value));
    }

    @Override
    public void visitIincInsn(final int index, final int increment) {
        // REPLACE with VarPool...
        instructions.add(new Increment(new Variable(ISTORE, index), increment));
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max, final org.objectweb.asm.Label dflt, final org.objectweb.asm.Label... labels) {
        instructions.add(new Table(min, max, label(dflt), labels(labels)));
    }

    @Override
    public void visitLookupSwitchInsn(final org.objectweb.asm.Label dflt, final int[] keys, final org.objectweb.asm.Label[] labels) {
        instructions.add(new Lookup(label(dflt), keys, labels(labels)));
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dimensions) {
        instructions.add(new Array(desc, dimensions));
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(final int ref, final TypePath path, final String desc, final boolean visible) {
        Node instruction = instructions.last;
        while (instruction.opcode == -1) instruction = instruction.previous;
        final Annotation.Type annotation = new Annotation.Type(ref, path, desc, visible);
        instruction.annotations.add(annotation);
        return annotation;
    }

    @Override
    public void visitTryCatchBlock(final org.objectweb.asm.Label start, final org.objectweb.asm.Label end, final org.objectweb.asm.Label handler, final String type) {
        blocks.add(new Block(label(start), label(end), label(handler), type));
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(final int ref, final TypePath path, final String desc, final boolean visible) {
        final Block block = blocks.get((ref & 0x00FFFF00) >> 8);
        final Annotation.Type annotation = new Annotation.Type(ref, path, desc, visible);
        block.annotations.add(annotation);
        return annotation;
    }

    @Override
    public void visitLocalVariable(final String name, final String desc, final String signature, final org.objectweb.asm.Label start, final org.objectweb.asm.Label end, final int index) {
        locals.add(new Local(name, desc, signature, label(start), label(end), index));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(final int ref, final TypePath path, final org.objectweb.asm.Label[] start, final org.objectweb.asm.Label[] end, final int[] index, final String desc, final boolean visible) {
        final Annotation.Local annotation = new Annotation.Local(ref, path, labels(start), labels(end), index, desc, visible);
        localAnnotations.add(annotation);
        return annotation;
    }

    @Override
    public void visitLineNumber(final int line, final org.objectweb.asm.Label start) {
        instructions.add(new Line(line, label(start)));
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
    }

    @Override
    public void visitEnd() {
        if (Global.PREPROCESSING) {
            Processor.process(this, Processor.Mode.PRE);
            Processor.process(this.instructions, Processor.Mode.PRE);
        }
    }

    protected Label label(final org.objectweb.asm.Label label) {
        if (!(label.info instanceof Label)) label.info = new Label();
        return (Label) label.info;
    }

    private Label[] labels(final org.objectweb.asm.Label[] labels) {
        final Label[] nodes = new Label[labels.length];
        for (int i = 0, n = nodes.length; i < n; ++i) nodes[i] = label(labels[i]);
        return nodes;
    }

    public void accept(final ClassVisitor visitor) {
        if (access == null) throw new IllegalStateException();
        accept(visitor.visitMethod(access.getAccess(), name, desc, signature, this.exceptions == null ? null : this.exceptions.toArray(new String[0])));
    }

    public void accept(final MethodVisitor visitor) {
        if (Global.POSTPROCESSING) Processor.process(this, Processor.Mode.POST);
        for (final Parameter parameter : parameters) parameter.accept(visitor);
        if (annotationDefault != null) {
            final AnnotationVisitor annotation = visitor.visitAnnotationDefault();
            Annotation.accept(annotation, null, annotationDefault);
            annotation.visitEnd();
        }
        for (final Annotation annotation : annotations) annotation.accept(visitor.visitAnnotation(annotation.desc, annotation.visible));
        for (final Annotation.Type typeAnnotation : typeAnnotations) typeAnnotation.accept(visitor.visitTypeAnnotation(typeAnnotation.ref, typeAnnotation.path, typeAnnotation.desc, typeAnnotation.visible));
        int params = 0;
        for (final List<Annotation> parameters : parameterAnnotations) {
            for (final Annotation annotation : parameters) annotation.accept(visitor.visitParameterAnnotation(params, annotation.desc, annotation.visible));
            params++;
        }
        for (final Attribute attribute : attributes) visitor.visitAttribute(attribute);
        if (instructions.size() > 0 && !access.isAbstract() && !access.isNative()) {
            visitor.visitCode();
            int index = 0;
            for (final Block block : blocks) {
                block.index(index++);
                block.accept(visitor);
            }
            instructions.accept(visitor);
            for (final Local local : locals) local.accept(visitor);
            for (final Annotation.Local localAnnotation : localAnnotations) localAnnotation.accept(visitor);
            visitor.visitMaxs(maxStack, maxLocals);
        }
        visitor.visitEnd();
    }
}