package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.Movement;
import com.ethpalser.chess.move.custom.CustomMove;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomPiece implements Piece {

    private final PieceType type;
    private final String code;
    private final Colour colour;
    private final List<CustomMove> moveSpecifications;
    private Point position;
    private boolean hasMoved;

    public CustomPiece(PieceType pieceType, Colour colour, Point vector) {
        this(pieceType, colour, vector, (CustomMove) null);
    }

    public CustomPiece(PieceType pieceType, Colour colour, Point vector, CustomMove... specifications) {
        this.type = pieceType;
        this.colour = colour;
        this.position = vector;
        this.moveSpecifications = new ArrayList<>(Arrays.asList(specifications));
        this.hasMoved = false;
        this.code = pieceType.getCode();
    }

    public CustomPiece(PieceType pieceType, Colour colour, Point vector, boolean hasMoved, CustomMove... customMoves) {
        this(pieceType, colour, vector, customMoves);
        this.hasMoved = hasMoved;
    }

    @Override
    public String getCode() {
        if (this.type != PieceType.CUSTOM) {
            return type.getCode();
        } else {
            return code;
        }
    }

    @Override
    public Colour getColour() {
        return this.colour;
    }

    @Override
    public Point getPoint() {
        return this.position;
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board) {
        return this.getMoves(board, null, null, false, false);
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log, ThreatMap threats,
            boolean onlyAttacks, boolean includeDefends) {
        Set<Movement> movements = new HashSet<>();
        if (onlyAttacks) {
            for (CustomMove spec : this.moveSpecifications) {
                if (spec.isAttack()) {
                    movements.addAll(spec.toMovementList(board, this.colour, this.position, includeDefends));
                }
            }
        } else {
            for (CustomMove spec : this.moveSpecifications) {
                movements.addAll(spec.toMovementList(board, this.colour, this.position, includeDefends));
            }
        }
        return new MoveSet(movements);
    }

    public void addMove(CustomMove move) {
        this.moveSpecifications.add(move);
    }

    /**
     * Updates this piece's position to the new {@link Point} destination. If this destination is not the same
     * as its current position then it is considered to have moved.
     *
     * @param destination representing the new location of this piece.
     */
    @Override
    public void move(Point destination) {
        if (destination == null) {
            throw new IllegalArgumentException("illegal argument, destination is null");
        }
        if (destination.equals(this.position)) {
            return;
        }
        this.position = destination;
        this.hasMoved = true;
    }

    /**
     * Retrieves the value of hasMoved.
     *
     * @return true or false
     */
    @Override
    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public String toString() {
        return this.type.getCode() + position.toString();
    }

}
