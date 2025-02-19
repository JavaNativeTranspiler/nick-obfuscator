package nick.tests.exceptions;

import nick.Test;

import java.sql.SQLDataException;

public class JunkNestedExceptionTest implements Test {
    @Override
    public void run() {
        try {
            try {
                try {
                    try {
                        try {
                            try {
                                try {
                                    try {
                                        try {
                                            try {
                                                try {
                                                    try {
                                                        try {
                                                            try {
                                                                try {
                                                                    try {
                                                                        try {
                                                                            try {
                                                                                try {
                                                                                    try {
                                                                                        try {
                                                                                            try {
                                                                                                System.out.println("Junk Nested Exception Test Passed");
                                                                                            } catch (ArithmeticException | ArrayStoreException | ArrayIndexOutOfBoundsException | SecurityException exception) {} finally {}
                                                                                        }catch (Exception ex) {}finally {}
                                                                                    }catch (Exception ex) {}finally {}
                                                                                }catch (Exception ex) {}finally {}
                                                                            }catch (Exception ex) {}finally {}
                                                                        }catch (Exception ex) {}finally {}
                                                                    }catch (Exception ex) {}finally {}
                                                                }catch (Exception ex) {}finally {}
                                                            }catch (Exception ex) {}finally {}
                                                        }catch (Exception ex) {} finally {
                                                            System.out.println("Junk Exception Test Successful!");
                                                        }
                                                    }catch (Exception ex) {}finally {}
                                                }catch (Exception ex) {}finally {}
                                            }catch (Exception ex) {}finally {}
                                        }catch (Exception ex) {}finally {}
                                    }catch (Exception ex) {}finally {}
                                }catch (Exception ex) {}finally {}
                            }catch (Exception ex) {}finally {}
                        } catch (Exception ex) {}finally {}
                    }catch (Exception ex) {}finally {}
                }catch (Exception ex) {}finally {}
            }catch (Exception ex) {}finally {}
        }catch (Exception ex) {}finally {}

        try {
            try{
                try {
                    try{
                        try {
                            try{
                                try {
                                    try{
                                        try {
                                            try{
                                                try {
                                                    try{
                                                        try {
                                                            try{
                                                                try {
                                                                    try{
                                                                        try {
                                                                            try{
                                                                                try {
                                                                                    try{
                                                                                        try {
                                                                                            try{
                                                                                                try {
                                                                                                    try{
                                                                                                        try {
                                                                                                            try{
                                                                                                                try {
                                                                                                                    try{
                                                                                                                        try {
                                                                                                                            try{
                                                                                                                                try {
                                                                                                                                    try{
                                                                                                                                        try {
                                                                                                                                            try{
                                                                                                                                                try {
                                                                                                                                                    try{
                                                                                                                                                        try {
                                                                                                                                                            try{
System.out.println("Junk Nest Exception Test Round 2 Passed");
                                                                                                                                                            } catch (Exception ex) {}
                                                                                                                                                        }catch (Exception ex) {}
                                                                                                                                                    } catch (Exception ex) {}
                                                                                                                                                }catch (Exception ex) {}
                                                                                                                                            } catch (Exception ex) {}
                                                                                                                                        }catch (Exception ex) {}
                                                                                                                                    } catch (Exception ex) {}
                                                                                                                                }catch (Exception ex) {}
                                                                                                                            } catch (Exception ex) {}
                                                                                                                        }catch (Exception ex) {}
                                                                                                                    } catch (Exception ex) {}
                                                                                                                }catch (Exception ex) {}
                                                                                                            } catch (Exception ex) {}
                                                                                                        }catch (Exception ex) {}
                                                                                                    } catch (Exception ex) {}
                                                                                                }catch (Exception ex) {}
                                                                                            } catch (Exception ex) {}
                                                                                        }catch (Exception ex) {}
                                                                                    } catch (Exception ex) {}
                                                                                }catch (Exception ex) {}
                                                                            } catch (Exception ex) {}
                                                                        }catch (Exception ex) {}
                                                                    } catch (Exception ex) {}
                                                                }catch (Exception ex) {}
                                                            } catch (Exception ex) {}
                                                        }catch (Exception ex) {}
                                                    } catch (Exception ex) {}
                                                }catch (Exception ex) {}
                                            } catch (Exception ex) {}
                                        }catch (Exception ex) {}
                                    } catch (Exception ex) {}
                                }catch (Exception ex) {}
                            } catch (Exception ex) {}
                        }catch (Exception ex) {}
                    } catch (Exception ex) {}
                }catch (Exception ex) {}
            } catch (Exception ex) {}
        }catch (Exception ex) {}
    }
}