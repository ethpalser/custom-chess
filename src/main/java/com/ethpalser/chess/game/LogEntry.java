package com.ethpalser.chess.game;

public interface LogEntry<T extends Comparable<T>, U> {

    T getStart();

    T getEnd();

    U getStartObject();

    U getEndObject();

    boolean isFirstOccurrence();

}
