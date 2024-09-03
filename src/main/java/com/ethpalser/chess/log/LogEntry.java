package com.ethpalser.chess.log;

import com.ethpalser.chess.game.view.ActionView;

public interface LogEntry<T extends Comparable<T>, U> {

    T getStart();

    T getEnd();

    U getStartObject();

    U getEndObject();

    /**
     * This contains entries that happen simultaneously to the LogEntry, but may not have been recorded in a log.
     * This can occur when case for generating an entry forces a situation that would generate an additional entry,
     * but it is not standard practice logging that forced situation.
     * <br/>
     * This is not meant for chaining LogEntries to create a primitive LinkedList.
     * @return LogEntry
     */
    LogEntry<T, U> getSubLogEntry();

    boolean isFirstOccurrence();

    ActionView toView();

}
