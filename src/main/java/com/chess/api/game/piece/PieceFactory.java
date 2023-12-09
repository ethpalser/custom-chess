package com.chess.api.game.piece;

import com.chess.api.game.Colour;
import com.chess.api.game.Vector2D;
import com.chess.api.game.condition.Comparator;
import com.chess.api.game.condition.Conditional;
import com.chess.api.game.condition.Property;
import com.chess.api.game.condition.PropertyCondition;
import com.chess.api.game.condition.ReferenceCondition;
import com.chess.api.game.movement.ExtraAction;
import com.chess.api.game.movement.Movement;
import com.chess.api.game.movement.MovementType;
import com.chess.api.game.movement.Path;
import com.chess.api.game.reference.Direction;
import com.chess.api.game.reference.Location;
import com.chess.api.game.reference.Reference;
import java.util.List;

public class PieceFactory {

    private static PieceFactory factory;

    private PieceFactory() {
    }

    public static PieceFactory getInstance() {
        if (factory == null) {
            factory = new PieceFactory();
        }
        return factory;
    }

    public Piece build(PieceType type, Colour colour, Vector2D vector) {
        Path vertical = new Path(new Vector2D(0, 1), new Vector2D(0, 7));
        Path horizontal = new Path(new Vector2D(1, 0), new Vector2D(7, 0));
        Path diagonal = new Path(new Vector2D(1, 1), new Vector2D(7, 7));

        switch (type) {
            case KNIGHT -> {
                Movement knightBaseMove1 = new Movement(new Path(new Vector2D(1, 2)), MovementType.JUMP, true, true);
                Movement knightBaseMove2 = new Movement(new Path(new Vector2D(2, 1)), MovementType.JUMP, true, true);
                return new Piece(PieceType.KNIGHT, colour, vector, knightBaseMove1, knightBaseMove2);
            }
            case ROOK -> {
                Movement rookBaseMoveV = new Movement(vertical, MovementType.ADVANCE, true, false);
                Movement rookBaseMoveH = new Movement(horizontal, MovementType.ADVANCE, false, true);
                return new Piece(PieceType.ROOK, colour, vector, rookBaseMoveV, rookBaseMoveH);
            }
            case BISHOP -> {
                Movement bishopBaseMove = new Movement(diagonal, MovementType.ADVANCE, true, true);
                return new Piece(PieceType.BISHOP, colour, vector, bishopBaseMove);
            }
            case QUEEN -> {
                Movement queenBaseMoveV = new Movement(vertical, MovementType.ADVANCE, true, false);
                Movement queenBaseMoveH = new Movement(horizontal, MovementType.ADVANCE, false, true);
                Movement queenBaseMoveD = new Movement(diagonal, MovementType.ADVANCE, true, true);
                return new Piece(PieceType.QUEEN, colour, vector, queenBaseMoveV, queenBaseMoveH, queenBaseMoveD);
            }
            case KING -> {
                Movement kingBaseMoveV = new Movement(new Path(new Vector2D(0, 1)), MovementType.ADVANCE, true, false);
                Movement kingBaseMoveH = new Movement(new Path(new Vector2D(1, 0)), MovementType.ADVANCE, false, true);
                Movement kingBaseMoveD = new Movement(new Path(new Vector2D(1, 1)), MovementType.ADVANCE, true, true);

                Vector2D kingSideRook = new Vector2D(7, 0);
                Conditional castleKingSideCond2 = new PropertyCondition(new Reference(Location.VECTOR, kingSideRook),
                        Comparator.FALSE, new Property<>("hasMoved"), false);
                Conditional castleKingSideCond3 = new ReferenceCondition(new Reference(Location.PATH_TO_VECTOR,
                        kingSideRook),
                        Comparator.DOES_NOT_EXIST, null);
                ExtraAction kingSideRookMovement = new ExtraAction(new Reference(Location.VECTOR, new Vector2D(7, 0)),
                        new Vector2D(5, 0));
                Movement castleKingSide = new Movement(new Path(new Vector2D(2, 0)), MovementType.ADVANCE,
                        false, false, true,
                        List.of(PropertyCondition.startNotMoved(), castleKingSideCond2, castleKingSideCond3),
                        kingSideRookMovement);

                Vector2D queenSideRook = new Vector2D(0, 0);
                Conditional castleQueenSideCond2 = new PropertyCondition(new Reference(Location.VECTOR, queenSideRook),
                        Comparator.FALSE, new Property<>("hasMoved"), false);
                Conditional castleQueenSideCond3 = new ReferenceCondition(new Reference(Location.PATH_TO_VECTOR,
                        queenSideRook),
                        Comparator.DOES_NOT_EXIST, null);
                ExtraAction queenSideRookMovement = new ExtraAction(new Reference(Location.VECTOR, new Vector2D(0, 0)),
                        new Vector2D(3, 0));
                Movement castleQueenSide = new Movement(new Path(new Vector2D(2, 0)), MovementType.ADVANCE,
                        false, true, true,
                        List.of(PropertyCondition.startNotMoved(), castleQueenSideCond2, castleQueenSideCond3),
                        queenSideRookMovement);

                return new Piece(PieceType.KING, colour, vector, kingBaseMoveV, kingBaseMoveH, kingBaseMoveD,
                        castleKingSide, castleQueenSide);
            }
            case PAWN -> {
                Movement pawnBaseMove = new Movement(new Path(new Vector2D(0, 1)), MovementType.ADVANCE, false,
                        false, false, List.of(PropertyCondition.destinationEmpty()));
                Movement fastAdvance = new Movement(new Path(new Vector2D(0, 1), new Vector2D(0, 2)),
                        MovementType.ADVANCE, false, false, true,
                        List.of(PropertyCondition.destinationEmpty(), PropertyCondition.startNotMoved()));

                Movement capture = new Movement(new Path(new Vector2D(1, 1)), MovementType.ADVANCE,
                        false, true, false,
                        List.of(PropertyCondition.destinationNotEmpty(),
                                PropertyCondition.destinationColourNotEqual()));

                Conditional enPassantCond1 = new PropertyCondition(new Reference(Location.LAST_MOVED),
                        Comparator.EQUAL, new Property<>("type"), PieceType.PAWN);
                Conditional enPassantCond2 = new ReferenceCondition(new Reference(Location.LAST_MOVED),
                        Comparator.EQUAL, new Reference(Location.DESTINATION, Direction.BACK, null));
                Conditional enPassantCond3 = new PropertyCondition(new Reference(Location.LAST_MOVED),
                        Comparator.EQUAL, new Property<>("lastMoveDistance"), 2);

                ExtraAction extraAction = new ExtraAction(new Reference(Location.DESTINATION, Direction.BACK, null),
                        null);
                Movement enPassant = new Movement(new Path(new Vector2D(1, 1)), MovementType.ADVANCE,
                        false, true, false,
                        List.of(PropertyCondition.destinationEmpty(), enPassantCond1, enPassantCond2, enPassantCond3),
                        extraAction);
                return new Piece(PieceType.PAWN, colour, vector, pawnBaseMove, fastAdvance, capture, enPassant);
            }
        }
        return new Piece();
    }

}
