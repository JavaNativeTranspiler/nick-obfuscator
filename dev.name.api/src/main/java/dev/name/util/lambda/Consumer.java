package dev.name.util.lambda;

/**
 * @param <T> input param #1
 */
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}