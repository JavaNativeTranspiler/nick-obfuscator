package dev.name.asm.ir.components;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.nodes.Label;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Annotation extends AnnotationVisitor implements Opcodes {
    public String desc;
    public boolean visible;
    public ArrayList<Object> args = new ArrayList<>();

    public static class Type extends Annotation {
        public boolean visible;
        public int ref;
        public TypePath path;

        public Type(final int ref, final TypePath path, final String desc, final boolean visible) {
            super(desc, visible);
            this.ref = ref;
            this.path = path;
            this.visible = visible;
        }
    }

    public static final class Local extends Type {
        public List<Label> start;
        public List<Label> end;
        public List<Integer> index;

        public Local(final int ref, final TypePath path, final Label[] start, final Label[] end, final int[] index, final String desc, final boolean visible) {
            super(ref, path, desc, visible);
            this.start = new ArrayList<>(List.of(start));
            this.end = new ArrayList<>(List.of(end));
            this.index = IntStream.of(index).boxed().collect(Collectors.toList());
        }

        public void accept(final MethodVisitor visitor) {
            accept(visitor.visitLocalVariableAnnotation(this.ref, this.path, this.start.stream().map(Label::form).toArray(org.objectweb.asm.Label[]::new), this.end.stream().map(Label::form).toArray(org.objectweb.asm.Label[]::new), this.index.stream().mapToInt(Integer::intValue).toArray(), this.desc, this.visible));
        }
    }

    public Annotation(final String desc, final boolean visible) {
        super(ASM9);
        this.desc = desc;
        this.visible = visible;
    }

    public Annotation(final Object[] args, final boolean visible) {
        super(ASM9);
        this.args = new ArrayList<>(List.of(args));
        this.visible = visible;
    }

    public Annotation(final List<Object> args, final boolean visible) {
        super(ASM9);
        this.args = new ArrayList<>(args);
        this.visible = visible;
    }

    public Annotation(final ArrayList<Object> args, final boolean visible) {
        super(ASM9);
        this.args = args;
        this.visible = visible;
    }

    public Annotation(final String desc, final Object[] args, final boolean visible) {
        super(ASM9);
        this.desc = desc;
        this.args = new ArrayList<>(List.of(args));
        this.visible = visible;
    }

    public Annotation(final String desc, final List<Object> args, final boolean visible) {
        super(ASM9);
        this.desc = desc;
        this.args = new ArrayList<>(List.of(args));
        this.visible = visible;
    }

    public Annotation(final String desc, final ArrayList<Object> args, final boolean visible) {
        super(ASM9);
        this.desc = desc;
        this.args = args;
        this.visible = visible;
    }

    @Override
    public void visit(final String name, final Object value) {
        if (this.desc != null) args.add(name);

        switch (value.getClass().getName()) {
            case "[B" -> args.add(Arrays.asList((Object[]) value));
            case "[Z" -> args.add(Arrays.asList(ArrayUtils.toObject((boolean[]) value)));
            case "[S" -> args.add(Arrays.asList(ArrayUtils.toObject((short[]) value)));
            case "[C" -> args.add(Arrays.asList(ArrayUtils.toObject((char[]) value)));
            case "[I" -> args.add(Arrays.asList(ArrayUtils.toObject((int[]) value)));
            case "[J" -> args.add(Arrays.asList(ArrayUtils.toObject((long[]) value)));
            case "[F" -> args.add(Arrays.asList(ArrayUtils.toObject((float[]) value)));
            case "[D" -> args.add(Arrays.asList(ArrayUtils.toObject((double[]) value)));
            default -> args.add(value);
        }
    }

    @Override
    public void visitEnum(final String name, final String descriptor, final String value) {
        if (this.desc != null) args.add(name);
        args.add(new String[]{descriptor, value});
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        if (this.desc != null) args.add(name);
        final Annotation annotation = new Annotation(descriptor, true);
        args.add(annotation);
        return annotation;
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        if (this.desc != null) args.add(name);
        final ArrayList<Object> arr = new ArrayList<>();
        this.args.add(arr);
        return new Annotation(arr, true);
    }

    @Override
    public void visitEnd() {
        if (Global.PREPROCESSING) Processor.process(this, Processor.Mode.PRE);
    }

    public void accept(final AnnotationVisitor visitor) {
        if (Global.POSTPROCESSING) Processor.process(this, Processor.Mode.POST);
        for (int i = 0; i < args.size(); i += 2) accept(visitor, (String) args.get(i), args.get(i + 1));
        visitor.visitEnd();
    }

    public static void accept(final AnnotationVisitor visitor, final String name, final Object value) {
        if (value instanceof String[] type) visitor.visitEnum(name, type[0], type[1]);
        else if (value instanceof Annotation annotation) annotation.accept(visitor.visitAnnotation(name, annotation.desc));
        else if (value instanceof List<?> arr) {
            final AnnotationVisitor annotation = visitor.visitArray(name);
            arr.forEach(element -> accept(annotation, null, element));
            annotation.visitEnd();
        } else visitor.visit(name, value);
    }
}