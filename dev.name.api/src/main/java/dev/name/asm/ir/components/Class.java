package dev.name.asm.ir.components;

import com.google.common.collect.ImmutableSet;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.nodes.Constant;
import dev.name.asm.ir.pattern.Pattern;
import dev.name.asm.ir.types.Access;
import dev.name.asm.ir.types.Flags;
import dev.name.asm.ir.types.InnerClass;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.List;

public class Class extends ClassVisitor implements Opcodes {
    public int version;
    public byte[] buf;
    public Access access;
    public String name, signature, superName;
    public String sourceFile, sourceDebug;
    public String outerClass, outerMethod, outerMethodDesc;
    public String nestHostClass;
    public Module module;
    //
    public List<Method> methods = new ArrayList<>();
    public List<Field> fields = new ArrayList<>();
    public List<Record> records = new ArrayList<>();
    public List<Annotation> annotations = new ArrayList<>();
    public List<Annotation.Type> typeAnnotations = new ArrayList<>();
    public List<Attribute> attributes = new ArrayList<>();
    public List<InnerClass> innerClasses = new ArrayList<>();
    public List<String> nestMembers = new ArrayList<>();
    public List<String> permitted = new ArrayList<>();
    public List<String> interfaces = new ArrayList<>();
    //
    public Flags flags = new Flags();

    public Class() {
        super(ASM9);
    }

    public ImmutableSet<Pattern.Range> match(final Pattern pattern) {
        return pattern.match_all(this);
    }

    public void addField(final Field field) {
        field.klass = this;
        fields.add(field);
    }

    public void addMethod(final Method method) {
        method.klass = this;
        methods.add(method);
    }

    public void addRecord(final Record record) {
        record.klass = this;
        records.add(record);
    }

    public Method getMethod(final String name, final String desc) {
        return methods.stream().filter(method -> method.name.equals(name) && method.desc.equals(desc)).findFirst().orElse(null);
    }

    public Field getField(final String name, final String desc) {
        return fields.stream().filter(field -> field.name.equals(name) && field.desc.equals(desc)).findFirst().orElse(null);
    }

    public Type type() {
        return Type.getType(String.format("L%s;", this.name));
    }

    public Constant constant() {
        return new Constant(type());
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.version = version;
        this.access = new Access(access);
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = new ArrayList<>(List.of(interfaces));
    }

    @Override
    public void visitSource(final String file, final String debug) {
        sourceFile = file;
        sourceDebug = debug;
    }

    @Override
    public ModuleVisitor visitModule(final String name, final int access, final String version) {
        this.module = new Module(name, access, version);
        return module;
    }

    @Override
    public void visitNestHost(final String host) {
        this.nestHostClass = host;
    }

    @Override
    public void visitOuterClass(final String owner, final String name, final String descriptor) {
        outerClass = owner;
        outerMethod = name;
        outerMethodDesc = descriptor;
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
    public void visitNestMember(final String member) {
        nestMembers.add(member);
    }

    @Override
    public void visitPermittedSubclass(final String subclass) {
        permitted.add(subclass);
    }

    @Override
    public void visitInnerClass(final String name, final String outer, final String inner, final int access) {
        innerClasses.add(new InnerClass(name, outer, inner, new Access(access)));
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(final String name, final String desc, final String signature) {
        final Record record = new Record(name, desc, signature);
        addRecord(record);
        return record;
    }

    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        final Field field = new Field(access, name, desc, signature, value);
        addField(field);
        return field;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final Method method = new Method(access, name, desc, signature, exceptions);
        addMethod(method);
        return method;
    }

    @Override
    public void visitEnd() {
        if (Global.PREPROCESSING) Processor.process(this, Processor.Mode.PRE);
    }

    public void accept(final ClassVisitor visitor) {
        if (Global.POSTPROCESSING) Processor.process(this, Processor.Mode.POST);
        if (access == null) throw new IllegalStateException();
        visitor.visit(version, access.getAccess(), name, signature, superName, this.interfaces.toArray(new String[0]));
        if (sourceFile != null || sourceDebug != null && !Global.SKIP_DEBUG) visitor.visitSource(sourceFile, sourceDebug);
        if (module != null) module.accept(visitor);
        if (nestHostClass != null) visitor.visitNestHost(nestHostClass);
        if (outerClass != null) visitor.visitOuterClass(outerClass, outerMethod, outerMethodDesc);
        for (final Annotation annotation : annotations) annotation.accept(visitor.visitAnnotation(annotation.desc, annotation.visible));
        for (final Annotation.Type typeAnnotation : typeAnnotations) typeAnnotation.accept(visitor.visitTypeAnnotation(typeAnnotation.ref, typeAnnotation.path, typeAnnotation.desc, typeAnnotation.visible));
        for (final Attribute attribute : attributes) visitor.visitAttribute(attribute);
        for (final String member : nestMembers) visitor.visitNestMember(member);
        for (final String permit : permitted) visitor.visitPermittedSubclass(permit);
        for (final InnerClass innerClass : innerClasses) innerClass.accept(visitor);
        for (final Record record : records) record.accept(visitor);
        for (final Field field : fields) field.accept(visitor);
        for (final Method method : methods) method.accept(visitor);
        visitor.visitEnd();
    }
}