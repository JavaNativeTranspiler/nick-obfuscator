package nick.tests.exceptions;

import nick.Test;

import java.nio.channels.AlreadyBoundException;

@SuppressWarnings("all")
public class PropagatedExceptionTest implements Test {
    @Override
    public void run() {
        try {
            try {
                try {
                    try {
                        mane(55555L, 1233, 1337.1337F);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        throw new NullPointerException();
                    }
                } catch (NullPointerException ok) {
                    throw new AlreadyBoundException();
                }
            } catch (AlreadyBoundException ok2) {
                throw new ArrayIndexOutOfBoundsException();
            }
        } catch (ArithmeticException xd) {
            System.out.println("Propagated Exception Test Passed");
            return;
        }
        System.out.println("Propagated Exception Test Failed");
    }

    private static void mane(final long j, final int n, float f) {
        f *= n ^ j / (j ^ j);
    }
}