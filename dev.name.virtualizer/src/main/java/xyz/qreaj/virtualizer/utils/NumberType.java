package xyz.qreaj.virtualizer.utils;

public enum NumberType {
    BOOLEAN,
    BYTE,
    SHORT,
    CHAR,
    INT,
    FLOAT,
    LONG,
    DOUBLE;

    public static NumberType type(final Object obj) {
        if (obj == null) throw new IllegalArgumentException("invalid obj");

        return switch (obj.getClass().getName()) {
            case "java.lang.Boolean" -> NumberType.BOOLEAN;
            case "java.lang.Byte" -> NumberType.BYTE;
            case "java.lang.Short" -> NumberType.SHORT;
            case "java.lang.Character" -> NumberType.CHAR;
            case "java.lang.Integer" -> NumberType.INT;
            case "java.lang.Float" -> NumberType.FLOAT;
            case "java.lang.Long" -> NumberType.LONG;
            case "java.lang.Double" -> NumberType.DOUBLE;
            default -> throw new IllegalArgumentException("bad type: " + obj.getClass().getName());
        };
    }
}