package com.ethpalser.chess.piece;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.game.context.Board;
import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.space.Coordinate;
import java.util.List;
import java.util.Objects;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-11-06",
        majorVersion = 3,
        minorVersion = 2,
        lastModified = "2025-01-13"
)
public abstract class Piece {

    private final String code;
    private final Colour colour;
    private Coordinate position;
    private boolean hasMoved;

    protected Piece(String code, Colour colour, Coordinate point, boolean hasMoved) {
        this.code = code;
        this.colour = colour;
        this.position = point;
        this.hasMoved = hasMoved;
    }

    public String getCode() {
        return this.code;
    }

    public Colour getColour() {
        return this.colour;
    }

    public Coordinate getCoordinate() {
        return this.position;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.position = coordinate;
    }

    public boolean getHasMoved() {
        return this.hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public void move(Coordinate point) {
        if (point == null) {
            throw new IllegalArgumentException("piece cannot move to null");
        }
        if (point.equals(this.getCoordinate())) {
            return;
        }
        this.setCoordinate(point);
        this.hasMoved = true;
    }

    public abstract MoveSet getMoves(GameContext.Record context);

    public abstract boolean canPromote(Board<Coordinate> board);

    public abstract List<String> getPromotions();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return hasMoved == piece.hasMoved && Objects.equals(code, piece.code) && colour == piece.colour && Objects.equals(position, piece.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, colour, position, hasMoved);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getColour().toCode());
        sb.append(this.getCode());
        sb.append(this.getCoordinate());
        if (!this.getHasMoved()) {
            sb.append("*");
        }
        return sb.toString();
    }
}
