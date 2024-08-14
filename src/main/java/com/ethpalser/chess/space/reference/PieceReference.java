package com.ethpalser.chess.space.reference;

import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.List;

public class PieceReference implements Reference<Piece> {

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
        return Location.VECTOR;
    }

    @Override
    public List<Piece> getReferences(Plane<Piece> plane) {
        return switch (this.direction) {
            case AT -> List.of(plane.get(
                    new Point(this.piece.getPoint().getX() + shiftX, this.piece.getPoint().getY() + shiftY)
            ));
            case LEFT -> List.of(plane.get(
                    new Point(this.piece.getPoint().getX() - shiftX, this.piece.getPoint().getY())
            ));
            case RIGHT -> List.of(plane.get(
                    new Point(this.piece.getPoint().getX() + shiftX, this.piece.getPoint().getY())
            ));
            case BACK -> List.of(plane.get(
                    new Point(this.piece.getPoint().getX(), this.piece.getPoint().getY() - shiftY)
            ));
            case FRONT -> List.of(plane.get(
                    new Point(this.piece.getPoint().getX(), this.piece.getPoint().getY() + shiftY)
            ));
        };
    }
}
