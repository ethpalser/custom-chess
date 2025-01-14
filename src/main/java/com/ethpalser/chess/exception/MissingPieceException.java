package com.ethpalser.chess.exception;

import com.ethpalser.chess.annotation.ClassPreamble;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2025-01-13",
        lastModified = "2025-01-13"
)
public class MissingPieceException extends NullPointerException {

    public static final String DEFAULT_MESSAGE = "Piece expected at source coordinate does not exist";

    public MissingPieceException() {
        super(DEFAULT_MESSAGE);
    }

    public MissingPieceException(String message) {
        super(message);
    }

}
