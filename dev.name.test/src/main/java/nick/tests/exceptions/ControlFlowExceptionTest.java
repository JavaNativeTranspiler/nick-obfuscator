package nick.tests.exceptions;

import nick.Test;

@SuppressWarnings("all")
public class ControlFlowExceptionTest implements Test {
    private static final class NigerianException extends Exception {
        public NigerianException(String message) {
            super(message);
        }
    }

    @Override
    public void run() {
        try {
            Object obj = new Object();

            if (obj == null) {
                throw new NullPointerException("xd");
            }

            for (int i = 0; i < 10; i++) {
                try {
                    if (i % 2 == 0) {
                        switch (i) {
                            case 0:
                                try {
                                    throw new NigerianException("ez");
                                } catch (NigerianException e) {

                                }
                                break;
                            case 2:
                            case 4:
                                try {
                                    throw new ArithmeticException("nn");
                                } catch (ArithmeticException e) {
                                    if (e == null) {
                                        if (e.fillInStackTrace() == null) {
                                            throw new IllegalArgumentException();
                                        }
                                        throw new IllegalArgumentException();
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    try {
                        if (i % 3 == 0) {
                            throw new IllegalArgumentException("k" + i);
                        }
                    } catch (IllegalArgumentException e) {

                    }

                    try {
                        if (i == 5) {
                            throw new NigerianException("lol");
                        }
                    } catch (NigerianException e) {

                    }

                    if (i == 7) {
                        continue;
                    }

                    if (i == 9) {
                        break;
                    }

                    for (int j = 0; j < 5; j++) {
                        try {
                            if (j == 3) {
                                throw new NullPointerException("bad" + j);
                            }
                        } catch (NullPointerException e) {

                        }
                    }

                } catch (Exception e) {

                } finally {
                    try {
                        throw new RuntimeException();
                    } catch (RuntimeException ex) {
                        if (ex == null) {
                            throw new IndexOutOfBoundsException();
                        }
                    }
                }
            }

        } catch (Exception outerEx) {

        }

        String[] arr = new String[3];
        if (arr[0] == null) arr[0] = "";
        if (arr[0] != null) arr[0] = null;
        try {
            if (arr[0] != null) {
                throw new IllegalArgumentException();
            }
        } catch (Throwable _t) {}

        try {
            recurse(5);
        } catch (StackOverflowError e) {
            System.out.println("Control Flow Exception test passed");
            return;
        }

        System.out.println("Control Flow Exception test failed");
    }

    private static void recurse(int n) throws StackOverflowError {
        if (n <= 0) {
            return;
        }

        recurse(n - 1);

        if (n == 2) {
            throw new StackOverflowError("ok" + n);
        }
    }
}