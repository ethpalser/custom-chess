package com.ethpalser.chess.piece.custom;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.board.Point;
import com.ethpalser.chess.piece.custom.condition.Comparator;
import com.ethpalser.chess.piece.custom.condition.Conditional;
import com.ethpalser.chess.piece.custom.condition.Property;
import com.ethpalser.chess.piece.custom.condition.PropertyCondition;
import com.ethpalser.chess.piece.custom.condition.ReferenceCondition;
import com.ethpalser.chess.piece.custom.movement.ExtraAction;
import com.ethpalser.chess.piece.custom.movement.Movement;
import com.ethpalser.chess.piece.custom.movement.MovementType;
import com.ethpalser.chess.piece.custom.movement.Path;
import com.ethpalser.chess.piece.custom.reference.Direction;
import com.ethpalser.chess.piece.custom.reference.Location;
import com.ethpalser.chess.piece.custom.reference.Reference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomPieceFactory {

    private static CustomPieceFactory factory;

    private CustomPieceFactory() {
    }

    public static CustomPieceFactory getInstance() {
        if (factory == null) {
            factory = new CustomPieceFactory();
        }
        return factory;
    }

    public CustomPiece build(String string) {
        Pattern pattern = Pattern.compile("^[A-Ha-h][1-8]\\*?#[wb][PRNBQK]");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            String[] parts = string.split("#");
            Point point = new Point(parts[0].charAt(0), parts[0].charAt(1));
            CustomPiece customPiece = this.build(PieceType.fromCode(parts[1].substring(1)), Colour.fromCode(parts[1].substring(0,
                            1)),
                    point);
            // todo: add this to constructor
            // customPiece.setHasMoved(!parts[0].contains("*"));
            return customPiece;
        } else {
            throw new IllegalArgumentException("String (" + string + ") does not match the required format.");
        }
    }

    public CustomPiece build(PieceType type, Colour colour, Point vector) {
        Path vertical = new Path(new Point(0, 1), new Point(0, 7));
        Path horizontal = new Path(new Point(1, 0), new Point(7, 0));
        Path diagonal = new Path(new Point(1, 1), new Point(7, 7));
        PropertyCondition notMoved = new PropertyCondition(new Reference(Location.START), Comparator.FALSE,
                new Property<>("hasMoved"), false);

        switch (type) {
            case KNIGHT -> {
                Movement knightMove1 = new Movement(new Path(new Point(1, 2)), MovementType.JUMP, true, true);
                Movement knightMove2 = new Movement(new Path(new Point(2, 1)), MovementType.JUMP, true, true);
                return new CustomPiece(PieceType.KNIGHT, colour, vector, knightMove1, knightMove2);
            }
            case ROOK -> {
                Movement rookMoveV = new Movement(vertical, MovementType.ADVANCE, true, false);
                Movement rookMoveH = new Movement(horizontal, MovementType.ADVANCE, false, true);
                return new CustomPiece(PieceType.ROOK, colour, vector, rookMoveV, rookMoveH);
            }
            case BISHOP -> {
                Movement bishopBaseMove = new Movement(diagonal, MovementType.ADVANCE, true, true);
                return new CustomPiece(PieceType.BISHOP, colour, vector, bishopBaseMove);
            }
            case QUEEN -> {
                Movement queenBaseMoveV = new Movement(vertical, MovementType.ADVANCE, true, false);
                Movement queenBaseMoveH = new Movement(horizontal, MovementType.ADVANCE, false, true);
                Movement queenBaseMoveD = new Movement(diagonal, MovementType.ADVANCE, true, true);
                return new CustomPiece(PieceType.QUEEN, colour, vector, queenBaseMoveV, queenBaseMoveH, queenBaseMoveD);
            }
            case KING -> {
                Movement kingBaseMoveV = new Movement(new Path(new Point(0, 1)), MovementType.ADVANCE, true, false);
                Movement kingBaseMoveH = new Movement(new Path(new Point(1, 0)), MovementType.ADVANCE, false, true);
                Movement kingBaseMoveD = new Movement(new Path(new Point(1, 1)), MovementType.ADVANCE, true, true);
                // Castle - King side Todo: Implement moving to a fixed location so this and queen-side can be intuitive
                Point kingSideRook = new Point(7, 0);
                Conditional castleKingSideCond2 = new PropertyCondition(new Reference(Location.VECTOR, kingSideRook),
                        Comparator.FALSE, new Property<>("hasMoved"), false);
                Conditional castleKingSideCond3 = new ReferenceCondition(new Reference(Location.PATH_TO_VECTOR,
                        kingSideRook),
                        Comparator.DOES_NOT_EXIST, null);
                Movement castleKingSide = new Movement.Builder(new Path(new Point(2, 0)), MovementType.CHARGE)
                        .isMirrorXAxis(false)
                        .isMirrorYAxis(false)
                        .isSpecificQuadrant(true)
                        .isAttack(false)
                        .conditions(List.of(notMoved, castleKingSideCond2, castleKingSideCond3))
                        .extraAction(new ExtraAction(new Reference(Location.VECTOR, kingSideRook), new Point(5, 0)))
                        .build();
                // Castle - Queen side
                Point queenSideRook = new Point(0, 0);
                Conditional castleQueenSideCond2 = new PropertyCondition(new Reference(Location.VECTOR, queenSideRook),
                        Comparator.FALSE, new Property<>("hasMoved"), false);
                Conditional castleQueenSideCond3 = new ReferenceCondition(new Reference(Location.PATH_TO_VECTOR,
                        queenSideRook),
                        Comparator.DOES_NOT_EXIST, null);
                Movement castleQueenSide = new Movement.Builder(new Path(new Point(2, 0)), MovementType.CHARGE)
                        .isMirrorXAxis(false)
                        .isMirrorYAxis(true)
                        .isSpecificQuadrant(false)
                        .isAttack(false)
                        .conditions(List.of(notMoved, castleQueenSideCond2, castleQueenSideCond3))
                        .extraAction(new ExtraAction(new Reference(Location.VECTOR, new Point(0, 0)),
                                new Point(3, 0)))
                        .build();

                return new CustomPiece(PieceType.KING, colour, vector, kingBaseMoveV, kingBaseMoveH, kingBaseMoveD,
                        castleKingSide, castleQueenSide);
            }
            case PAWN -> {
                Movement pawnBaseMove = new Movement.Builder(new Path(new Point(0, 1)), MovementType.ADVANCE)
                        .isMirrorXAxis(false)
                        .isMirrorYAxis(false)
                        .isSpecificQuadrant(true)
                        .isAttack(false)
                        .build();
                Movement pawnCharge = new Movement.Builder(new Path(new Point(0, 1), new Point(0, 2)),
                        MovementType.CHARGE)
                        .isMirrorXAxis(false)
                        .isMirrorYAxis(false)
                        .isSpecificQuadrant(true)
                        .isAttack(false)
                        .conditions(List.of(notMoved))
                        .build();
                Movement pawnCapture = new Movement.Builder(new Path(new Point(1, 1)), MovementType.ADVANCE)
                        .isMirrorXAxis(false)
                        .isMove(false)
                        .build();

                Conditional enPassantCond1 = new PropertyCondition(new Reference(Location.LAST_MOVED),
                        Comparator.EQUAL, new Property<>("type"), PieceType.PAWN);
                Conditional enPassantCond2 = new ReferenceCondition(new Reference(Location.LAST_MOVED),
                        Comparator.EQUAL, new Reference(Location.DESTINATION, Direction.BACK, null));
                Conditional enPassantCond3 = new PropertyCondition(new Reference(Location.LAST_MOVED),
                        Comparator.EQUAL, new Property<>("lastMoveDistance"), 2);

                ExtraAction extraAction = new ExtraAction(new Reference(Location.DESTINATION, Direction.BACK, null),
                        null);
                Movement enPassant = new Movement.Builder(new Path(new Point(1, 1)), MovementType.ADVANCE)
                        .isMirrorXAxis(false)
                        .isAttack(false)
                        .conditions(List.of(enPassantCond1, enPassantCond2, enPassantCond3))
                        .extraAction(extraAction)
                        .build();
                return new CustomPiece(PieceType.PAWN, colour, vector, pawnBaseMove, pawnCharge, pawnCapture, enPassant);
            }
        }
        return null;
    }

}
