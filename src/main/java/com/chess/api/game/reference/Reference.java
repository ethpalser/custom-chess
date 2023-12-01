package com.chess.api.game.reference;

import com.chess.api.game.Board;
import com.chess.api.game.Colour;
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

    public Reference(Location location, Direction direction, Vector2D vector) {
        this.location = location;
        this.direction = direction;

        if (location == Location.VECTOR) {
            this.vector = vector;
        } else {
            this.vector = null;
        }
    }

    public List<Piece> getPieces(@NonNull Board board, @NonNull Action action) {
        Direction direction = this.direction;
        Vector2D shiftedStart = this.shiftInDirection(action.colour(), direction, action.start());
        Vector2D shiftedEnd = this.shiftInDirection(action.colour(), direction, action.end());
        Vector2D shiftedReference = this.shiftInDirection(action.colour(), direction, this.vector);

        List<Piece> list = new ArrayList<>();
        switch (this.location) {
            case LAST_MOVED -> list.add(board.getLastMoved());
            case START -> list.add(board.getPiece(shiftedStart));
            case DESTINATION -> list.add(board.getPiece(shiftedEnd));
            case VECTOR -> list.add(board.getPiece(shiftedReference));
            case PATH_TO_DESTINATION -> list = board.getPieces(new Path(shiftedStart, shiftedEnd));
            case PATH_TO_VECTOR -> list = board.getPieces(new Path(shiftedStart, shiftedReference));
        }
        ;
        return list;
    }

    private Vector2D shiftInDirection(@NonNull Colour colour, @NonNull Direction direction, Vector2D vector) {
        boolean isWhite = Colour.WHITE.equals(colour);
        int shiftX = 0;
        int shiftY = 0;
        switch (direction) {
            case FRONT -> shiftY = isWhite ? 1 : -1;
            case BACK -> shiftY = isWhite ? -1 : 1;
            case RIGHT -> shiftX = 1;
            case LEFT -> shiftX = -1;
            default -> {/* Do nothing */}
        }
        return Vector2D.fromVector2D(vector, shiftX, shiftY);
    }
}
