package com.ethpalser.chess.exception;

public class CoordinateOutOfBoundsException extends IndexOutOfBoundsException {

    public static final String DEFAULT_MESSAGE = "One or more values of coordinate are out of bounds";

    public CoordinateOutOfBoundsException() {
        super(DEFAULT_MESSAGE);
    }

    public CoordinateOutOfBoundsException(String message) {
        super(message);
    }
}
