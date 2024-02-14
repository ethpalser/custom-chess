package com.chess.api.game.reference;

import com.chess.api.game.Board;
import com.chess.api.game.Vector2D;
import com.chess.api.game.movement.Action;
import com.chess.api.game.movement.Path;
import com.chess.api.game.piece.Piece;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

public record Reference(Location location, Direction direction, Vector2D vector) {

    public Reference() {
        this(Location.START);
    }

    public Reference(Location location) {
        this(location, Direction.AT, null);
    }

    public Reference(Location location, Vector2D vector) {
        this(location, Direction.AT, vector);
    }

    public Reference(Location location, Direction direction) {
        this(location, direction, null);
    }

    public Reference(Location location, Direction direction, Vector2D vector) {
        this.location = location;
        this.direction = direction;

        if (location == Location.VECTOR) {
            this.vector = vector;
        } else {
            this.vector = null;
        }
    }

    /**
     * Get one or more {@link Piece}s that this reference is for using the current state of the board and action
     * being attempted.
     *
     * @param board  {@link Board} used for reference
     * @param action {@link Action} used for reference containing a single piece's position and destination
     * @return List of Pieces of the location is a Path, otherwise a List of one Piece
     */
    public List<Piece> getPieces(@NonNull Board board, @NonNull Action action) {
        if (action.start() == null || action.end() == null) {
            throw new IllegalArgumentException("Action has null start or end vector.");
        }
        Vector2D shiftedStart = action.start().shift(action.colour(), this.direction);
        Vector2D shiftedEnd = action.end().shift(action.colour(), this.direction);
        Vector2D shiftedReference = this.vector == null ? null : this.vector.shift(action.colour(), this.direction);

        List<Piece> list = new ArrayList<>();
        Piece toAdd = null;
        switch (this.location) {
            case LAST_MOVED -> toAdd = board.getLastMoved();
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
