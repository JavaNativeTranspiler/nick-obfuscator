package nick.tests.exceptions;

import nick.Test;

@SuppressWarnings("all")
public class NestedExceptionTest implements Test {

    @Override
    public void run() {
        long result = 0;
        String sigma = "initial";

        try {
            try {
                try {
                    try {
                        try {
                            result = 123456L / 1234L;
                        } catch (ArithmeticException _t) {
                            result = 555L;
                            sigma = "ja";
                            throw new NullPointerException("KK");
                        } finally {
                            result = 111L;
                            sigma = "hello";
                            try {
                                throw new ArrayIndexOutOfBoundsException("ez");
                            } catch (ArrayIndexOutOfBoundsException _t) {
                                sigma = "xd";
                                result = 777L;
                            }
                        }
                    } catch (Throwable _t) {
                        result = 333L;
                        sigma = "rizz";
                        try {
                            result = result ^ sigma.charAt(0) ^ sigma.charAt(1) ^ sigma.charAt(~-sigma.charAt(0));
                        } catch (Exception __t) {
                            sigma = "wow";
                            result = 222L;
                        }
                    } finally {
                        result = 999L;
                        sigma = "nice";
                        try {
                            throw new IllegalStateException("cool");
                        } catch (IllegalStateException _t) {
                            result = 888L;
                            sigma = "bill";
                        } finally {
                            result = 9999L;
                            sigma = "nigerian prince";
                        }
                    }
                } catch (Exception e) {
                    result = 123L;
                    sigma = "aaa";
                    try {
                        result = sigma.charAt(2) ^ 555;
                    } catch (RuntimeException _t) {
                        result = 4321L;
                        sigma = "bbb";
                    } finally {
                        result = 876L;
                        sigma = "ccc";
                    }
                } finally {
                    result = 5555L;
                    sigma = "ezzz";
                }
            } catch (Exception e) {
                result = 11111L;
                sigma = "xddddddd";
                try {
                    result = "ok".charAt(-1);
                } catch (Exception ex) {
                    result = 77777L;
                    sigma = "fff";
                } finally {
                    result = 12345L;
                    sigma = "ggg";
                }
            }
        } catch (Throwable _t) {
            result = 999999L;
            sigma = "ttt";
        } finally {
            try {
                if (false) throw new ArrayIndexOutOfBoundsException();
            } catch (Throwable t) {
                throw new RuntimeException();
            } finally {
                try {
                    try {
                        try {
                            throw new ArrayIndexOutOfBoundsException();
                        } catch (ArrayIndexOutOfBoundsException _t) {
                            throw new NullPointerException();
                        }
                    } catch (ArithmeticException _t) {
                        result = -1L;
                        sigma = "failure nn";
                    }
                } catch (NullPointerException _t) {
                    result = 234 ^ System.nanoTime();
                    result ^= 999L;

                    for (int i = 0; i < 123; i++) {
                        result ^= System.nanoTime();
                    }

                    result ^= result;
                    sigma = "niger!";
                }
            }
        }

        if ("niger!".equals(sigma) && result == 0L) {
            System.out.println("Nested Exception Test passed");
        } else {
            System.out.println("Nested Exception Test failed");
        }
    }
}