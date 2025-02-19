package dev.name.transpiler.types;

import org.objectweb.asm.Type;

import java.util.Locale;

public class Parameter {
    public final JNIType type;
    public final String name;

    public Parameter(final JNIType type, final String name) {
        this.type = type;
        this.name = name;
    }

    public Parameter(final int index, final String desc) {
        this.type = JNIType.parse(desc);
        this.name = String.format("param_%d_%s", index, this.type.name().toLowerCase(Locale.ENGLISH));
    }

    public Parameter(final int index, final Type type) {
        this(index, type.getDescriptor());
    }

    @Override
    public String toString() {
        return String.format("%s %s", type.desc, name);
    }
}