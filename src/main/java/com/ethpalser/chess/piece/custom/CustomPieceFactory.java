package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.move.MoveSpec;
import com.ethpalser.chess.move.custom.condition.Comparator;
import com.ethpalser.chess.move.custom.condition.Conditional;
import com.ethpalser.chess.move.custom.condition.LogCondition;
import com.ethpalser.chess.move.custom.condition.PropertyCondition;
import com.ethpalser.chess.move.custom.condition.PropertyType;
import com.ethpalser.chess.move.custom.condition.ReferenceCondition;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.piece.standard.Bishop;
import com.ethpalser.chess.piece.standard.Knight;
import com.ethpalser.chess.piece.standard.Queen;
import com.ethpalser.chess.piece.standard.Rook;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Location;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Reference;
import com.ethpalser.chess.space.Space;
import java.util.List;
import java.util.Map;

public class CustomPieceFactory implements PieceFactory {

    private final Map<String, List<MoveSpec>> pieceSpecs;
    private final Space space;

    public CustomPieceFactory(Map<String, List<MoveSpec>> pieceSpecs, Space space) {
        this.pieceSpecs = pieceSpecs;
        this.space = space;
    }

    @Override
    public Piece create(String code, Colour colour, Coordinate coordinate) {
        return switch (PieceType.fromCode(code)) {
            case QUEEN -> new Queen(colour, coordinate);
            case BISHOP -> new Bishop(colour, coordinate);
            case KNIGHT -> new Knight(colour, coordinate);
            case ROOK -> new Rook(colour, coordinate);
            case KING -> this.king(colour, coordinate); // Different from default, as it fully utilizes MoveSpec
            case PAWN -> this.pawn(colour, coordinate); // Different from default, as it fully utilizes MoveSpec
            default -> this.custom(code, colour, coordinate);
        };
    }

    // PRIVATE METHODS

    // CONDITIONS

    private Conditional conditionSelfNotMoved() {
        return new PropertyCondition(new Reference(Location.POINT, Direction.AT), Comparator.FALSE,
                PropertyType.HAS_MOVED, false);
    }

    private Conditional conditionTargetNotMoved(Coordinate point) {
        return new PropertyCondition(new Reference(Location.POINT, Direction.AT, point),
                Comparator.FALSE, PropertyType.HAS_MOVED, false);
    }

    private Conditional conditionTargetIsRook(Coordinate point) {
        return new PropertyCondition(new Reference(Location.POINT, Direction.AT, point),
                Comparator.EQUAL, PropertyType.CODE, PieceType.ROOK.toCode());
    }

    private Conditional conditionPathIsEmpty(Coordinate start, Coordinate end) {
        return new ReferenceCondition(new Reference(Location.PATH, Direction.AT, new Path(start, end)),
                Comparator.EQUAL, null);
    }

    private Conditional conditionLastMovedIsPawn() {
        return new PropertyCondition(new Reference(Location.LAST_MOVED, Direction.AT), Comparator.EQUAL,
                PropertyType.CODE, PieceType.PAWN.toCode());
    }

    private Conditional conditionLastMovedIsAtDirection(Direction direction) {
        return new ReferenceCondition(new Reference(Location.LAST_MOVED, Direction.AT), Comparator.EQUAL,
                new Reference(Location.POINT, direction));
    }

    private Conditional conditionLastMovedTwo() {
        return new LogCondition(Comparator.EQUAL, PropertyType.DISTANCE_MOVED, 2);
    }

    // PIECES

