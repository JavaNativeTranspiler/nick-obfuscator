package dev.name.util.asm;

import java.lang.reflect.Field;

public class Opcodes {
    private static String[] opcodes;

    static {
        try {
            final Field[] fields = org.objectweb.asm.Opcodes.class.getDeclaredFields();
            final int len = fields.length;
            opcodes = new String[(int) fields[len - 1].get(null) + 1];

            for (int delta = len - 1; ; delta--) {
                Field field = fields[delta];
                int index = (int) field.get(null);
                String name = field.getName();
                opcodes[index] = name;
                if (name.equals("NOP")) break;
            }
        } catch (final Throwable _t) {
            _t.printStackTrace(System.err);
        }
    }

    public static String id(int opcode) {
        return opcodes[opcode];
    }
}