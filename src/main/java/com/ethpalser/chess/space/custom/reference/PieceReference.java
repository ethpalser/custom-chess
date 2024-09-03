package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
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
    public List<Piece> getReferences(Plane<Piece> plane) {
        Piece ref = switch (this.direction) {
            case AT -> plane.get(
                    new Point(this.piece.getPoint().getX() + shiftX, this.piece.getPoint().getY() + shiftY)
            );
            case LEFT, RIGHT -> plane.get(
                    new Point(this.piece.getPoint().getX() + shiftX, this.piece.getPoint().getY())
            );
            case BACK, FRONT -> plane.get(
                    new Point(this.piece.getPoint().getX(), this.piece.getPoint().getY() + shiftY)
            );
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
