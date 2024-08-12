package com.ethpalser.chess.log;

public interface LogEntry<T extends Comparable<T>, U> {

    T getStart();

    T getEnd();

    U getStartObject();

    U getEndObject();

    boolean isFirstOccurrence();

}
