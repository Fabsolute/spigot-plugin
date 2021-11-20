package org.gronia.utils;

import java.util.function.Consumer;

public record Pair<T>(Class<T> p1, Consumer<T> p2) {
    public static <T> Pair<T> of(Class<T> p1, Consumer<T> p2) {
        return new Pair<>(p1, p2);
    }
}

