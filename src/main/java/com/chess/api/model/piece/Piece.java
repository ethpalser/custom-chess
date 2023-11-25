package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import com.chess.api.model.movement.Movement;
import com.chess.api.model.movement.MovementType;
import java.util.ArrayList;
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
            boolean valid = move.validCoordinate(this.colour, this.position, destination);
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

    public static Piece PAWN(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        Movement pawnBaseMove = new Movement(MovementType.ADVANCE, false, false, new Coordinate(0, 1));
        // Todo: Add conditions to these moves
        Movement fastAdvance = new Movement(MovementType.ADVANCE, false, false, new Coordinate(0, 2));
        Movement capture = new Movement(MovementType.ADVANCE, false, true, new Coordinate(1, 1));
        Movement enPassant = new Movement(MovementType.ADVANCE, false, true, new Coordinate(1, 1));
        return new Piece(PieceType.PAWN, colour, coordinate, pawnBaseMove, fastAdvance, capture, enPassant);
    }

    public static Piece ROOK(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        List<Coordinate> baseCoordinates = new ArrayList<>();
        Coordinate origin = Coordinate.origin();
        baseCoordinates.addAll(Coordinate.vertical(origin));
        baseCoordinates.addAll(Coordinate.horizontal(origin));

        Movement rookBaseMove = new Movement(MovementType.ADVANCE, true, true, baseCoordinates);
        return new Piece(PieceType.ROOK, colour, coordinate, rookBaseMove);
    }

    public static Piece KNIGHT(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        Movement knightBaseMove = new Movement(MovementType.JUMP, true, true, new Coordinate(1, 2),
                new Coordinate(2, 1));
        return new Piece(PieceType.KNIGHT, colour, coordinate, knightBaseMove);
    }

    public static Piece BISHOP(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        Coordinate origin = Coordinate.origin();
        List<Coordinate> baseCoordinates = new ArrayList<>(Coordinate.diagonal(origin));

        Movement bishopBaseMove = new Movement(MovementType.ADVANCE, true, true, baseCoordinates);
        return new Piece(PieceType.BISHOP, colour, coordinate, bishopBaseMove);
    }

    public static Piece QUEEN(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        List<Coordinate> baseCoordinates = new ArrayList<>();
        Coordinate origin = Coordinate.origin();
        baseCoordinates.addAll(Coordinate.vertical(origin));
        baseCoordinates.addAll(Coordinate.horizontal(origin));
        baseCoordinates.addAll(Coordinate.diagonal(origin));

        Movement queenBaseMove = new Movement(MovementType.ADVANCE, true, true, baseCoordinates);
        return new Piece(PieceType.QUEEN, colour, coordinate, queenBaseMove);
    }

    public static Piece KING(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        Movement kingBaseMove = new Movement(MovementType.ADVANCE, true, true, new Coordinate(0, 1),
                new Coordinate(1, 1), new Coordinate(1, 0));
        return new Piece(PieceType.KING, colour, coordinate, kingBaseMove);
    }

    // endregion
}
