package dev.name.util.lambda;

/**
 * @param <T> input param #1
 * @param <U> input param #2
 */
@FunctionalInterface
public interface BiConsumer<T, U> {
    void accept(T t, U u);
}