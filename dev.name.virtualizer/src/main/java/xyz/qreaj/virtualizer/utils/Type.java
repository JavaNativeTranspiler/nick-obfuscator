package xyz.qreaj.virtualizer.utils;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class Type {
    public static final int
            VOID = 0,
            BOOLEAN = 1,
            CHAR = 2,
            BYTE = 3,
            SHORT = 4,
            INT = 5,
            FLOAT = 6,
            LONG = 7,
            DOUBLE = 8,
            ARRAY = 9,
            OBJECT = 10,
            METHOD = 11;

    public static final Type
            VOID_TYPE = new Type(VOID, null, ('V' << 24) | (5 << 16) | (0), 1),
            BOOLEAN_TYPE = new Type(BOOLEAN, null, ('Z' << 24) | (0) | (5 << 8) | 1, 1),
            CHAR_TYPE = new Type(CHAR, null, ('C' << 24) | (0) | (6 << 8) | 1, 1),
            BYTE_TYPE = new Type(BYTE, null, ('B' << 24) | (0) | (5 << 8) | 1, 1),
            SHORT_TYPE = new Type(SHORT, null, ('S' << 24) | (0) | (7 << 8) | 1, 1),
            INT_TYPE = new Type(INT, null, ('I' << 24) | (0) | 1, 1),
            FLOAT_TYPE = new Type(FLOAT, null, ('F' << 24) | (2 << 16) | (2 << 8) | 1, 1),
            LONG_TYPE = new Type(LONG, null, ('J' << 24) | (1 << 16) | (1 << 8) | 2, 1),
            DOUBLE_TYPE = new Type(DOUBLE, null, ('D' << 24) | (3 << 16) | (3 << 8) | 2, 1);

    @Getter
    private final int sort, off, len;
    private final char[] buf;

    private Type(final int sort, final char[] buf, final int off, final int len) {
        this.sort = sort;
        this.buf = buf;
        this.off = off;
        this.len = len;
    }

    public static Type getType(final String typeDescriptor) {
        return getType(typeDescriptor.toCharArray(), 0);
    }

    public static Type getObjectType(final String internalName) {
        char[] buf = internalName.toCharArray();
        return new Type(buf[0] == '[' ? ARRAY : OBJECT, buf, 0, buf.length);
    }

    public static Type getMethodType(final String methodDescriptor) {
        return getType(methodDescriptor.toCharArray(), 0);
    }

    public static Type getMethodType(final Type returnType,
                                     final Type... argumentTypes) {
        return getType(getMethodDescriptor(returnType, argumentTypes));
    }

    public static Type getType(final Class<?> c) {
        if (c.isPrimitive()) {
            if (c == Integer.TYPE) {
                return INT_TYPE;
            } else if (c == Void.TYPE) {
                return VOID_TYPE;
            } else if (c == Boolean.TYPE) {
                return BOOLEAN_TYPE;
            } else if (c == Byte.TYPE) {
                return BYTE_TYPE;
            } else if (c == Character.TYPE) {
                return CHAR_TYPE;
            } else if (c == Short.TYPE) {
                return SHORT_TYPE;
            } else if (c == Double.TYPE) {
                return DOUBLE_TYPE;
            } else if (c == Float.TYPE) {
                return FLOAT_TYPE;
            } else {
                return LONG_TYPE;
            }
        } else {
            return getType(getDescriptor(c));
        }
    }

    public static Type getType(final Constructor<?> c) {
        return getType(getConstructorDescriptor(c));
    }

    public static Type getType(final Method m) {
        return getType(getMethodDescriptor(m));
    }

    public static Type[] getArgumentTypes(final String methodDescriptor) {
        char[] buf = methodDescriptor.toCharArray();
        int off = 1;
        int size = 0;
        while (true) {
            char car = buf[off++];
            if (car == ')') {
                break;
            } else if (car == 'L') {
                while (buf[off++] != ';') {}
                ++size;
            } else if (car != '[') {
                ++size;
            }
        }
        Type[] args = new Type[size];
        off = 1;
        size = 0;
        while (buf[off] != ')') {
            args[size] = getType(buf, off);
            off += args[size].len + (args[size].sort == OBJECT ? 2 : 0);
            size += 1;
        }
        return args;
    }

    public static Type[] getArgumentTypes(final Method method) {
        Class<?>[] classes = method.getParameterTypes();
        Type[] types = new Type[classes.length];
        for (int i = classes.length - 1; i >= 0; --i) {
            types[i] = getType(classes[i]);
        }
        return types;
    }

    public static Type getReturnType(final String methodDescriptor) {
        char[] buf = methodDescriptor.toCharArray();
        return getType(buf, methodDescriptor.indexOf(')') + 1);
    }

    public static Type getReturnType(final Method method) {
        return getType(method.getReturnType());
    }

    public static int getArgumentsAndReturnSizes(final String desc) {
        int n = 1;
        int c = 1;
        while (true) {
            char car = desc.charAt(c++);
            if (car == ')') {
                car = desc.charAt(c);
                return n << 2
                        | (car == 'V' ? 0 : (car == 'D' || car == 'J' ? 2 : 1));
            } else if (car == 'L') {
                while (desc.charAt(c++) != ';') {}
                n += 1;
            } else if (car == '[') {
                while ((car = desc.charAt(c)) == '[') {
                    ++c;
                }
                if (car == 'D' || car == 'J') {
                    n -= 1;
                }
            } else if (car == 'D' || car == 'J') {
                n += 2;
            } else {
                n += 1;
            }
        }
    }

    private static Type getType(final char[] buf, final int off) {
        int len;
        switch (buf[off]) {
            case 'V': return VOID_TYPE;
            case 'Z': return BOOLEAN_TYPE;
            case 'C': return CHAR_TYPE;
            case 'B': return BYTE_TYPE;
            case 'S': return SHORT_TYPE;
            case 'I': return INT_TYPE;
            case 'F': return FLOAT_TYPE;
            case 'J': return LONG_TYPE;
            case 'D': return DOUBLE_TYPE;
            case '[':
                len = 1;
                while (buf[off + len] == '[') ++len;
                if (buf[off + len] == 'L') {
                    ++len;
                    while (buf[off + len] != ';') ++len;
                }
                return new Type(ARRAY, buf, off, len + 1);
            case 'L':
                len = 1;
                while (buf[off + len] != ';') {
                    ++len;
                }
                return new Type(OBJECT, buf, off + 1, len - 1);
            default: return new Type(METHOD, buf, off, buf.length - off);
        }
    }

    public int getDimensions() {
        int i = 1;
        while (buf[off + i] == '[') {
            ++i;
        }
        return i;
    }

    public Type getElementType() {
        return getType(buf, off + getDimensions());
    }

    public String getClassName() {
        return switch (sort) {
            case VOID -> "void";
            case BOOLEAN -> "boolean";
            case CHAR -> "char";
            case BYTE -> "byte";
            case SHORT -> "short";
            case INT -> "int";
            case FLOAT -> "float";
            case LONG -> "long";
            case DOUBLE -> "double";
            case ARRAY -> getElementType().getClassName() + "[]".repeat(Math.max(0, getDimensions()));
            case OBJECT -> new String(buf, off, len).replace('/', '.');
            default -> null;
        };
    }

    public String getInternalName() {
        return new String(buf, off, len);
    }

    public Type[] getArgumentTypes() {
        return getArgumentTypes(getDescriptor());
    }

    public Type getReturnType() {
        return getReturnType(getDescriptor());
    }

    public int getArgumentsAndReturnSizes() {
        return getArgumentsAndReturnSizes(getDescriptor());
    }

    public String getDescriptor() {
        StringBuffer buf = new StringBuffer();
        getDescriptor(buf);
        return buf.toString();
    }

    public static String getMethodDescriptor(final Type returnType,
                                             final Type... argumentTypes) {
        StringBuffer buf = new StringBuffer();
        buf.append('(');
        for (Type argumentType : argumentTypes) argumentType.getDescriptor(buf);
        buf.append(')');
        returnType.getDescriptor(buf);
        return buf.toString();
    }

    private void getDescriptor(final StringBuffer buf) {
        if (this.buf == null) {

            buf.append((char) ((off & 0xFF000000) >>> 24));
        } else if (sort == OBJECT) {
            buf.append('L');
            buf.append(this.buf, off, len);
            buf.append(';');
        } else {
            buf.append(this.buf, off, len);
        }
    }

    public static String getInternalName(final Class<?> c) {
        return c.getName().replace('.', '/');
    }

    public static String getDescriptor(final Class<?> c) {
        StringBuffer buf = new StringBuffer();
        getDescriptor(buf, c);
        return buf.toString();
    }

    public static String getConstructorDescriptor(final Constructor<?> c) {
        Class<?>[] parameters = c.getParameterTypes();
        StringBuffer buf = new StringBuffer();
        buf.append('(');
        for (final Class<?> parameter : parameters) getDescriptor(buf, parameter);
        return buf.append(")V").toString();
    }

    public static String getMethodDescriptor(final Method m) {
        Class<?>[] parameters = m.getParameterTypes();
        StringBuffer buf = new StringBuffer();
        buf.append('(');
        for (final Class<?> parameter : parameters) getDescriptor(buf, parameter);
        buf.append(')');
        getDescriptor(buf, m.getReturnType());
        return buf.toString();
    }

    private static void getDescriptor(final StringBuffer buf, final Class<?> c) {
        Class<?> d = c;
        while (true) {
            if (d.isPrimitive()) {
                switch (c.getName()) {
                    case "int" -> buf.append('I');
                    case "void" -> buf.append('V');
                    case "boolean" -> buf.append('Z');
                    case "byte" -> buf.append('B');
                    case "char" -> buf.append('C');
                    case "short" -> buf.append('S');
                    case "double" -> buf.append('D');
                    case "float" -> buf.append('F');
                    case "long" -> buf.append('J');
                    default -> throw new IllegalArgumentException("Unknown primitive type: " + c.getName());
                }
                return;
            } else if (d.isArray()) {
                buf.append('[');
                d = d.getComponentType();
            } else {
                buf.append('L');
                String name = d.getName();
                int len = name.length();
                for (int i = 0; i < len; ++i) {
                    char car = name.charAt(i);
                    buf.append(car == '.' ? '/' : car);
                }
                buf.append(';');
                return;
            }
        }
    }

    public int getSize() {

        return buf == null ? (off & 0xFF) : 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Type type)) return false;
        if (sort != type.sort) return false;

        if (sort >= ARRAY) {
            if (len != type.len) return false;
            for (int i = off, j = type.off, end = i + len; i < end; i++, j++) {
                if (buf[i] != type.buf[j]) return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hc = 13 * sort;

        if (sort >= ARRAY)
            for (int i = off, end = i + len; i < end; i++)
                hc = 17 * (hc + buf[i]);

        return hc;
    }

    @Override
    public String toString() {
        return getDescriptor();
    }
}