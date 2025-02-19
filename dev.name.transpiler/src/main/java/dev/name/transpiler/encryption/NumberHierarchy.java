package dev.name.transpiler.encryption;

import dev.name.util.math.Math;

public class NumberHierarchy {
    public static String process(final Number n) {
        return String.valueOf(n);
    }

    public void push_back( ) {

    }

    public static void main(String[] args) {
        System.out.println(process(123));
        System.out.println(process(123D));
        System.out.println(process(123L));
        System.out.println(process((short) 123));
        System.out.println(process((byte) 123));
        System.out.println(process(123F));
        System.out.println(Math.xor(1).left);
        System.out.println(Math.xor(1).left);
    }
}