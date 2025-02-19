package dev.name.util.lambda;

/**
 * @param <T> input param #1
 * @param <R> input param #2
 */
public interface BiCondition<T, R> {
    boolean test(T t, R r);
}