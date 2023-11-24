package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Piece {

    private final PieceType type;
    private final Colour colour;
    private Coordinate position;

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
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public boolean verifyMove(@NonNull Coordinate destination) {
        // Todo: Implement Movement
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

    // region Static methods

    public static Piece PAWN(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        return new Piece(PieceType.PAWN, colour, coordinate);
    }

    public static Piece ROOK(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        return new Piece(PieceType.ROOK, colour, coordinate);
    }

    public static Piece KNIGHT(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        return new Piece(PieceType.KNIGHT, colour, coordinate);
    }
    public static Piece BISHOP(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        return new Piece(PieceType.BISHOP, colour, coordinate);
    }

    public static Piece QUEEN(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        return new Piece(PieceType.QUEEN, colour, coordinate);
    }

    public static Piece KING(@NonNull Colour colour, @NonNull Coordinate coordinate) {
        return new Piece(PieceType.KING, colour, coordinate);
    }

    // endregion
}
