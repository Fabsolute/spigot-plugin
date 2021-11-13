package org.gronia.utils;

public record Pair<A, B, C, D>(A p1, B p2, C p3, D p4) {
    public static <A, B, C, D> Pair<A, B, C, D> of(A p1, B p2, C p3, D p4) {
        return new Pair<>(p1, p2, p3, p4);
    }

    public static record Pair2<A, B>(A p1, B p2) {
        public static <A, B> Pair2<A, B> of(A p1, B p2) {
            return new Pair2<>(p1, p2);
        }
    }
}
