package xyz.qreaj.tests;

public class IntegerTests {
    static float getFloat(float a) {
        return a * 0.5f;
    }
    static double getDouble(double a) {
        return a * 0.5;
    }
    static long getLong(long i) {
        return i + 1;
    }

    public static void test() {
        int i1 = 0;
        int i2 = 1;
        int i3 = 69;
        int i4 = 5;
        int testVal = 2;

        if (i1 == i3) throw new IllegalStateException("ICMP FAIL");
        else System.out.println("ICMP PASS");

        float f = getFloat(2);
        if ((int)f == i2) System.out.println("F2I PASS");
        else throw new IllegalStateException("F2I FAIL");

        double d = getDouble(2);
        if ((long)d == i2) System.out.println("D2I PASS");
        else throw new IllegalStateException("D2I FAIL");

        long l = getLong(0);
        if ((int)l  == i2) System.out.println("L2I PASS");
        else throw new IllegalStateException("L2I FAIL");

        i1 += dontOptimize(testVal, i1);
        testVal += i2 + i1 + i4 + i3;
        if (testVal == 77) System.out.println("IADD PASS");
        else throw new IllegalStateException("IADD FAIL");

        i1 += dontOptimize(testVal, i1);
        testVal -= i1 - i3;
        if (testVal == 146) System.out.println("ISUB PASS");
        else throw new IllegalStateException("ISUB FAIL");

        i3 += dontOptimize(testVal, i3);
        testVal = i2 * i3 * i4;
        if (testVal == 345) System.out.println("IMUL PASS");
        else throw new IllegalStateException("IMUL FAIL");

        i3 += dontOptimize(testVal, i3);
        testVal = i3 / i2;
        if (testVal == 69) System.out.println("IDIV PASS");
        else throw new IllegalStateException("IDIV FAIL");

        i3 += dontOptimize(testVal, i3);
        testVal = i3 % i2;
        if (testVal == 0) System.out.println("IREM PASS");
        else throw new IllegalStateException("IREM FAIL");

        i3 += dontOptimize(testVal, i3);
        testVal = -i3;
        if (testVal == -69) System.out.println("INEG PASS");
        else throw new IllegalStateException("INEG FAIL");

        i3 += dontOptimize(testVal, i3);
        testVal = i3 << 2;
        if (testVal == 276) System.out.println("ISHL PASS");
        else throw new IllegalStateException("ISHL FAIL");

        i3 += dontOptimize(testVal, i3);
        testVal = i3 >> 2;
        if (testVal == 17) System.out.println("ISHR PASS");
        else throw new IllegalStateException("ISHR FAIL");

        i3 += dontOptimize(testVal, i3);
        testVal = i3 >>> 2;
        if (testVal == 17) System.out.println("IUSHR PASS");
        else throw new IllegalStateException("IUSHR FAIL");

        i3 += dontOptimize(testVal, i3);
        testVal = i3 & i4;
        if (testVal == 5) System.out.println("IAND PASS");
        else throw new IllegalStateException("IAND FAIL");

        i3 += dontOptimize(testVal, i3);
        testVal = i3 | i4;
        if (testVal == 69) System.out.println("IOR PASS");
        else throw new IllegalStateException("IOR FAIL");

        i3 += dontOptimize(testVal, i3);
        testVal = i3 ^ i4;
        if (testVal == 64) System.out.println("IXOR PASS");
        else throw new IllegalStateException("IXOR FAIL");
    }

    public static int dontOptimize(int i, int i2) {
        try {
            return (int) (1.2 % i) % i2 / 0;
        } catch (ArithmeticException e) {
            return 0;
        }
    }
}
