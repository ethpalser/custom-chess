package com.ethpalser.chess.exception;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2025-01-13",
        lastModified = "2025-01-13"
)
public class CoordinateOutOfBoundsException extends IndexOutOfBoundsException {

    public static final String DEFAULT_MESSAGE = "One or more values of coordinate are out of bounds";

    public CoordinateOutOfBoundsException() {
        super(DEFAULT_MESSAGE);
    }

    public CoordinateOutOfBoundsException(String message) {
        super(message);
    }
}
