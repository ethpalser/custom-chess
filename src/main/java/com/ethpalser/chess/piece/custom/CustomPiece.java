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

    public CustomPiece(String code, Colour colour, Point vector, boolean hasMoved, CustomMove... customMoves) {
        this.type = PieceType.fromCode(code);
        this.code = code;
        this.colour = colour;
        this.position = vector;
        this.hasMoved = hasMoved;
        this.moveSpecifications = new ArrayList<>(Arrays.asList(customMoves));
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
    public void setPoint(Point point) {
        this.position = point;
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board) {
        return this.getMoves(board, null, null, false, false);
    }

    @Override
    public MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log, ThreatMap threats,
            boolean onlyAttacks, boolean includeDefends) {
        Set<Movement> movements = new HashSet<>();
        for (CustomMove spec : this.moveSpecifications) {
            movements.addAll(spec.toMovementList(board, threats, this.colour, this.position, onlyAttacks,
                    includeDefends));
        }
        return new MoveSet(movements);
    }

    public List<CustomMove> getMoveSpecs() {
        return this.moveSpecifications;
    }

    public void addMoveSpec(CustomMove move) {
        this.moveSpecifications.add(move);
    }

    @Override
    public boolean getHasMoved() {
        return hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public boolean canPromote(Plane<Piece> board) {
        // Temporary work-around. This should be defined on construction by a configuration object/string
        if (PieceType.PAWN.getCode().equals(this.code)) {
            return Colour.WHITE.equals(this.colour) && this.getPoint().getY() == board.getMaxY()
                    || Colour.BLACK.equals(this.colour) && this.getPoint().getY() == board.getMinY();
        } else {
            return false;
        }
    }

    @Override
    public List<String> promoteOptions() {
        // Temporary work-around. This should be defined on construction
        if (PieceType.PAWN.getCode().equals(this.code)) {
            return List.of(PieceType.QUEEN.getCode(), PieceType.KNIGHT.getCode(), PieceType.ROOK.getCode(),
                    PieceType.BISHOP.getCode());
        } else {
            return List.of();
        }
    }

    @Override
    public String toString() {
        return this.colour.toCode() + this.getCode() + this.position.toString() + (this.hasMoved ? "" : "*");
    }

}
