package com.ethpalser.chess.exception;

public class UnsupportedEventException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "Event not supported by the current game state";

    public UnsupportedEventException() {
        super(DEFAULT_MESSAGE);
    }

    public UnsupportedEventException(String message) {
        super(message);
    }

}