    private CustomPiece king(Colour colour, Coordinate coordinate) {
        MoveSpec vSpec = new MoveSpec(new Path(new Point(0, 1)), true, false);
        MoveSpec hSpec = new MoveSpec(new Path(new Point(1, 0)), false, true);
        MoveSpec dSpec = new MoveSpec(new Path(new Point(1, 1)), true, true);

        // Special Move: Castle
        int startRank = Colour.WHITE.equals(colour) ? this.space.min(Space.AXIS.Y) : this.space.max(Space.AXIS.Y);
        // todo: replace with king's relative location when creating coordinates
        Coordinate kingStart = new Point(4, startRank);

        // region Queen-side Castle
        // Queen-side rook is moved to the right of the king's destination
        Coordinate qskEnd = kingStart.translate(2, Direction.LEFT);
        // Queen-side rook starts at the left-most edge of the board
        Coordinate qsrStart = new Point(this.space.min(Space.AXIS.X), startRank);
        // This move is expected to move two along the x-axis, and to the left (mirror y-axis is true)
        MoveSpec castleQueen = (new MoveSpec.Builder(new Path(new Point(2, 0))))
                .isAttack(false)
                .isMirrorXAxis(false)
                .isMirrorYAxis(true)
                .isSpecificQuadrant(true)
                // todo: add condition options to verify them more dynamically. These only use fixed coordinates.
                .conditions(List.of(
                        this.conditionSelfNotMoved(),
                        this.conditionTargetNotMoved(qsrStart),
                        this.conditionTargetIsRook(qsrStart),
                        this.conditionPathIsEmpty(
                                kingStart.translate(1, Direction.LEFT),
                                qsrStart.translate(1, Direction.RIGHT)
                        )
                ))
                .followUp(
                        new Reference(Location.POINT, Direction.AT, qsrStart),
                        new Path(qskEnd.translate(1, Direction.RIGHT)))
                .build();
        // endregion
        // region King-side castle
        // King-side rook is moved to the left of the king's destination
        Coordinate kskEnd = kingStart.translate(2, Direction.RIGHT);
        // King-side rook starts at the right-most edge of the board
        Point ksrStart = new Point(this.space.max(Space.AXIS.X), startRank);
        // This move is expected to move two along the x-axis, and to the right (mirror y-axis is false)
        MoveSpec castleKing = (new MoveSpec.Builder(new Path(new Point(2, 0))))
                .isAttack(false)
                .isMirrorXAxis(false)
                .isMirrorYAxis(false)
                .isSpecificQuadrant(true)
                // todo: add condition options to verify them more dynamically. These only use fixed coordinates.
                .conditions(List.of(
                        this.conditionSelfNotMoved(),
                        this.conditionTargetNotMoved(ksrStart),
                        this.conditionTargetIsRook(ksrStart),
                        this.conditionPathIsEmpty(
                                kingStart.translate(1, Direction.RIGHT),
                                ksrStart.translate(1, Direction.LEFT))
                ))
                .followUp(
                        new Reference(Location.POINT, Direction.AT, ksrStart),
                        new Path(kskEnd.translate(1, Direction.LEFT)))
                .build();
        // endregion
        return new CustomPiece(PieceType.KING.toCode(), colour, coordinate, false,
                vSpec, hSpec, dSpec, castleKing, castleQueen);
    }

    private CustomPiece pawn(Colour colour, Coordinate coordinate) {
        MoveSpec moveOne = new MoveSpec.Builder(new Path(new Point(0, 1)))
                .isMirrorXAxis(false)
                .isMirrorYAxis(false)
                .isSpecificQuadrant(true)
                .isAttack(false)
                .build();
        // Pawns can only capture one space diagonal from their front
        MoveSpec capture = new MoveSpec.Builder(new Path(new Point(1, 1)))
                .isMirrorXAxis(false)
                .isMove(false)
                .build();
        // Pawns can move forward two spaces if they have not moved
        MoveSpec moveTwo = new MoveSpec.Builder(new Path(new Point(0, 1), new Point(0, 2)))
                .isMirrorXAxis(false)
                .isMirrorYAxis(false)
                .isSpecificQuadrant(true)
                .isAttack(false)
                .conditions(List.of(this.conditionSelfNotMoved()))
                .build();

        // En Passant is split into two due to limitations with References, as they don't have MoveSpec's mirroring
        // En Passant front-right
        MoveSpec epRight = new MoveSpec.Builder(new Path(new Point(1, 1)))
                .isMirrorXAxis(false)
                .isMirrorYAxis(false)
                .isSpecificQuadrant(true)
                .isAttack(false)
                .conditions(List.of(
                        this.conditionLastMovedIsPawn(),
                        this.conditionLastMovedIsAtDirection(Direction.RIGHT),
                        this.conditionLastMovedTwo()
                ))
                // This should use the piece's relative point for reference. The captured pawn is to the right.
                .followUp(new Reference(Location.POINT, Direction.RIGHT), null)
                .build();
        // En Passant front-left
        MoveSpec epLeft = new MoveSpec.Builder(new Path(new Point(1, 1)))
                .isMirrorXAxis(false)
                .isMirrorYAxis(true)
                .isSpecificQuadrant(true)
                .isAttack(false)
                .conditions(List.of(
                        this.conditionLastMovedIsPawn(),
                        this.conditionLastMovedIsAtDirection(Direction.LEFT),
                        this.conditionLastMovedTwo()
                ))
                // This should use the piece's relative point for reference. The captured pawn is to the left.
                .followUp(new Reference(Location.POINT, Direction.LEFT), null)
                .build();

        return new CustomPiece(PieceType.PAWN.toCode(), colour, coordinate, false,
                moveOne, capture, moveTwo, epRight, epLeft);
    }

    private CustomPiece custom(String code, Colour colour, Coordinate coordinate) {
        return new CustomPiece(code, colour, coordinate, false, this.pieceSpecs.get(code));
    }

}
