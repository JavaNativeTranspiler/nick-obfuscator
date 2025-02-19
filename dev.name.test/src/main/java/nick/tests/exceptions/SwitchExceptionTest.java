package nick.tests.exceptions;

import nick.Test;

import java.io.PrintStream;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AlreadyConnectedException;
import java.util.FormatterClosedException;
import java.util.IllegalFormatException;

@SuppressWarnings("all")
public class SwitchExceptionTest implements Test {
    @Override
    public void run() {
        int n = 1;
        while (n <= 10) {
            try {
                switch (n) {
                    case 1 -> {
                        try {
                            System.out.println("Switch layer 1 passed.");
                            n = 2;
                        } catch (Throwable t) {
                            System.out.println("Switch layer 1 failed.");
                            n = 11;
                        }
                    }
                    case 2 -> {
                        try {
                            throw new RuntimeException();
                        } catch (RuntimeException x) {
                            System.out.println("Switch layer 2 passed.");
                            n = 3;
                        }
                    }
                    case 3 -> {
                        try {
                            try {
                                throw new RuntimeException();
                            } catch (Throwable t) {
                                System.out.println("Switch layer 3 passed.");
                                n = 4;
                            }
                        } catch (ArrayStoreException x) {
                            System.out.println("Switch layer 3 failed.");
                            n = 11;
                        }
                    }
                    case 4 -> {
                        try {
                            try {
                                Object x[] = new String[3];
                                x[0] = Integer.valueOf(0);
                            } catch (IllegalStateException e) {
                                System.out.println("Switch exception test failed.");
                                n = 11;
                            }
                        } catch (ArrayStoreException x) {
                            System.out.println("Switch layer 4 passed.");
                            n = 5;
                        }
                    }
                    case 5 -> {
                        try {
                            try {
                                Object[] arr = new Object[1];
                                System.out.println((int) ((Object) arr));
                            } catch (ArithmeticException | AbstractMethodError | AlreadyBoundException |
                                     AlreadyConnectedException | IllegalFormatException e) {
                                System.out.println("Switch exception test failed.");
                                n = 11;
                            }
                        } catch (ClassCastException x) {
                            System.out.println("Switch layer 5 passed.");
                            n = 6;
                        }
                    }
                    case 6 -> {
                        try {
                            try {
                                Object[] arr = new Object[((n ^ 777) ^ n) - (n + 50000)];
                                System.out.println(arr[(n ^ 5) + (n + 50000)]);
                            } catch (ThreadDeath | NullPointerException | IllegalThreadStateException |
                                     IllegalStateException |
                                     IllegalFormatException | AbstractMethodError e) {
                                System.out.println("Switch layer 6 failed.");
                                n = 11;
                            }
                        } catch (NegativeArraySizeException x) {
                            System.out.println("Switch layer 6 passed.");
                            n = 7;
                        }
                    }
                    case 7 -> {
                        try {
                            try {
                                Object[] arr = new Object[50];
                                System.out.println(arr[(n ^ 5) + (n + 50000)]);
                            } catch (ThreadDeath | NullPointerException | FormatterClosedException ex) {
                                System.out.println("Switch layer 7 failed.");
                                n = 11;
                            }
                        } catch (ArrayIndexOutOfBoundsException x) {
                            System.out.println("Switch layer 7 passed.");
                            n = 8;
                        }
                    }
                    case 8 -> {
                        try {
                            try {
                                int num = Integer.parseInt("Hello World!");
                                System.out.println(num);
                            } catch (ArithmeticException ex) {
                                System.out.println("Switch layer 8 failed.");
                                n = 11;
                            }
                        } catch (NumberFormatException x) {
                            System.out.println("Switch layer 8 passed.");
                            n = 9;
                        }
                    }
                    case 9 -> {
                        try {
                            n /= (n ^ n);
                            System.out.println("Switch layer 9 failed.");
                            n = 11;
                        } catch (ArithmeticException x) {
                            System.out.println("Switch layer 9 passed.");
                            n = 10;
                        }
                    }
                    case 10 -> {
                        try {
                            PrintStream stream = null;
                            stream.println("Hello");
                            n = 11;
                            System.out.println("Switch layer 10 failed.");
                        } catch (NullPointerException x) {
                            System.out.println("Switch layer 10 passed.");
                            try {
                                n += 1;
                                n++;
                                --n;
                                n++;
                                n--;
                                n++;
                            } finally {
                                System.out.println("Switch exception test passed.");
                            }
                        }
                    }
                }
            } catch (final Throwable _t) {
                System.err.println("Switch exception test failed at #" + n);
                _t.printStackTrace(System.err);
            }
        }
    }
}