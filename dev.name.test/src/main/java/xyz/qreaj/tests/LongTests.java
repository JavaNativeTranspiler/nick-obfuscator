package xyz.qreaj.tests;

public class LongTests {

    static float getFloat(float a) {
        return a * 0.5f;
    }
    static double getDouble(double a) {
        return a * 0.5;
    }
    static int getInt(int i) {
        return i + 1;
    }
    public static void test() {
        long l1 = 0L;
        long l2 = 1L;
        long l3 = 69L;
        long l4 = 5L;
        long testVal = 2L;


        if (l1 == l3) throw new IllegalStateException("LCMP FAIL");
        else System.out.println("LCMP PASS");

        float f = getFloat(2);
        if ((long)f == l2) System.out.println("F2L PASS");
        else throw new IllegalStateException("F2L FAIL");

        double d = getDouble(2);
        if ((long)d == l2) System.out.println("D2L PASS");
        else throw new IllegalStateException("D2L FAIL");

        int i = getInt(0);
        if ((long)i == l2) System.out.println("I2L PASS");
        else throw new IllegalStateException("I2L FAIL");

        l1 += dontOptimize(testVal,l1);
        testVal += l2 + l1 + l4 + l3;
        if (testVal == 77) System.out.println("LADD PASS");
        else throw new IllegalStateException("LADD FAIL");

        l1 += dontOptimize(testVal,l1);
        testVal -= l1 - l3;
        if (testVal == 146) System.out.println("LSUB PASS");
        else throw new IllegalStateException("LSUB FAIL");

        l3 += dontOptimize(testVal,l3);
        testVal = l2 * l3 *l4;
        if (testVal == 345) System.out.println("LMUL PASS");
        else throw new IllegalStateException("LMUL FAIL");

        l3 += dontOptimize(testVal,l3);
        testVal = l3 / l2;
        if (testVal == 69) System.out.println("LDIV PASS");
        else throw new IllegalStateException("LDIV FAIL");

        l3 += dontOptimize(testVal,l3);
        testVal = l3 % l2;
        if ( testVal == 0) System.out.println("LREM PASS");
        else throw new IllegalStateException("LREM FAIL");

        l3 += dontOptimize(testVal,l3);
        testVal = -l3;
        if (testVal == -69) System.out.println("LNEG PASS");
        else throw new IllegalStateException("LNEG FAIL");

        l3 += dontOptimize(testVal,l3);
        testVal = l3 << 2;
        if (testVal == 276) System.out.println("LSHL PASS");
        else throw new IllegalStateException("LSHL FAIL");

        l3 += dontOptimize(testVal,l3);
        testVal = l3 >> 2;
        if (testVal == 17) System.out.println("LSHR PASS");
        else throw new IllegalStateException("LSHR FAIL");

        l3 += dontOptimize(testVal,l3);
        testVal = l3 >>> 2;
        if (testVal == 17) System.out.println("LUSHR PASS");
        else throw new IllegalStateException("LUSHR FAIL");

        l3 += dontOptimize(testVal,l3);
        testVal = l3 & l4;
        if ( testVal == 5) System.out.println("LAND PASS");
        else throw new IllegalStateException("LAND FAIL");

        l3 += dontOptimize(testVal,l3);
        testVal = l3 | l4;
        if ( testVal == 69) System.out.println("LOR PASS");
        else throw new IllegalStateException("LOR FAIL");


        l3 += dontOptimize(testVal,l3);
        testVal = l3 ^ l4;
        if (testVal == 64) System.out.println("LXOR PASS");
        else throw new IllegalStateException("LXOR FAIL");


    }
    public static long dontOptimize(long l,long l2) {
        try {
            return (long) (1.2 % l) % l2 / 0;
        } catch (ArithmeticException e) {
            return 0;
        }
    }
}
