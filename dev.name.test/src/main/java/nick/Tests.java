package nick;

import nick.tests.exceptions.*;
import nick.tests.predicates.SwitchPredicateTest;

import java.util.List;

public class Tests {
    private static final List<Test> TESTS = List.of(
            // Exceptions
            new NestedExceptionTest(),
            new PropagatedExceptionTest(),
            new ControlFlowExceptionTest(),
            new SwitchExceptionTest(),
            new FinallyTest(),
            new JunkNestedExceptionTest(),

            // Predicates
            new SwitchPredicateTest()
    );

    public static void run() {
        try {
            System.out.println("Running custom tests...");
        } catch (Throwable _t) {
            System.out.println("Throwable caught!");
        } finally {
            System.out.println("Finally block test success...");
        }

        try {
            System.out.println("Custom test 2...");
        } finally {
            System.out.println("Success!");
        }

        try {
            TESTS.forEach((test) -> {
                        System.out.println("Running " + test.getClass());
                        test.run();
                    }
            );
        } catch (final Throwable _t) {
            System.err.println("Failed to run tests.");
            _t.printStackTrace(System.err);
        }
    }

    public static void main(String[] args) {
        run();
    }
}