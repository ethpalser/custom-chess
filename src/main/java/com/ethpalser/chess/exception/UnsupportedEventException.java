package com.ethpalser.chess.exception;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2025-01-13",
        lastModified = "2025-01-13"
)
public class UnsupportedEventException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "Event not supported by the current game state";

    public UnsupportedEventException() {
        super(DEFAULT_MESSAGE);
    }

    public UnsupportedEventException(String message) {
        super(message);
    }

}
