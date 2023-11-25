package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import com.chess.api.model.movement.Movement;
import com.chess.api.model.movement.MovementType;
import java.util.Arrays;
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

    @Getter(AccessLevel.NONE)
    private boolean hasMoved;

    public Piece() {
        this.type = PieceType.PAWN;
        this.colour = Colour.WHITE;
        this.position = new Coordinate(0, 0);
        this.hasMoved = false;
    }

    public Piece(PieceType pieceType, Colour colour, Coordinate coordinate) {
        this.type = pieceType;
        this.colour = colour;
        this.position = coordinate;
        this.hasMoved = false;
        this.movementList = List.of();
    }

    public Piece(PieceType pieceType, Colour colour, Coordinate coordinate, Movement... movements) {
        this(pieceType, colour, coordinate);
        this.movementList = List.of(movements);
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
        this.position = destination;
        this.hasMoved = true;
    }

    @Override
    public String toString() {
        return this.type.getCode() + position.toString();
    }

    // region Static methods - Specific Chess Pieces

    public static Piece pawn(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        Movement pawnBaseMove = new Movement(MovementType.ADVANCE, false, false, Coordinate.at(0, 1));
        // Todo: Add conditions to these moves
        Movement fastAdvance = new Movement(MovementType.ADVANCE, false, false, Coordinate.at(0, 1),
                Coordinate.at(0, 2));
        Movement capture = new Movement(MovementType.ADVANCE, false, true, Coordinate.at(1, 1));
        Movement enPassant = new Movement(MovementType.ADVANCE, false, true, Coordinate.at(1, 1));
        return new Piece(PieceType.PAWN, colour, coordinate, pawnBaseMove, fastAdvance, capture, enPassant);
    }

    public static Piece rook(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        Movement rookBaseMoveV = new Movement(MovementType.ADVANCE, true, false, Coordinate.at(0, 1),
                Coordinate.at(0, Coordinate.MAX_Y));
        Movement rookBaseMoveH = new Movement(MovementType.ADVANCE, false, true, Coordinate.at(1, 0),
                Coordinate.at(Coordinate.MAX_X, 0));
        return new Piece(PieceType.ROOK, colour, coordinate, rookBaseMoveV, rookBaseMoveH);
    }

    public static Piece knight(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        Movement knightBaseMove1 = new Movement(MovementType.JUMP, true, true, Coordinate.at(1, 2));
        Movement knightBaseMove2 = new Movement(MovementType.JUMP, true, true, Coordinate.at(2, 1));
        return new Piece(PieceType.KNIGHT, colour, coordinate, knightBaseMove1, knightBaseMove2);
    }

    public static Piece bishop(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        Movement bishopBaseMove = new Movement(MovementType.ADVANCE, true, true, Coordinate.at(1, 1),
                Coordinate.at(Coordinate.MAX_X, Coordinate.MAX_Y));
        return new Piece(PieceType.BISHOP, colour, coordinate, bishopBaseMove);
    }

    public static Piece queen(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        Movement queenBaseMoveV = new Movement(MovementType.ADVANCE, true, false, Coordinate.at(0, 1),
                Coordinate.at(0, Coordinate.MAX_Y));
        Movement queenBaseMoveH = new Movement(MovementType.ADVANCE, false, true, Coordinate.at(1, 0),
                Coordinate.at(Coordinate.MAX_X, 0));
        Movement queenBaseMoveD = new Movement(MovementType.ADVANCE, true, true, Coordinate.at(1, 1),
                Coordinate.at(Coordinate.MAX_X, Coordinate.MAX_Y));
        return new Piece(PieceType.QUEEN, colour, coordinate, queenBaseMoveV, queenBaseMoveH, queenBaseMoveD);
    }

    public static Piece king(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        Movement kingBaseMoveV = new Movement(MovementType.ADVANCE, true, false, new Coordinate(0, 1));
        Movement kingBaseMoveH = new Movement(MovementType.ADVANCE, false, true, new Coordinate(1, 0));
        Movement kingBaseMoveD = new Movement(MovementType.ADVANCE, true, true, new Coordinate(1, 1));
        return new Piece(PieceType.KING, colour, coordinate, kingBaseMoveV, kingBaseMoveH, kingBaseMoveD);
    }

    // endregion
}
