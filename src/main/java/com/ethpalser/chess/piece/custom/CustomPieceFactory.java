package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.log.ChessLogEntry;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.custom.CustomMove;
import com.ethpalser.chess.move.custom.CustomMoveType;
import com.ethpalser.chess.move.custom.condition.Comparator;
import com.ethpalser.chess.move.custom.condition.Conditional;
import com.ethpalser.chess.move.custom.condition.Property;
import com.ethpalser.chess.move.custom.condition.PropertyCondition;
import com.ethpalser.chess.move.custom.condition.ReferenceCondition;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.reference.Location;
import com.ethpalser.chess.space.reference.PathReference;
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
        Path vertical = new Path(new Point(0, 1), new Point(0, 7));
        Path horizontal = new Path(new Point(1, 0), new Point(7, 0));
        Path diagonal = new Path(new Point(1, 1), new Point(7, 7));
        PropertyCondition<Piece> notMoved = new PropertyCondition<>(new PathReference<>(Location.PATH_START),
                Comparator.FALSE,
                new Property<>("hasMoved"), false);

        switch (type) {
            case KNIGHT -> {
                CustomMove knightMove1 = new CustomMove(new Path(new Point(1, 2)), CustomMoveType.JUMP, true, true);
                CustomMove knightMove2 = new CustomMove(new Path(new Point(2, 1)), CustomMoveType.JUMP, true, true);
                return new CustomPiece(PieceType.KNIGHT, colour, vector, hasMoved, knightMove1, knightMove2);
            }
            case ROOK -> {
                CustomMove rookMoveV = new CustomMove(vertical, CustomMoveType.ADVANCE, true, false);
                CustomMove rookMoveH = new CustomMove(horizontal, CustomMoveType.ADVANCE, false, true);
                return new CustomPiece(PieceType.ROOK, colour, vector, hasMoved, rookMoveV, rookMoveH);
            }
            case BISHOP -> {
                CustomMove bishopBaseMove = new CustomMove(diagonal, CustomMoveType.ADVANCE, true, true);
                return new CustomPiece(PieceType.BISHOP, colour, vector, hasMoved, bishopBaseMove);
            }
            case QUEEN -> {
                CustomMove queenBaseMoveV = new CustomMove(vertical, CustomMoveType.ADVANCE, true, false);
                CustomMove queenBaseMoveH = new CustomMove(horizontal, CustomMoveType.ADVANCE, false, true);
                CustomMove queenBaseMoveD = new CustomMove(diagonal, CustomMoveType.ADVANCE, true, true);
                return new CustomPiece(PieceType.QUEEN, colour, vector, hasMoved, queenBaseMoveV, queenBaseMoveH,
                        queenBaseMoveD);
            }
            case KING -> {
                CustomMove kingBaseMoveV = new CustomMove(new Path(new Point(0, 1)), CustomMoveType.ADVANCE, true,
                        false);
                CustomMove kingBaseMoveH = new CustomMove(new Path(new Point(1, 0)), CustomMoveType.ADVANCE, false,
                        true);
                CustomMove kingBaseMoveD = new CustomMove(new Path(new Point(1, 1)), CustomMoveType.ADVANCE, true,
                        true);
                Point kingSideRook = new Point(7, 0);
                Conditional<Piece> castleKingSideCond2 = new PropertyCondition<>(new PathReference<>(Location.POINT,
                        kingSideRook),
                        Comparator.FALSE, new Property<>("hasMoved"), false);
                Conditional<Piece> castleKingSideCond3 = new ReferenceCondition<>(new PathReference<>(Location.PATH,
                        kingSideRook),
                        Comparator.DOES_NOT_EXIST, null);
                CustomMove castleKingSide = new CustomMove.Builder(new Path(new Point(2, 0)), CustomMoveType.CHARGE)
                        .isMirrorXAxis(false)
                        .isMirrorYAxis(false)
                        .isSpecificQuadrant(true)
                        .isAttack(false)
                        .conditions(List.of(notMoved, castleKingSideCond2, castleKingSideCond3))
                        .followUp(new ChessLogEntry(kingSideRook, new Point(5, 0), this.board.getPiece(kingSideRook)))
                        .build();
                // Castle - Queen side
                Point queenSideRook = new Point(0, 0);
                Conditional<Piece> castleQueenSideCond2 = new PropertyCondition<>(new PathReference<>(Location.POINT,
                        queenSideRook),
                        Comparator.FALSE, new Property<>("hasMoved"), false);
                Conditional<Piece> castleQueenSideCond3 = new ReferenceCondition<>(new PathReference<>(Location.PATH,
                        queenSideRook),
                        Comparator.DOES_NOT_EXIST, null);
                CustomMove castleQueenSide = new CustomMove.Builder(new Path(new Point(2, 0)), CustomMoveType.CHARGE)
                        .isMirrorXAxis(false)
                        .isMirrorYAxis(true)
                        .isSpecificQuadrant(false)
                        .isAttack(false)
                        .conditions(List.of(notMoved, castleQueenSideCond2, castleQueenSideCond3))
                        .followUp(new ChessLogEntry(queenSideRook, new Point(3, 0), this.board.getPiece(queenSideRook)))
                        .build();

                return new CustomPiece(PieceType.KING, colour, vector, hasMoved, kingBaseMoveV, kingBaseMoveH,
                        kingBaseMoveD,
                        castleKingSide, castleQueenSide);
            }
            case PAWN -> {
                CustomMove pawnBaseMove = new CustomMove.Builder(new Path(new Point(0, 1)), CustomMoveType.ADVANCE)
                        .isMirrorXAxis(false)
                        .isMirrorYAxis(false)
                        .isSpecificQuadrant(true)
                        .isAttack(false)
                        .build();
                CustomMove pawnCharge = new CustomMove.Builder(new Path(new Point(0, 1), new Point(0, 2)),
                        CustomMoveType.CHARGE)
                        .isMirrorXAxis(false)
                        .isMirrorYAxis(false)
                        .isSpecificQuadrant(true)
                        .isAttack(false)
                        .conditions(List.of(notMoved))
                        .build();
                CustomMove pawnCapture = new CustomMove.Builder(new Path(new Point(1, 1)), CustomMoveType.ADVANCE)
                        .isMirrorXAxis(false)
                        .isMove(false)
                        .build();

                Conditional<Piece> enPassantCond1 = new PropertyCondition<>(new PathReference<>(Location.LAST_MOVED),
                        Comparator.EQUAL, new Property<>("type"), PieceType.PAWN);
                Conditional<Piece> enPassantCond2 = new ReferenceCondition<>(new PathReference<>(Location.LAST_MOVED),
                        Comparator.EQUAL, new PathReference<>(Location.PATH_END, vector.shift(colour, Direction.BACK)));
                Conditional<Piece> enPassantCond3 = new PropertyCondition<>(new PathReference<>(Location.LAST_MOVED),
                        Comparator.EQUAL, new Property<>("lastMoveDistance"), 2);

                LogEntry<Point, Piece> followUp = new ChessLogEntry(vector, null,
                        this.board.getPiece(vector.shift(colour, Direction.BACK)));
                CustomMove enPassant = new CustomMove.Builder(new Path(new Point(1, 1)), CustomMoveType.ADVANCE)
                        .isMirrorXAxis(false)
                        .isAttack(false)
                        .conditions(List.of(enPassantCond1, enPassantCond2, enPassantCond3))
                        .followUp(followUp)
                        .build();
                return new CustomPiece(PieceType.PAWN, colour, vector, hasMoved, pawnBaseMove, pawnCharge,
                        pawnCapture, enPassant);
            }
        }
        return null;
    }

}
