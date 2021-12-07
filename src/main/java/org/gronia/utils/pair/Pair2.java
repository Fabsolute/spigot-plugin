package org.gronia.utils.pair;

public record Pair2<T, K>(T p1, K p2) {
    public static <T, K> Pair2<T, K> of(T p1, K p2) {
        return new Pair2<>(p1, p2);
    }
}
