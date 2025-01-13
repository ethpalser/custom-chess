package com.ethpalser.chess.exception;

public class IllegalMoveException extends IllegalArgumentException {

    public IllegalMoveException() {
        super();
    }

    public IllegalMoveException(String message) {
        super(message);
    }
}
