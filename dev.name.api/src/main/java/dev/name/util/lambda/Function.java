package dev.name.util.lambda;

/**
 * @param <T> input parameter
 * @param <R> output type
 */
public interface Function<T, R> {
    R evaluate(T t);
}