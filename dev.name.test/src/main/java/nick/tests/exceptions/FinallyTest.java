package nick.tests.exceptions;

import nick.Test;

public class FinallyTest implements Test {
    @Override
    public void run() {
        try {
            throw new RuntimeException();
        } catch (RuntimeException ex) {

        } finally {
            System.out.println("Finally Test Success");
        }
    }
}