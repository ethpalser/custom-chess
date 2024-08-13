package com.ethpalser.chess.piece.custom.reference;

import com.ethpalser.chess.board.CustomBoard;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Point;
import java.util.ArrayList;
import java.util.List;

public class Reference {

    private final Location location;
    private final Direction direction;
    private final Point vector;

    public Reference() {
        this(Location.START);
    }

    public Reference(Location location) {
        this(location, Direction.AT, null);
    }

    public Reference(Location location, Point vector) {
        this(location, Direction.AT, vector);
    }

    public Reference(Location location, Direction direction) {
        this(location, direction, null);
    }

    public Reference(Location location, Direction direction, Point vector) {
        this.location = location;
        this.direction = direction;

        if (location == Location.VECTOR) {
            this.vector = vector;
        } else {
            this.vector = null;
        }
    }

    /**
     * Get one or more {@link CustomPiece}s that this reference is for using the current state of the board and action
     * being attempted.
     *
     * @param board  {@link CustomBoard} used for reference
     * @param action {@link Action} used for reference containing a single piece's position and destination
     * @return List of Pieces of the location is a Path, otherwise a List of one Piece
     */
    public List<Piece> getPieces(CustomBoard board, Action action) {
        if (board == null || action == null) {
            throw new NullPointerException();
        }
        if (action.getStart() == null || action.getEnd() == null) {
            throw new IllegalArgumentException("Action has null start or end vector.");
        }
        Point shiftedStart = action.getStart().shift(action.getColour(), this.direction);
        Point shiftedEnd = action.getEnd().shift(action.getColour(), this.direction);
        Point shiftedReference = this.vector == null ? null : this.vector.shift(action.getColour(), this.direction);

        List<Piece> list = new ArrayList<>();
        Piece toAdd = null;
        switch (this.location) {
            case LAST_MOVED -> toAdd = null; // Todo: Use ChessLog
            case START -> toAdd = board.getPiece(shiftedStart);
            case DESTINATION -> toAdd = board.getPiece(shiftedEnd);
            case VECTOR -> toAdd = board.getPiece(shiftedReference);
            case PATH_TO_DESTINATION -> list = board.getPieces(new Path(shiftedStart, shiftedEnd));
            case PATH_TO_VECTOR -> list = board.getPieces(new Path(shiftedStart, shiftedReference));
        }
        if (toAdd != null)
            list.add(toAdd);
        return list;
    }

}
