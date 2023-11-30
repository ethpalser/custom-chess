package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import com.chess.api.model.movement.Movement;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Piece {

    private final PieceType type;
    private final Colour colour;
    private Coordinate position;
    private List<Movement> movementList;
    private int lastMoveDistance;

    @Getter(AccessLevel.NONE)
    private boolean hasMoved;

    public Piece() {
        this.type = PieceType.PAWN;
        this.colour = Colour.WHITE;
        this.position = new Coordinate(0, 0);
        this.hasMoved = false;
        this.lastMoveDistance = 0;
    }

    public Piece(PieceType pieceType, Colour colour, Coordinate coordinate) {
        this.type = pieceType;
        this.colour = colour;
        this.position = coordinate;
        this.hasMoved = false;
        this.movementList = List.of();
        this.lastMoveDistance = 0;
    }

    public Piece(PieceType pieceType, Colour colour, Coordinate coordinate, Movement... movements) {
        this(pieceType, colour, coordinate);
        this.movementList = List.of(movements);
        this.lastMoveDistance = 0;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public boolean verifyMove(@NonNull Coordinate destination) {
        for (Movement move : movementList) {
            boolean valid = move.isValidCoordinate(this.colour, this.position, destination);
            if (valid) {
                return true;
            }
        }
        return false;
    }

    public void performMove(@NonNull Coordinate destination) {
        this.lastMoveDistance = Math.max(Math.abs(destination.getX() - position.getX()),
                Math.abs(destination.getY() - position.getY()));
        this.position = destination;
        this.hasMoved = true;
    }

    @Override
    public String toString() {
        return this.type.getCode() + position.toString();
    }

}
