package dev.name.util.math;

import java.security.SecureRandom;

public interface Random {
    SecureRandom RANDOM = new SecureRandom();

    default int nextInt() {
        return RANDOM.nextInt();
    }

    default int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    default int nextInt(int i1, int i2, int i3) {
        int n;
        do {
            n = nextInt(i1, i2);
        } while (n == i3);
        return n;
    }

    default int nextInt(int i1, int i2) {
        return i1 + RANDOM.nextInt(i2 - i1 + 1);
    }

    default long nextLong() {
        return RANDOM.nextLong();
    }

    default double nextDouble() {
        return RANDOM.nextDouble();
    }

    default boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    default void nextBytes(byte[] arr) {
        RANDOM.nextBytes(arr);
    }

    default float nextFloat() {
        return RANDOM.nextFloat();
    }

    default double nextGaussian() {
        return RANDOM.nextGaussian();
    }

    default long nextModulus(final long mod) {
        return (nextLong() / mod) * mod;
    }
}