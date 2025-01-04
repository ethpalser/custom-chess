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

    private Conditional selfNotMovedCondition() {
        return new PropertyCondition(new Reference(Direction.AT, Location.POINT), Comparator.FALSE,
                PropertyType.HAS_MOVED, false);
    }

    private Conditional targetNotMovedCondition(Coordinate point) {
        return new PropertyCondition(new Reference(Direction.AT, Location.POINT, point),
                Comparator.FALSE, PropertyType.HAS_MOVED, false);
    }

    private Conditional targetIsPieceTypeCondition(Coordinate point, PieceType type) {
        return new PropertyCondition(new Reference(Direction.AT, Location.POINT, point),
                Comparator.EQUAL, PropertyType.TYPE, type);
    }

    private Conditional emptyPathCondition(Coordinate start, Coordinate end) {
        return new ReferenceCondition(new Reference(Direction.AT, Location.PATH, new Path(start, end)),
                Comparator.EQUAL, null);
    }

    private Conditional lastMovedIsPieceTypeCondition(PieceType type) {
        return new PropertyCondition(new Reference(Direction.AT, Location.LAST_MOVED), Comparator.EQUAL,
                PropertyType.TYPE, type);
    }

    private Conditional lastMovedIsNearbyPieceCondition(Direction direction) {
        return new ReferenceCondition(new Reference(Direction.AT, Location.LAST_MOVED), Comparator.EQUAL,
                new Reference(direction, Location.POINT));
    }

    private Conditional lastMovedTravelledDistanceCondition(int distance) {
        return new LogCondition(Comparator.EQUAL, PropertyType.DISTANCE_MOVED, distance);
    }

    // PIECES

    private CustomPiece king(Colour colour, Coordinate coordinate) {
        MoveSpec vSpec = new MoveSpec(new Path(new Point(0, 1)), true, false);
        MoveSpec hSpec = new MoveSpec(new Path(new Point(1, 0)), false, true);
        MoveSpec dSpec = new MoveSpec(new Path(new Point(1, 1)), true, true);

        // Special Move: Castle
        int startRank = Colour.WHITE.equals(colour) ? this.space.min(Space.AXIS.Y) : this.space.max(Space.AXIS.Y);
        // region Queen-side Castle
        Path castleQueenPath = new Path(
                coordinate.translate(1, Direction.LEFT.vector()),
                coordinate.translate(2, Direction.LEFT.vector()));
        // Queen-side rook is moved to the right of the king's destination
        Coordinate qskEnd = castleQueenPath.getPoint(castleQueenPath.length() - 1);
        // Queen-side rook starts at the left-most edge of the board
        Coordinate qsrStart = new Point(this.space.min(Space.AXIS.X), startRank);

        MoveSpec castleQueen = (new MoveSpec.Builder(castleQueenPath))
                .isAttack(false)
                .isMirrorXAxis(false)
                .isMirrorYAxis(false)
                .isSpecificQuadrant(true)
                .conditions(List.of(
                        this.selfNotMovedCondition(),
                        this.targetNotMovedCondition(qsrStart),
                        this.targetIsPieceTypeCondition(qsrStart, PieceType.ROOK),
                        this.emptyPathCondition(
                                coordinate.translate(1, Direction.LEFT.vector()),
                                qsrStart.translate(1, Direction.RIGHT.vector())
                        )
                ))
                .followUp(
                        new Reference(Direction.AT, Location.POINT, qsrStart),
                        new Path(qsrStart.translate(1, Direction.RIGHT.vector()),
                                qskEnd.translate(1, Direction.RIGHT.vector())))
                .build();
        // endregion
        // region King-side castle
        Path castleKingPath = new Path(
                coordinate.translate(1, Direction.RIGHT.vector()),
                coordinate.translate(2, Direction.RIGHT.vector()));
        // King-side rook is moved to the left of the king's destination
        Coordinate kskEnd = castleQueenPath.getPoint(castleQueenPath.length() - 1);
        // King-side rook starts at the right-most edge of the board
        Point ksrStart = new Point(this.space.max(Space.AXIS.X), startRank);

        MoveSpec castleKing = (new MoveSpec.Builder(castleKingPath))
                .isAttack(false)
                .isMirrorXAxis(false)
                .isMirrorYAxis(false)
                .isSpecificQuadrant(true)
                .conditions(List.of(
                        this.selfNotMovedCondition(),
                        this.targetNotMovedCondition(ksrStart),
                        this.targetIsPieceTypeCondition(ksrStart, PieceType.ROOK),
                        this.emptyPathCondition(
                                coordinate.translate(1, Direction.RIGHT.vector()),
                                ksrStart.translate(1, Direction.LEFT.vector()))
                ))
                .followUp(
                        new Reference(Direction.AT, Location.POINT, ksrStart),
                        new Path(ksrStart.translate(1, Direction.LEFT.vector()),
                                kskEnd.translate(1, Direction.LEFT.vector())))
                .build();
        // endregion
        return new CustomPiece(PieceType.KING.getCode(), colour, coordinate, false,
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
                .conditions(List.of(this.selfNotMovedCondition()))
                .build();

        // En Passant is split into two due to limitations with References, as they don't have MoveSpec's mirroring
        // En Passant front-right
        MoveSpec epRight = new MoveSpec.Builder(new Path(new Point(1, 1)))
                .isMirrorXAxis(false)
                .isMirrorYAxis(false)
                .isSpecificQuadrant(true)
                .isAttack(false)
                .conditions(List.of(
                        this.lastMovedIsPieceTypeCondition(PieceType.PAWN),
                        this.lastMovedIsNearbyPieceCondition(Direction.RIGHT),
                        this.lastMovedTravelledDistanceCondition(2)
                ))
                // This should use the piece's relative point for reference. The captured pawn is to the right.
                .followUp(new Reference(Direction.RIGHT, Location.POINT), null)
                .build();
        // En Passant front-left
        MoveSpec epLeft = new MoveSpec.Builder(new Path(new Point(1, 1)))
                .isMirrorXAxis(false)
                .isMirrorYAxis(true)
                .isSpecificQuadrant(true)
                .isAttack(false)
                .conditions(List.of(
                        this.lastMovedIsPieceTypeCondition(PieceType.PAWN),
                        this.lastMovedIsNearbyPieceCondition(Direction.LEFT),
                        this.lastMovedTravelledDistanceCondition(2)
                ))
                // This should use the piece's relative point for reference. The captured pawn is to the left.
                .followUp(new Reference(Direction.LEFT, Location.POINT), null)
                .build();

        return new CustomPiece(PieceType.PAWN.getCode(), colour, coordinate, false,
                moveOne, capture, moveTwo, epRight, epLeft);
    }

    private CustomPiece custom(String code, Colour colour, Coordinate coordinate) {
        return new CustomPiece(code, colour, coordinate, false, this.pieceSpecs.get(code));
    }

}
