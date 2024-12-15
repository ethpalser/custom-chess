package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.custom.Location;
import com.ethpalser.chess.view.ReferenceView;
import java.util.List;

public class PieceReference implements Reference<Piece> {

    // Concern: Potential for Piece to not get garbage collected as it is referenced here despite not being on board
    private final Piece piece;
    private final Direction direction;
    private final int shiftX;
    private final int shiftY;

    public PieceReference(Piece piece) {
        this.piece = piece;
        this.direction = Direction.AT;
        this.shiftX = 0;
        this.shiftY = 0;
    }

    public PieceReference(Piece piece, Direction direction) {
        this.piece = piece;
        this.direction = direction;
        if (Direction.AT == direction) {
            this.shiftX = 0;
            this.shiftY = 0;
        } else {
            this.shiftX = 1;
            this.shiftY = 1;
        }
    }

    public PieceReference(Piece piece, Direction direction, int distance) {
        distance = Math.abs(distance);
        this.piece = piece;
        this.direction = direction;
        switch (direction) {
            case LEFT -> {
                shiftX = -distance;
                shiftY = 0;
            }
            case RIGHT -> {
                shiftX = distance;
                shiftY = 0;
            }
            case BACK -> {
                shiftX = 0;
                shiftY = -distance;
            }
            case FRONT -> {
                shiftX = 0;
                shiftY = distance;
            }
            default -> {
                shiftX = 0;
                shiftY = 0;
            }
        }
    }

    public PieceReference(Piece piece, Direction direction, int shiftX, int shiftY) {
        this.piece = piece;
        this.direction = direction;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    @Override
    public Location getLocation() {
        return Location.PIECE;
    }

    @Override
    public List<Piece> getReferences(Board<Coordinate> plane) {
        Piece ref = switch (this.direction) {
            case AT -> plane.get(this.piece.getCoordinate().translate(1, shiftX, shiftY));
            case LEFT, RIGHT -> plane.get(this.piece.getCoordinate().translate(1, shiftX));
            case BACK, FRONT -> plane.get(this.piece.getCoordinate().translate(1, 0, shiftY));
        };
        if (ref == null) {
            return List.of();
        }
        return List.of(ref);
    }

    @Override
    public ReferenceView toView() {
        return new ReferenceView(Location.PIECE, null, this.shiftX, this.shiftY);
    }
}
