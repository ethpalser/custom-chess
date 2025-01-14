package com.ethpalser.chess.util;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-08-30",
        majorVersion = 2,
        lastModified = "2025-01-13"
)
public class Pair<T, U> {

    private final T first;
    private final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return this.first;
    }

    public U getSecond() {
        return this.second;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
