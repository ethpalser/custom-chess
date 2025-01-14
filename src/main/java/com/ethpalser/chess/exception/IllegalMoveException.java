package com.ethpalser.chess.exception;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-01-31",
        minorVersion = 2,
        lastModified = "2025-01-13"
)
public class IllegalMoveException extends RuntimeException {

    public IllegalMoveException() {
        super();
    }

    public IllegalMoveException(String message) {
        super(message);
    }
}
