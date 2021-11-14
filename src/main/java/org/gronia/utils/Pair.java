package org.gronia.utils;

public record Pair<A, B, C, D>(A p1, B p2, C p3, D p4) {
    public static <A, B, C, D> Pair<A, B, C, D> of(A p1, B p2, C p3, D p4) {
        return new Pair<>(p1, p2, p3, p4);
    }

    public static record Pair3<A, B, C>(A p1, B p2, C p3) {
        public static <A, B, C> Pair3<A, B, C> of(A p1, B p2, C p3) {
            return new Pair3<>(p1, p2, p3);
        }
    }
}
