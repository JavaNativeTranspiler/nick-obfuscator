package dev.name.asm.ir.extensions.processor;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Module;
import dev.name.asm.ir.components.Record;
import dev.name.asm.ir.components.*;
import dev.name.asm.ir.instructions.Instructions;
import org.objectweb.asm.Opcodes;

import java.util.LinkedList;
import java.util.List;

import static dev.name.asm.ir.extensions.processor.Processor.*;

@SuppressWarnings("unused")
public sealed abstract class Processor<T> implements Opcodes permits
        AnnotationProcessor,
        ClassProcessor,
        FieldProcessor,
        InstructionProcessor,
        MethodProcessor,
        ModuleProcessor,
        RecordProcessor
{
    public static int
        ANNOTATION =    0b0000001,
        CLASS =         0b0000010,
        FIELD =         0b0000100,
        INSTRUCTION =   0b0001000,
        METHOD =        0b0010000,
        MODULE =        0b0100000,
        RECORD =        0b1000000;

    public enum Mode {
        PRE,
        POST
    }

    private static final List<Processor<?>> processors = new LinkedList<>();

    public static void register(final Processor<?> processor) {
        processors.add(processor);
    }

    @SuppressWarnings("unchecked")
    public static <T> void process(final T type, final Mode mode) {
        processors.forEach(processor -> {
            if (!processor.applicable(type)) return;
            process((Processor<T>) processor, type, mode);
        });
    }

    public static <T> void process(final Processor<T> processor, final T type, final Mode mode) {
        if (mode == Mode.PRE) processor.pre(type);
        else processor.post(type);
    }

    public abstract int type();
    public abstract void pre(final T type);
    public abstract void post(final T type);
    protected abstract boolean applicable(final Object obj);

    public static non-sealed abstract class AnnotationProcessor extends Processor<Annotation> {
        @Override public final int type() { return Processor.ANNOTATION; }
        @Override protected final boolean applicable(final Object obj) { return obj instanceof Annotation; }
    }

    public static non-sealed abstract class ClassProcessor extends Processor<Class> {
        @Override public final int type() { return Processor.CLASS; }
        @Override protected final boolean applicable(final Object obj) { return obj instanceof Class; }
    }

    public static non-sealed abstract class FieldProcessor extends Processor<Field> {
        @Override public final int type() { return Processor.FIELD; }
        @Override protected final boolean applicable(final Object obj) { return obj instanceof Field; }
    }

    public static non-sealed abstract class InstructionProcessor extends Processor<Instructions> {
        @Override public final int type() { return Processor.INSTRUCTION; }
        @Override protected final boolean applicable(final Object obj) { return obj instanceof Instructions; }
    }

    public static non-sealed abstract class MethodProcessor extends Processor<Method> {
        @Override public final int type() { return Processor.METHOD; }
        @Override protected final boolean applicable(final Object obj) { return obj instanceof Method; }
    }

    public static non-sealed abstract class ModuleProcessor extends Processor<Module> {
        @Override public final int type() { return Processor.MODULE; }
        @Override protected final boolean applicable(final Object obj) { return obj instanceof Module; }
    }

    public static non-sealed abstract class RecordProcessor extends Processor<Record> {
        @Override public final int type() { return Processor.RECORD; }
        @Override protected final boolean applicable(final Object obj) { return obj instanceof Record; }
    }
}