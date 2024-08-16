package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.custom.Location;

public class ChessReferenceFactory {

    private final Board board;
    private final Log<Point, Piece> log;

    public ChessReferenceFactory(Board board, Log<Point, Piece> log) {
        this.board = board;
        this.log = log;
    }

    public Reference<Piece> build(Location location, Colour colour, Direction direction, Point start, int shiftX,
            int shiftY) {
        switch (location) {
            case PIECE -> {
                return new PieceReference(board.getPiece(start), direction, shiftX, shiftY);
            }
            case POINT, PATH_START -> {
                return new AbsoluteReference<>(start, colour, direction);
            }
            case PATH_END -> {
                return new AbsoluteReference<>(this.pathEnd(start, shiftX, shiftY), colour, direction);
            }
            case LAST_MOVED -> {
                return new LogReference<>(this.log);
            }
            default -> {
                return new PathReference<>(location, start, this.pathEnd(start, shiftX, shiftY));
            }
        }
    }

    private Point pathEnd(Point start, int shiftX, int shiftY) {
        return new Point(start.getX() + shiftX, start.getY() + shiftY);
    }

}
