package com.ethpalser.chess.exception;

public class IllegalActionException extends IllegalArgumentException {

    public IllegalActionException() {
        super();
    }

    public IllegalActionException(String message) {
        super(message);
    }

    public IllegalActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalActionException(Throwable cause) {
        super(cause);
    }
}
