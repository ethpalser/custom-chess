package com.chess.api.model.piece;

import com.chess.api.model.Board;
import com.chess.api.model.Colour;
import com.chess.api.model.Vector2D;
import com.chess.api.model.movement.Movement;
import com.chess.api.model.movement.Path;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Piece {

    private final PieceType type;
    private final Colour colour;
    private final List<Movement> movements;
    private Vector2D position;
    private int lastMoveDistance;

    @Getter(AccessLevel.NONE)
    private boolean hasMoved;

    public Piece() {
        this(PieceType.PAWN, Colour.WHITE, new Vector2D());
    }

    public Piece(PieceType pieceType, Colour colour, Vector2D vector) {
        this(pieceType, colour, vector, (Movement) null);
    }

    public Piece(PieceType pieceType, Colour colour, Vector2D vector, Movement... movements) {
        this.type = pieceType;
        this.colour = colour;
        this.position = vector;
        this.movements = List.of(movements);
        this.hasMoved = false;
        this.lastMoveDistance = 0;
    }

    public void setPosition(@NonNull Vector2D destination) {
        this.lastMoveDistance = Math.max(Math.abs(destination.getX() - position.getX()),
                Math.abs(destination.getY() - position.getY()));
        this.position = destination;
        this.hasMoved = true;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public Movement getMovement(@NonNull Board board, @NonNull Vector2D destination) {
        for (Movement move : this.movements) {
            Path path = move.getPath(this.colour, this.position, destination);
            if (path != null && path.isTraversable(board) && move.passesConditions(board, this.position, destination)) {
                return move;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.type.getCode() + position.toString();
    }

}
