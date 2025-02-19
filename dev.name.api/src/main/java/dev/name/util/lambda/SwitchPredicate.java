package dev.name.util.lambda;

/**
 * @param <T> input & condition
 * @param <R> output
 */
@FunctionalInterface
public interface SwitchPredicate<T, R> {
    R evaluate(T value);

    static <T, R> SwitchPredicate<T, R> base() {
        return value -> null;
    }

    static <T, R> SwitchPredicate<T, R> of(Function<T, R> function) {
        return function::evaluate;
    }

    static <T, R> SwitchPredicate<T, R> base(Condition<T> condition, Function<T, R> function) {
        return value -> condition.test(value) ? function.evaluate(value) : null;
    }

    default SwitchPredicate<T, R> when(Condition<T> condition, Function<T, R> function) {
        return value -> condition.test(value) ? function.evaluate(value) : evaluate(value);
    }
}