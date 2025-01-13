package com.ethpalser.chess.exception;

public class MissingPieceException extends NullPointerException {

    public static final String DEFAULT_MESSAGE = "Piece expected at source coordinate does not exist";

    public MissingPieceException() {
        super(DEFAULT_MESSAGE);
    }

    public MissingPieceException(String message) {
        super(message);
    }

}
