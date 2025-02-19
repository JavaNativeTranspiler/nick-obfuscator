package dev.name.asm.ir.types;

public class Descriptor {
    public static String BOOLEAN = "Z";
    public static String BYTE = "B";
    public static String SHORT = "S";
    public static String CHARACTER = "C";
    public static String INTEGER = "I";
    public static String FLOAT = "F";
    public static String LONG = "J";
    public static String DOUBLE = "D";

    public static String method(final Class<?> ret, final Class<?>... args) {
        final StringBuilder descriptor = new StringBuilder("(");
        for (final Class<?> arg : args) descriptor.append(getTypeDescriptor(arg));
        return descriptor.append(')').append(getTypeDescriptor(ret)).toString();
    }

    public static String of(final Class<?> klass) {
        return getTypeDescriptor(klass);
    }

    private static String getTypeDescriptor(final Class<?> klass) {
        if (klass.isArray()) return "[" + getTypeDescriptor(klass.getComponentType());

        if (klass.isPrimitive()) {
            return switch (klass.getName()) {
                case "boolean" -> BOOLEAN;
                case "byte" -> BYTE;
                case "short" -> SHORT;
                case "char" -> CHARACTER;
                case "int" -> INTEGER;
                case "float" -> FLOAT;
                case "long" -> LONG;
                case "double" -> DOUBLE;
                default -> throw new IllegalArgumentException("bad: " + klass.getName());
            };
        }

        return "L" + klass.getName().replace('.', '/') + ";";
    }
}
