package com.ethpalser.chess.exception;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2025-01-13",
        lastModified = "2025-01-13"
)
public class IllegalResultException extends RuntimeException {

    public IllegalResultException() {
        super();
    }

    public IllegalResultException(String message) {
        super(message);
    }
}
