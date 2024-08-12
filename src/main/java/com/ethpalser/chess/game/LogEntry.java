package com.ethpalser.chess.game;

import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Point;

public interface LogEntry {

    Point getStart();

    Point getEnd();

    Piece getStartObject();

    Piece getEndObject();

    boolean isFirstOccurrence();

}
