package xyz.qreaj.tests;

public class FloatTests {

    static double getDouble(double a) {
        return a * 0.5;
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
        float f1 = 0.0f;
        float f2 = 1.0f;
        float f3 = 69.0f;
        float f4 = 5.0f;
        float testVal = 2.0f;

        Object[] o = {f1,f2,f3,f4,testVal};
        dontOptimize(o);

        if (f1 == f3) throw new IllegalStateException("FCMP FAIL");
        else System.out.println("FCMP PASS");

        int i = getInt(0);
        if ((float) i == f2) System.out.println("I2F PASS");
        else throw new IllegalStateException("I2F FAIL");

        double d = getDouble(2);
        if ((float) d == f2) System.out.println("D2F PASS");
        else throw new IllegalStateException("D2F FAIL");

        long l = getLong(0);
        if ((float) l  == f2) System.out.println("L2F PASS");
        else throw new IllegalStateException("L2F FAIL");


        testVal += f2 + f1 + f4 + f3;
        if (testVal == 77.0f) System.out.println("FADD PASS");
        else throw new IllegalStateException("FADD FAIL");

        testVal -= f1 - f3;
        if (testVal == 146.0f) System.out.println("FSUB PASS");
        else throw new IllegalStateException("FSUB FAIL");

        testVal = f2 * f3 * f4;
        if (testVal == 345.0f) System.out.println("FMUL PASS");
        else throw new IllegalStateException("FMUL FAIL");

        testVal = f3 / f2;
        if (testVal == 69.0f) System.out.println("FDIV PASS");
        else throw new IllegalStateException("FDIV FAIL");

        testVal = f3 % f2;
        if (testVal == 0.0f) System.out.println("FREM PASS");
        else throw new IllegalStateException("FREM FAIL");

        testVal = -f3;
        if (testVal == -69.0f) System.out.println("FNEG PASS");
        else throw new IllegalStateException("FNEG FAIL");


        testVal = 0.0f / 0.0f;
        if (Float.isNaN(testVal)) System.out.println("FNAN PASS");
        else throw new IllegalStateException("FNAN FAIL");


        testVal = f3 / f1;
        if (Float.isInfinite(testVal) && testVal > 0) System.out.println("FInfinity PASS");
        else throw new IllegalStateException("FInfinity FAIL");

        testVal = -f3 / f1;
        if (Float.isInfinite(testVal) && testVal < 0) System.out.println("FNegativeInfinity PASS");
        else throw new IllegalStateException("FNegativeInfinity FAIL");
    }
}
