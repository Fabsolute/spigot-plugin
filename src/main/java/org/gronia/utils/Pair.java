package org.gronia.utils;

public record Pair<T, K>(T p1, K p2) {
    public static <T, K> Pair<T, K> of(T p1, K p2) {
        return new Pair<>(p1, p2);
    }
}
