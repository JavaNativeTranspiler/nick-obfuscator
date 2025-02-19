package dev.name.util.math;

import dev.name.util.collections.generic.Pair;

import java.security.SecureRandom;

public class Math {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static Pair<Long, Long> xor(final long n) {
        final long a = RANDOM.nextLong();
        return new Pair<>(a ^ n, n);
    }

    public static long[] xor(long n, final int segments) {
        long[] val = new long[segments];
        long segment = n / segments;
        long reversed = Long.reverse(segment);

        for (int i = 0; i < segments - 1; i++) {
            long shuffle = reversed ^ RANDOM.nextLong();
            long rnd = (shuffle + RANDOM.nextInt()) * RANDOM.nextLong();
            val[i] = (segment ^ rnd);
            n ^= val[i];
        }

        val[segments - 1] = n;
        return val;
    }

    public static long[] xor(long n, int segments, final long... params) {
        segments += params.length;
        long[] val = new long[segments];
        long segment = n / segments;
        long reversed = Long.reverse(segment);

        if (params.length > 0) {
            System.arraycopy(params, 0, val, 0, params.length);
            for (long l : params) n ^= l;
        }

        for (int i = params.length; i < segments - 1; i++) {
            long rnd = ((reversed ^ RANDOM.nextLong()) + RANDOM.nextInt()) * RANDOM.nextLong();
            val[i] = (segment ^ rnd);
            n ^= val[i];
        }

        val[segments - 1] = n;
        return val;
    }

    public static long[] or(final long n) {
        final long rm = (1L << (binaryDigits(n) / 2)) - 1;
        return new long[] { n & ~rm, n & rm};
    }

    public static long[] and(final long n) {
        return new long[]{n, n | (1L << 1)};
    }

    public static int digits(int n) {
        if (n == 0) return 1;
        n = (n < 0) ? -n : n;
        int c = 0;
        while (n > 0) {
            n /= 10;
            c++;
        }
        return c;
    }

    public static int digits(long n) {
        if (n == 0) return 1;
        n = (n < 0) ? -n : n;
        int c = 0;
        while (n > 0) {
            n /= 10;
            c++;
        }
        return c;
    }

    public static int binaryDigits(int n) {
        if (n == 0) return 1;
        n = (n < 0) ? -n : n;
        int c = 0;
        while (n > 0) {
            n >>= 1;
            c++;
        }
        return c;
    }

    public static int binaryDigits(long n) {
        if (n == 0) return 1;
        n = (n < 0) ? -n : n;
        int c = 0;
        while (n > 0) {
            n >>= 1;
            c++;
        }
        return c;
    }
}