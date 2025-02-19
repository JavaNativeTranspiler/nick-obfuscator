package dev.name.util.math;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class Radix {
    private static final SecureRandom RANDOM = new SecureRandom();

    public String str;
    public long val;
    public int radix, begin, end, coeff;
    public boolean negative;
    public long key, seed;
    public int stage;

    public Radix(final long n) {
        this.val = n;
        this.negative = n < 0;
        this.key = RANDOM.nextLong();
        this.seed = RANDOM.nextLong();
        this.radix = RANDOM.nextInt(4, Character.MAX_RADIX);
        this.coeff = RANDOM.nextInt(radix, Integer.MAX_VALUE);
        this.str = Long.toString(this.val, this.radix);
        if (this.negative) this.str = this.str.substring(1);
        this.stage = 1;
    }

    public void encrypt() {
        if (this.stage != 2) throw new IllegalStateException();
        final char[] data = this.str.toCharArray();
        for (int i = 0; i < data.length; i++) data[i] = (char) ((~seed) ^ data[i] ^ (key >> (i & (~coeff | coeff + (~begin | end)) % 4)));
        this.str = new String(data);
        this.stage = 3;
    }

    public void mix() {
        if (this.stage != 1) throw new IllegalStateException();
        final byte[] start = new byte[RANDOM.nextInt(10, 50)], end = new byte[RANDOM.nextInt(0,50)];
        fill(start);
        fill(end);
        final String s = new String(start, StandardCharsets.ISO_8859_1), e = new String(end, StandardCharsets.ISO_8859_1);
        this.begin = s.length();
        this.end = this.begin + this.str.length();
        this.str = String.format("%s%s%s", s, this.str, e);
        this.stage = 2;
    }

    public static long parseLong(final CharSequence seq, final int begin, final int end, int radix, final boolean negative, final long key, final int coeff, final long seed) {
        int i = begin;
        long r = 0;
        while (i < end) {
            r *= radix;
            r -= Character.digit((char) ((~seed) ^ (seq.charAt(i) ^ (key >> (i & (~coeff | coeff + (~begin | end)) % 4)))), radix);
            i++;
        }
        return negative ? r : ~r + 1;
    }

    private static void fill(final byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int r = RANDOM.nextInt(36);
            if (r < 10) arr[i] = (byte) ('0' + r);
            else arr[i] = (byte) ('a' + (r - 10));
        }
    }
}