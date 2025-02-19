package dev.name.util.asm;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class Templates {
    public static boolean if_icmpeq(int i1, int i2) {
        return i1 == i2;
    }

    public static boolean if_icmpne(int i1, int i2) {
        return i1 != i2;
    }

    public static boolean if_icmplt(int i1, int i2) {
        return i1 < i2;
    }

    public static boolean if_icmple(int i1, int i2) {
        return i1 <= i2;
    }

    public static boolean if_icmpgt(int i1, int i2) {
        return i1 > i2;
    }

    public static boolean if_icmpge(int i1, int i2) {
        return i1 >= i2;
    }

    public static boolean ifeq(int i1) {
        return i1 == 0;
    }

    public static boolean ifne(int i1) {
        return i1 != 0;
    }

    public static boolean iflt(int i1) {
        return i1 < 0;
    }

    public static boolean ifle(int i1) {
        return i1 <= 0;
    }

    public static boolean ifgt(int i1) {
        return i1 > 0;
    }

    public static boolean ifge(int i1) {
        return i1 >= 0;
    }

    public static int lcmp(long l1, long l2) {
        return Long.compare(l1, l2);
    }

    public static int dcmpl(double d1, double d2) {
        return (Double.isNaN(d1) || Double.isNaN(d2)) ? -1 : Double.compare(d1, d2);
    }

    public static int dcmpg(double d1, double d2) {
        return (Double.isNaN(d1) || Double.isNaN(d2)) ? 1 : Double.compare(d1, d2);
    }

    public static int fcmpl(float f1, float f2) {
        return (Float.isNaN(f1) || Float.isNaN(f2)) ? -1 : Float.compare(f1, f2);
    }

    public static int fcmpg(float f1, float f2) {
        return (Float.isNaN(f1) || Float.isNaN(f2)) ? 1 : Float.compare(f1, f2);
    }

    public static boolean ifnull(final Object obj) {
        return obj == null;
    }

    public static boolean ifnonnull(final Object obj) {
        return obj != null;
    }
}