package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.log.custom.ReferenceLogEntry;
import com.ethpalser.chess.move.custom.CustomMove;
import com.ethpalser.chess.move.custom.CustomMoveType;
import com.ethpalser.chess.move.custom.condition.Comparator;
import com.ethpalser.chess.move.custom.condition.Conditional;
import com.ethpalser.chess.move.custom.condition.LogCondition;
import com.ethpalser.chess.move.custom.condition.PropertyCondition;
import com.ethpalser.chess.move.custom.condition.PropertyType;
import com.ethpalser.chess.move.custom.condition.ReferenceCondition;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceFactory;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import com.ethpalser.chess.space.custom.Location;
import com.ethpalser.chess.space.custom.reference.AbsoluteReference;
import com.ethpalser.chess.space.custom.reference.LogReference;
import com.ethpalser.chess.space.custom.reference.PathReference;
import com.ethpalser.chess.space.custom.reference.PieceReference;
import com.ethpalser.chess.view.MoveView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomPieceFactory implements PieceFactory {

    private final Map<String, List<MoveView>> pieceSpecs;
    private final Space space;
    private final Log<Coordinate, Piece> log;

    public CustomPieceFactory(Map<String, List<MoveView>> pieceSpecs,
            Log<Coordinate, Piece> log,
            Space space) {
        this.pieceSpecs = pieceSpecs;
        this.space = space; // Todo: Decouple this from pieces and factory
        this.log = log; // Todo: Decouple this from pieces and factory
    }

    @Override
    public Piece create(Colour colour, String code) {
        return switch (PieceType.fromCode(code)) {
            case KNIGHT -> this.knight(colour);
            case ROOK -> this.rook(colour);
            case BISHOP -> this.bishop(colour);
            case QUEEN -> this.queen(colour);
            case KING -> this.king(colour);
            case PAWN -> this.pawn(colour);
            default -> this.custom(colour, code);
        };
    }

    // PRIVATE METHODS

    // CONDITIONS

    private Conditional<Piece> selfNotMovedCondition(Piece piece) {
        return new PropertyCondition<>(new PieceReference(piece), Comparator.FALSE,
                PropertyType.HAS_MOVED, false);
    }

    private Conditional<Piece> targetNotMovedCondition(Point point) {
        return new PropertyCondition<>(new AbsoluteReference<>(point), Comparator.FALSE,
                PropertyType.HAS_MOVED, false);
    }

    private Conditional<Piece> targetIsPieceTypeCondition(Point point, PieceType type) {
        return new PropertyCondition<>(new AbsoluteReference<>(point), Comparator.EQUAL,
                PropertyType.TYPE, type);
    }

    private Conditional<Piece> emptyPathCondition(Point start, Point end) {
        return new ReferenceCondition<>(new PathReference(Location.PATH, start, end), Comparator.EQUAL,
                null);
    }

    private Conditional<Piece> lastMovedIsPieceTypeCondition(PieceType type) {
        return new PropertyCondition<>(new LogReference<>(this.log), Comparator.EQUAL,
                PropertyType.TYPE, type);
    }

    private Conditional<Piece> lastMovedIsNearbyPieceCondition(Piece piece, int shiftX, int shiftY) {
        return new ReferenceCondition<>(new LogReference<>(this.log), Comparator.EQUAL,
                new PieceReference(piece, Direction.AT, shiftX, shiftY));
    }

    private Conditional<Piece> lastMovedTravelledDistanceCondition(int distance) {
        return new LogCondition<>(this.log, Comparator.EQUAL, PropertyType.DISTANCE_MOVED, distance);
    }

    // PATHS

    private Path vertical() {
        List<Point> points = new ArrayList<>();
        // Assuming origin (minX, minY) is occupied by piece, and the piece cannot move to its own location
        Point current = new Point(0, 1);
        while (!this.space.isOutOfBounds(current) && !this.space.isUnavailable(current)) {
            points.add(current);
            current = (Point) current.translate(1, 1, 0);
        }
        return new Path(points);
    }

    private Path horizontal() {
        List<Point> points = new ArrayList<>();
        // Assuming origin (minX, minY) is occupied by piece, and the piece cannot move to its own location
        Point current = new Point(0, 1);
        while (!this.space.isOutOfBounds(current) && !this.space.isUnavailable(current)) {
            points.add(current);
            current = (Point) current.translate(1, 0, 1);
        }
        return new Path(points);
    }

    private Path diagonal() {
        List<Point> points = new ArrayList<>();
        // Assuming origin (minX, minY) is occupied by piece, and the piece cannot move to its own location
        Point current = new Point(0, 1);
        while (!this.space.isOutOfBounds(current) && !this.space.isUnavailable(current)) {
            points.add(current);
            current = (Point) current.translate(1, 1, 1);
        }
        return new Path(points);
    }

    // PIECES

    private CustomPiece knight(Colour colour) {
        CustomMove baseMoveL1 = new CustomMove(new Path(new Point(1, 2)), CustomMoveType.JUMP, true, true);
        CustomMove baseMoveL2 = new CustomMove(new Path(new Point(2, 1)), CustomMoveType.JUMP, true, true);
        return new CustomPiece(PieceType.KNIGHT.getCode(), colour, Point.ORIGIN, false, baseMoveL1, baseMoveL2);
    }

    private CustomPiece rook(Colour colour) {
        CustomMove baseMoveV = new CustomMove(this.vertical(), CustomMoveType.ADVANCE, true, false);
        CustomMove baseMoveH = new CustomMove(this.horizontal(), CustomMoveType.ADVANCE, false, true);
        return new CustomPiece(PieceType.ROOK.getCode(), colour, Point.ORIGIN, false, baseMoveV, baseMoveH);
    }

    private CustomPiece bishop(Colour colour) {
        CustomMove baseMoveD = new CustomMove(this.diagonal(), CustomMoveType.ADVANCE, true, true);
        return new CustomPiece(PieceType.BISHOP.getCode(), colour, Point.ORIGIN, false, baseMoveD);
    }

    private CustomPiece queen(Colour colour) {
        CustomMove baseMoveV = new CustomMove(this.vertical(), CustomMoveType.ADVANCE, true, false);
        CustomMove baseMoveH = new CustomMove(this.horizontal(), CustomMoveType.ADVANCE, false, true);
        CustomMove baseMoveD = new CustomMove(this.diagonal(), CustomMoveType.ADVANCE, true, true);
        return new CustomPiece(PieceType.QUEEN.getCode(), colour, Point.ORIGIN, false, baseMoveV, baseMoveH, baseMoveD);
    }

    private CustomPiece king(Colour colour) {
        CustomMove baseMoveV = new CustomMove(new Path(new Point(0, 1)), CustomMoveType.ADVANCE, true, false);
        CustomMove baseMoveH = new CustomMove(new Path(new Point(1, 0)), CustomMoveType.ADVANCE, false, true);
        CustomMove baseMoveD = new CustomMove(new Path(new Point(1, 1)), CustomMoveType.ADVANCE, true, true);
        CustomPiece king = new CustomPiece(PieceType.KING.getCode(), colour, Point.ORIGIN, false, baseMoveV, baseMoveH,
                baseMoveD);

        {
            // Castle - King side
            Point kingSideRook = new Point(7, 0); // Assuming a standard board
            CustomMove castleKingSide = new CustomMove.Builder(new Path(new Point(2, 0)), CustomMoveType.CHARGE)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(false)
                    .isSpecificQuadrant(true)
                    .isAttack(false)
                    .conditions(List.of(
                            this.selfNotMovedCondition(king),
                            this.targetNotMovedCondition(kingSideRook),
                            this.targetIsPieceTypeCondition(kingSideRook, PieceType.ROOK),
                            // Todo: Update pieces to have an internal start location, then use this off of its start
                            this.emptyPathCondition(new Point().shift(colour, Direction.RIGHT), kingSideRook)
                    ))
                    .followUp(new ChessLogEntry(kingSideRook, new Point(5, 0), null))
                    .build();
            king.addMoveSpec(castleKingSide);
        }
        {
            // Castle - Queen side
            Point queenSideRook = new Point(0, 0); // Assuming a standard board
            CustomMove castleQueenSide = new CustomMove.Builder(new Path(new Point(2, 0)), CustomMoveType.CHARGE)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(true)
                    .isSpecificQuadrant(false)
                    .isAttack(false)
                    .conditions(List.of(
                            this.selfNotMovedCondition(king),
                            this.targetNotMovedCondition(queenSideRook),
                            this.targetIsPieceTypeCondition(queenSideRook, PieceType.ROOK),
                            // Todo: Update pieces to have an internal start location, then use this off of its start
                            this.emptyPathCondition(new Point().shift(colour, Direction.LEFT), queenSideRook)
                    ))
                    .followUp(new ChessLogEntry(queenSideRook, new Point(3, 0), null))
                    .build();
            king.addMoveSpec(castleQueenSide);
        }
        return king;
    }

    private CustomPiece pawn(Colour colour) {
        CustomMove baseMove = new CustomMove.Builder(new Path(new Point(0, 1)), CustomMoveType.ADVANCE)
                .isMirrorXAxis(false)
                .isMirrorYAxis(false)
                .isSpecificQuadrant(true)
                .isAttack(false)
                .build();
        CustomPiece pawn = new CustomPiece(PieceType.PAWN.getCode(), colour, Point.ORIGIN, false, baseMove);

        {
            // Pawns can only capture one space diagonal from their front
            CustomMove pawnCapture = new CustomMove.Builder(new Path(new Point(1, 1)), CustomMoveType.ADVANCE)
                    .isMirrorXAxis(false)
                    .isMove(false)
                    .build();
            pawn.addMoveSpec(pawnCapture);
        }
        {
            // Pawns can move forward two spaces if they have not moved
            CustomMove pawnCharge = new CustomMove.Builder(new Path(new Point(0, 1), new Point(0, 2)),
                    CustomMoveType.ADVANCE)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(false)
                    .isSpecificQuadrant(true)
                    .isAttack(false)
                    .conditions(List.of(this.selfNotMovedCondition(pawn)))
                    .build();
            pawn.addMoveSpec(pawnCharge);
        }

        // En Passant is split into two due to limitations with References, as refs don't have CustomPiece's mirroring
        {
            // En Passant front-right
            LogEntry<Coordinate, Piece> followUpRight = new ReferenceLogEntry<>(null, // remove board dependency
                    new PieceReference(pawn, Direction.AT, 1, 0), null);
            CustomMove enPassantRight = new CustomMove.Builder(new Path(new Point(1, 1)), CustomMoveType.ADVANCE)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(false)
                    .isSpecificQuadrant(true)
                    .isAttack(false)
                    .conditions(List.of(
                            this.lastMovedIsPieceTypeCondition(PieceType.PAWN),
                            this.lastMovedIsNearbyPieceCondition(null, 1, 0),
                            this.lastMovedTravelledDistanceCondition(2)
                    ))
                    .followUp(followUpRight)
                    .build();
            pawn.addMoveSpec(enPassantRight);
        }
        {
            // En Passant front-left
            LogEntry<Coordinate, Piece> followUpLeft = new ReferenceLogEntry<>(null, // remove board dependency
                    new PieceReference(pawn, Direction.AT, -1, 0), null);
            CustomMove enPassantLeft = new CustomMove.Builder(new Path(new Point(1, 1)), CustomMoveType.ADVANCE)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(true)
                    .isSpecificQuadrant(true)
                    .isAttack(false)
                    .conditions(List.of(
                            this.lastMovedIsPieceTypeCondition(PieceType.PAWN),
                            this.lastMovedIsNearbyPieceCondition(null, -1, 0),
                            this.lastMovedTravelledDistanceCondition(2)
                    ))
                    .followUp(followUpLeft)
                    .build();
            pawn.addMoveSpec(enPassantLeft);
        }
        return pawn;
    }

    private CustomPiece custom(Colour colour, String code) {
        CustomPiece piece = new CustomPiece(code, colour, Point.ORIGIN, false);
        for (MoveView spec : this.pieceSpecs.get(code)) {
            // piece.addMoveSpec(new CustomMove(this.board, this.log, spec)); // todo: fix movement to not depend on board
        }
        return piece;
    }

}
