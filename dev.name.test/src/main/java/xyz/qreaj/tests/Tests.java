package xyz.qreaj.tests;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class Tests {

    static void dontOptimize(Object[] o) {
        if (System.currentTimeMillis() < 0) {
            o[0] = 2137;
        }
    }

    public static void testConditions(int value,int value2, Object obj1, Object obj2) {
        dontOptimize(new Object[]{value,value2});
        if (value <= 0) System.out.println("IFGT PASS");
        else throw new IllegalStateException("IFGT FAIL");

        if (value2 >= 0) System.out.println("IFLT PASS");
        else throw new IllegalStateException("IFLT FAIL");

        if (obj1 == obj2) throw new IllegalStateException("IF_ACMPEQ FAIL");
        else System.out.println("IF_ACMPEQ PASS");

        if (obj1 != obj2) System.out.println("IF_ACMPNE PASS");
        else throw new IllegalStateException("IF_ACMPNE FAIL");
    }

    public static void runTests() {
         LongTests.test();
         IntegerTests.test();
         FloatTests.test();
         DoubleTests.test();

         float[] arr = {1,2,3,4,5};
         dontOptimize(new Object[]{arr});
         if (arr[0] == 1) System.out.println("FALOAD,FASTORE PASS");
         else throw new IllegalStateException("FALOAD,FASTORE FAIL");

        double[] arr2 = {1,2,3,4,5};
        dontOptimize(new Object[]{arr2});
        if (arr2[0] == 1) System.out.println("DALOAD,DASTORE PASS");
        else throw new IllegalStateException("DALOAD,DASTORE FAIL");

        short[] arr3 = {1,2,3,4,5};
        dontOptimize(new Object[]{arr3});
        if (arr3[0] == 1) System.out.println("SALOAD,SASTORE PASS");
        else throw new IllegalStateException("SALOAD,SASTORE FAIL");

        short a = (short) (int) arr2[1];
        if (a == arr[1]) System.out.println("I2S PASS");
        else throw new IllegalStateException("I2S,SASTORE FAIL");

       testConditions(-5,5, new Object(),new Object());

       // DUP_X1 check
        CustomMap<String, Integer> customMap = new CustomMap<>();
        customMap.put("Test", 1);
        customMap.put("Test2", 2);

        Set<Map.Entry<String, Integer>> entrySet1 = customMap.entrySet();

        customMap.put("Test3", 3);

        Set<Map.Entry<String, Integer>> entrySet2 = customMap.entrySet();

        boolean isSameEntrySet = entrySet1 == entrySet2;

        boolean isEntrySetUpdated = entrySet2.size() == 3 && entrySet2.stream()
                .anyMatch(entry -> entry.getKey().equals("Test3") && entry.getValue().equals(3));

        if (isSameEntrySet || isEntrySetUpdated) System.out.println("DUP_X1 PASS");
        else throw new IllegalStateException("DUP_X1 FAIL");

        switch ((int)arr[0]) {
            case 1:
                System.out.println("TABLESWITCH PASS");
                break;
            case 2:
            case 3:
            default:
                throw new IllegalStateException("TABLESWITCH FAIL");
        }
        if (new Float(arr[0]) instanceof Number) System.out.println("INSTANCEOF PASS");
        else throw new IllegalStateException("INSTANCEOF FAIL");

        int[][] multiDimensionalArray = new int[2][2];
        multiDimensionalArray[0][1] = 2;
        multiDimensionalArray[1][0] = 3;

        dontOptimize(multiDimensionalArray);
        if (multiDimensionalArray[0][1] == 2 && multiDimensionalArray[1][0] == 3)
            System.out.println("MULTIANEWARRAY PASS");
        else throw new IllegalStateException("MULTIANEWARRAY FAIL");
    }
}
