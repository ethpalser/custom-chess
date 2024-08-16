package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.log.custom.ReferenceLogEntry;
import com.ethpalser.chess.move.custom.CustomMove;
import com.ethpalser.chess.move.custom.CustomMoveType;
import com.ethpalser.chess.move.custom.condition.Comparator;
import com.ethpalser.chess.move.custom.condition.Conditional;
import com.ethpalser.chess.move.custom.condition.LogCondition;
import com.ethpalser.chess.move.custom.condition.Property;
import com.ethpalser.chess.move.custom.condition.PropertyCondition;
import com.ethpalser.chess.move.custom.condition.PropertyType;
import com.ethpalser.chess.move.custom.condition.ReferenceCondition;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.reference.AbsoluteReference;
import com.ethpalser.chess.space.reference.Location;
import com.ethpalser.chess.space.reference.LogReference;
import com.ethpalser.chess.space.reference.PathReference;
import com.ethpalser.chess.space.reference.PieceReference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomPieceFactory {

    private final Board board;
    private final Log<Point, Piece> log;

    public CustomPieceFactory(Board board, Log<Point, Piece> log) {
        this.board = board;
        this.log = log;
    }

    public CustomPiece build(String string) {
        Pattern pattern = Pattern.compile("^[A-Ha-h][1-8]\\*?#[wb][PRNBQK]");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            String[] parts = string.split("#");
            Point point = new Point(parts[0].charAt(0), parts[0].charAt(1));
            return this.build(PieceType.fromCode(parts[1].substring(1)), Colour.fromCode(parts[1].substring(0, 1)),
                    point, !parts[0].contains("*"));
        } else {
            throw new IllegalArgumentException("String (" + string + ") does not match the required format.");
        }
    }

    public CustomPiece build(PieceType type, Colour colour, Point vector, boolean hasMoved) {
        return switch (type) {
            case KNIGHT -> this.knight(colour, vector, hasMoved);
            case ROOK -> this.rook(colour, vector, hasMoved);
            case BISHOP -> this.bishop(colour, vector, hasMoved);
            case QUEEN -> this.queen(colour, vector, hasMoved);
            case KING -> this.king(colour, vector, hasMoved);
            case PAWN -> this.pawn(colour, vector, hasMoved);
            default -> new CustomPiece(type, colour, vector, hasMoved); // Todo: Allow adding moves
        };
    }

    // PRIVATE METHODS

    // CONDITIONS

    private Conditional<Piece> selfNotMovedCondition(Piece piece) {
        return new PropertyCondition<>(new PieceReference(piece), Comparator.FALSE,
                new Property<>("hasMoved"), false);
    }

    private Conditional<Piece> targetNotMovedCondition(Point point) {
        return new PropertyCondition<>(new AbsoluteReference<>(point), Comparator.FALSE,
                new Property<>("hasMoved"), false);
    }

    private Conditional<Piece> targetIsPieceTypeCondition(Point point, PieceType type) {
        return new PropertyCondition<>(new AbsoluteReference<>(point), Comparator.EQUAL,
                new Property<>("type"), type);
    }

    private Conditional<Piece> emptyPathCondition(Point start, Point end) {
        return new ReferenceCondition<>(new PathReference<>(Location.PATH, start, end), Comparator.DOES_NOT_EXIST,
                null);
    }

    private Conditional<Piece> lastMovedIsPieceTypeCondition(PieceType type) {
        return new PropertyCondition<>(new LogReference<>(this.log), Comparator.EQUAL,
                new Property<>("type"), type);
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
        Plane<Piece> plane = this.board.getPieces(); // Used for dimensions
        // Assuming origin (minX, minY) is occupied by piece, and the piece cannot move to its own location
        return new Path(new Point(plane.getMinX(), plane.getMinY() + 1), new Point(plane.getMinX(), plane.getMaxY()));
    }

    private Path horizontal() {
        Plane<Piece> plane = this.board.getPieces(); // Used for dimensions
        // Assuming origin (minX, minY) is occupied by piece, and the piece cannot move to its own location
        return new Path(new Point(plane.getMinX() + 1, plane.getMinY()), new Point(plane.getMaxX(), plane.getMinY()));
    }

    private Path diagonal() {
        Plane<Piece> plane = this.board.getPieces(); // Used for dimensions
        // Assuming origin (minX, minY) is occupied by piece, and the piece cannot move to its own location
        return new Path(new Point(plane.getMinX() + 1, plane.getMinY() + 1),
                new Point(plane.getMaxX(), plane.getMaxY()));
    }

    // PIECES

    private CustomPiece knight(Colour colour, Point point, boolean hasMoved) {
        CustomMove baseMoveL1 = new CustomMove(new Path(new Point(1, 2)), CustomMoveType.JUMP, true, true);
        CustomMove baseMoveL2 = new CustomMove(new Path(new Point(2, 1)), CustomMoveType.JUMP, true, true);
        return new CustomPiece(PieceType.KNIGHT, colour, point, hasMoved, baseMoveL1, baseMoveL2);
    }

    private CustomPiece rook(Colour colour, Point point, boolean hasMoved) {
        CustomMove baseMoveV = new CustomMove(this.vertical(), CustomMoveType.ADVANCE, true, false);
        CustomMove baseMoveH = new CustomMove(this.horizontal(), CustomMoveType.ADVANCE, false, true);
        return new CustomPiece(PieceType.ROOK, colour, point, hasMoved, baseMoveV, baseMoveH);
    }

    private CustomPiece bishop(Colour colour, Point point, boolean hasMoved) {
        CustomMove baseMoveD = new CustomMove(this.diagonal(), CustomMoveType.ADVANCE, true, true);
        return new CustomPiece(PieceType.BISHOP, colour, point, hasMoved, baseMoveD);
    }

    private CustomPiece queen(Colour colour, Point point, boolean hasMoved) {
        CustomMove baseMoveV = new CustomMove(this.vertical(), CustomMoveType.ADVANCE, true, false);
        CustomMove baseMoveH = new CustomMove(this.horizontal(), CustomMoveType.ADVANCE, false, true);
        CustomMove baseMoveD = new CustomMove(this.diagonal(), CustomMoveType.ADVANCE, true, true);
        return new CustomPiece(PieceType.QUEEN, colour, point, hasMoved, baseMoveV, baseMoveH, baseMoveD);
    }

    private CustomPiece king(Colour colour, Point point, boolean hasMoved) {
        CustomMove baseMoveV = new CustomMove(new Path(new Point(0, 1)), CustomMoveType.ADVANCE, true, false);
        CustomMove baseMoveH = new CustomMove(new Path(new Point(1, 0)), CustomMoveType.ADVANCE, false, true);
        CustomMove baseMoveD = new CustomMove(new Path(new Point(1, 1)), CustomMoveType.ADVANCE, true, true);
        CustomPiece king = new CustomPiece(PieceType.KING, colour, point, hasMoved, baseMoveV, baseMoveH, baseMoveD);

        Plane<Piece> plane = this.board.getPieces(); // For dimensions
        {
            // Castle - King side
            Point kingSideRook = new Point(plane.getMaxX(), plane.getMinY()); // Assuming a standard board
            CustomMove castleKingSide = new CustomMove.Builder(new Path(new Point(2, 0)), CustomMoveType.CHARGE)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(false)
                    .isSpecificQuadrant(true)
                    .isAttack(false)
                    .conditions(List.of(
                            this.selfNotMovedCondition(king),
                            this.targetNotMovedCondition(kingSideRook),
                            this.targetIsPieceTypeCondition(kingSideRook, PieceType.ROOK),
                            this.emptyPathCondition(point.shift(colour, Direction.RIGHT), kingSideRook)
                    ))
                    .followUp(new ChessLogEntry(kingSideRook, new Point(5, 0), this.board.getPiece(kingSideRook)))
                    .build();
            king.addMove(castleKingSide);
        }
        {
            // Castle - Queen side
            Point queenSideRook = new Point(plane.getMinX(), plane.getMinY()); // Assuming a standard board
            CustomMove castleQueenSide = new CustomMove.Builder(new Path(new Point(2, 0)), CustomMoveType.CHARGE)
                    .isMirrorXAxis(false)
                    .isMirrorYAxis(true)
                    .isSpecificQuadrant(false)
                    .isAttack(false)
                    .conditions(List.of(
                            this.selfNotMovedCondition(king),
                            this.targetNotMovedCondition(queenSideRook),
                            this.targetIsPieceTypeCondition(queenSideRook, PieceType.ROOK),
                            this.emptyPathCondition(point.shift(colour, Direction.LEFT), queenSideRook)
                    ))
                    .followUp(new ChessLogEntry(queenSideRook, new Point(3, 0), this.board.getPiece(queenSideRook)))
                    .build();
            king.addMove(castleQueenSide);
        }
        return king;
    }

    private CustomPiece pawn(Colour colour, Point point, boolean hasMoved) {
        CustomMove baseMove = new CustomMove.Builder(new Path(new Point(0, 1)), CustomMoveType.ADVANCE)
                .isMirrorXAxis(false)
                .isMirrorYAxis(false)
                .isSpecificQuadrant(true)
                .isAttack(false)
                .build();
        CustomPiece pawn = new CustomPiece(PieceType.PAWN, colour, point, hasMoved, baseMove);

        {
            // Pawns can only capture one space diagonal from their front
            CustomMove pawnCapture = new CustomMove.Builder(new Path(new Point(1, 1)), CustomMoveType.ADVANCE)
                    .isMirrorXAxis(false)
                    .isMove(false)
                    .build();
            pawn.addMove(pawnCapture);
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
            pawn.addMove(pawnCharge);
        }

        // En Passant is split into two due to limitations with References, as they don't have CustomPiece's mirroring
        {
            LogEntry<Point, Piece> followUpRight = new ReferenceLogEntry<>(this.board.getPieces(),
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
            pawn.addMove(enPassantRight);
        }
        {
            LogEntry<Point, Piece> followUpLeft = new ReferenceLogEntry<>(this.board.getPieces(),
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
            pawn.addMove(enPassantLeft);
        }
        return pawn;
    }

}
