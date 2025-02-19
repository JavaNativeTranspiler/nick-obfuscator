package dev.name.asm.ir.types;

import java.util.HashMap;
import java.util.Map;

public class Flags {
    private final Map<Integer, Object> flags = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T get(final int flag) {
        return (T) flags.get(flag);
    }

    public void set(final int flag, final Object val) {
        flags.put(flag, val);
    }

    public void clear(final int flag) {
        flags.remove(flag);
    }

    public boolean has(final int flag) {
        return flags.get(flag) != null;
    }

    public static final class Class {
        public static int LIBRARY = 0;
    }

    public static final class Instruction {
        public static int WRAPPED_CONDITION = 0x497454; // was this conditional statement wrapped
        public static int HANDLER = 0x34295771; // is this a label that is a exception handler
    }

    public static final class Field {}
    public static final class Method {}
    public static final class Module {}
    public static final class Record {}
}