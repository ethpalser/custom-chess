package com.ethpalser.chess.util;

public class Tuple<T, U> {

    private final T first;
    private final U second;

    public Tuple(T first, U second) {
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
