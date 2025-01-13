package com.ethpalser.chess.exception;

public class IllegalResultException extends RuntimeException {

    public IllegalResultException() {
        super();
    }

    public IllegalResultException(String message) {
        super(message);
    }
}
