package dev.name.transpiler.types;

import org.objectweb.asm.Type;

public enum JNIType {
    BOOLEAN("jboolean"),
    BYTE("jbyte"),
    SHORT("jshort"),
    CHAR("jchar"),
    INTEGER("jint"),
    FLOAT("jfloat"),
    OBJECT("jobject"),
    LONG("jlong"),
    DOUBLE("jdouble"),
    ARRAY("jarray"),
    BOOLEAN_ARRAY("jbooleanArray"),
    BYTE_ARRAY("jbyteArray"),
    SHORT_ARRAY("jshortArray"),
    CHAR_ARRAY("jcharArray"),
    INT_ARRAY("jintArray"),
    FLOAT_ARRAY("jfloatArray"),
    OBJECT_ARRAY("jobjectArray"),
    LONG_ARRAY("jlongArray"),
    DOUBLE_ARRAY("jdoubleArray"),
    CLASS("jclass"),
    FIELD("jfieldID"),
    METHOD("jmethodID"),
    THROWABLE("jthrowable"),
    STRING("jstring"),
    VOID("void");

    public final String desc;

    JNIType(final String desc) {
        this.desc = desc;
    }

    private static int dimensions(final String str) {
        int dimensions = 0;
        for (char c : str.toCharArray()) if (c == '[') dimensions++;
        return dimensions;
    }

    public static JNIType parse(final Type type) {
        final int sort = type.getSort();
        final String desc = type.getDescriptor();

        if (sort == Type.ARRAY) {
            final int dimensions = dimensions(desc);
            if (dimensions > 1) return OBJECT_ARRAY;
            final char base = desc.charAt(dimensions);

            return switch (base) {
                case 'Z' -> BOOLEAN_ARRAY;
                case 'B' -> BYTE_ARRAY;
                case 'S' -> SHORT_ARRAY;
                case 'C' -> CHAR_ARRAY;
                case 'I' -> INT_ARRAY;
                case 'F' -> FLOAT_ARRAY;
                case 'L' -> OBJECT_ARRAY;
                case 'J' -> LONG_ARRAY;
                case 'D' -> DOUBLE_ARRAY;
                default -> throw new IllegalArgumentException("bad array type: " + desc);
            };
        }

        return switch (sort) {
            case Type.BOOLEAN -> BOOLEAN;
            case Type.BYTE -> BYTE;
            case Type.SHORT -> SHORT;
            case Type.CHAR -> CHAR;
            case Type.INT -> INTEGER;
            case Type.FLOAT -> FLOAT;
            case Type.OBJECT -> OBJECT;
            case Type.LONG -> LONG;
            case Type.DOUBLE -> DOUBLE;
            case Type.VOID -> VOID;
            default -> throw new IllegalArgumentException("bad type: " + type);
        };
    }

    public static JNIType parse(final String desc) {
        return parse(Type.getType(desc));
    }
}