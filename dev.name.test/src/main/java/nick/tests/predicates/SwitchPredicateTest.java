package nick.tests.predicates;

import nick.Test;

import java.util.function.Function;
import java.util.function.Predicate;

public class SwitchPredicateTest implements Test {
    private interface SwitchPredicate<T, R> {
        R evaluate(T value);

        static <T, R> SwitchPredicate<T, R> base() {
            return value -> null;
        }

        default SwitchPredicate<T, R> when(Predicate<T> predicate, Function<T, R> function) {
            return value -> predicate.test(value) ? function.apply(value) : evaluate(value);
        }
    }

    @Override
    public void run() {
        final SwitchPredicate<Integer, String> predicate = SwitchPredicate.<Integer, String>base()
                .when(i -> i < 0, i -> "-1")
                .when(i -> i == 0, i -> "0")
                .when(i -> i == 1, i -> "1")
                .when(i -> i == 2, i -> "2")
                .when(i -> i == 3, i -> "3")
                .when(i -> i == 4, i -> "4")
                .when(i -> i == 5, i -> "5")
                .when(i -> i == 6, i -> "6")
                .when(i -> i == 7, i -> "7")
                .when(i -> i == 8, i -> "8")
                .when(i -> i == 9, i -> "9")
                .when(i -> i == 10, i -> "10");

        if (predicate == null) {
            System.out.printf("Switch Predicate Test Failed%n");
            return;
        }

        for (int i = -1; i <= 10; i++) {
            System.out.printf("Switch Predicate Test Stage %d %s%n", i + 2, switch (predicate.evaluate(i)) {
                case "-1" -> i < 0;
                case "0" -> i == 0;
                case "1" -> i == 1;
                case "2" -> i == 2;
                case "3" -> i == 3;
                case "4" -> i == 4;
                case "5" -> i == 5;
                case "6" -> i == 6;
                case "7" -> i == 7;
                case "8" -> i == 8;
                case "9" -> i == 9;
                case "10" -> i == 10;
                default -> throw new IllegalArgumentException("Switch Predicate Test Failed.");
            } ? "Passed" : "Failed");
        }
    }
}