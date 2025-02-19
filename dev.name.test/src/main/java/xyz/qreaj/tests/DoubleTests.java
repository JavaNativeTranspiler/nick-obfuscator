package xyz.qreaj.tests;

public class DoubleTests {


    static float getFloat(float a) {
        return a * 0.5f;
    }
    static long getLong(long i) {
        return i + 1;
    }
    static int getInt(int i) {
        return i + 1;
    }


    static void dontOptimize(Object[] o) {
        if (System.currentTimeMillis() < 0) {
            o[0] = 2137;
        }
    }

    public static void test() {
        double d1 = 0.0;
        double d2 = 1.0;
        double d3 = 69.0;
        double d4 = 5.0;
        double testVal = 2.0;

        Object[] o = {d1, d2, d3, d4, testVal};
        dontOptimize(o);

        if (d1 == d3) throw new IllegalStateException("DCMP FAIL");
        else System.out.println("DCMP PASS");

        int i = getInt(0);
        if ((double) i == d2) System.out.println("I2D PASS");
        else throw new IllegalStateException("I2D FAIL");

        float f = getFloat(2);
        if ((double) f == d2) System.out.println("F2D PASS");
        else throw new IllegalStateException("F2D FAIL");

        long l = getLong(0);
        if ((double) l  == d2) System.out.println("L2D PASS");
        else throw new IllegalStateException("L2D FAIL");

        testVal += d2 + d1 + d4 + d3;
        if (testVal == 77.0) System.out.println("DADD PASS");
        else throw new IllegalStateException("DADD FAIL");

        testVal -= d1 - d3;
        if (testVal == 146.0) System.out.println("DSUB PASS");
        else throw new IllegalStateException("DSUB FAIL");

        testVal = d2 * d3 * d4;
        if (testVal == 345.0) System.out.println("DMUL PASS");
        else throw new IllegalStateException("DMUL FAIL");

        testVal = d3 / d2;
        if (testVal == 69.0) System.out.println("DDIV PASS");
        else throw new IllegalStateException("DDIV FAIL");

        testVal = d3 % d2;
        if (testVal == 0.0) System.out.println("DREM PASS");
        else throw new IllegalStateException("DREM FAIL");

        testVal = -d3;
        if (testVal == -69.0) System.out.println("DNEG PASS");
        else throw new IllegalStateException("DNEG FAIL");


        testVal = 0.0 / 0.0;
        if (Double.isNaN(testVal)) System.out.println("DNAN PASS");
        else throw new IllegalStateException("DNAN FAIL");

        testVal = d3 / d1;
        if (Double.isInfinite(testVal) && testVal > 0) System.out.println("DInfinity PASS");
        else throw new IllegalStateException("DInfinity FAIL");

        testVal = -d3 / d1;
        if (Double.isInfinite(testVal) && testVal < 0) System.out.println("DNegativeInfinity PASS");
        else throw new IllegalStateException("DNegativeInfinity FAIL");
    }
}
