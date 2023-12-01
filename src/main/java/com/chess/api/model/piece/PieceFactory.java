package com.chess.api.model.piece;

import com.chess.api.model.Colour;
import com.chess.api.model.Vector2D;
import com.chess.api.model.movement.ExtraMovement;
import com.chess.api.model.movement.Movement;
import com.chess.api.model.movement.MovementType;
import com.chess.api.model.movement.Path;
import com.chess.api.model.movement.condition.Condition;
import com.chess.api.model.movement.condition.Direction;
import com.chess.api.model.movement.condition.Location;
import com.chess.api.model.movement.condition.Property;
import com.chess.api.model.movement.condition.PropertyState;
import com.chess.api.model.movement.condition.Reference;
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
        // Common and basic conditions
        Condition noPieceAtDestination = Condition.builder()
                .reference(new Reference(Location.DESTINATION))
                .propertyState(PropertyState.DOES_NOT_EXIST)
                .build();
        Condition hasPieceAtDestination = Condition.builder()
                .reference(new Reference(Location.DESTINATION))
                .propertyState(PropertyState.EXIST)
                .build();
        Condition oppositeColour = Condition.builder()
                .reference(new Reference(Location.DESTINATION))
                .property(new Property<>("colour"))
                .propertyState(PropertyState.OPPOSITE)
                .build();
        Condition selfNotMoved = Condition.builder()
                .reference(new Reference(Location.START))
                .property(new Property<>("hasMoved"))
                .propertyState(PropertyState.FALSE)
                .build();

        Path vertical = new Path(Vector2D.at(0, 1), Vector2D.at(0, Vector2D.MAX_Y));
        Path horizontal = new Path(Vector2D.at(1, 0), Vector2D.at(Vector2D.MAX_X, 0));
        Path diagonal = new Path(Vector2D.at(1, 1), Vector2D.at(Vector2D.MAX_X, Vector2D.MAX_Y));

        switch (type) {
            case KNIGHT -> {
                Movement knightBaseMove1 = new Movement(new Path(Vector2D.at(1, 2)), MovementType.JUMP, true, true);
                Movement knightBaseMove2 = new Movement(new Path(Vector2D.at(2, 1)), MovementType.JUMP, true, true);
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
                Movement kingBaseMoveV = new Movement(new Path(Vector2D.at(0, 1)), MovementType.ADVANCE, true, false);
                Movement kingBaseMoveH = new Movement(new Path(Vector2D.at(1, 0)), MovementType.ADVANCE, false, true);
                Movement kingBaseMoveD = new Movement(new Path(Vector2D.at(1, 1)), MovementType.ADVANCE, true, true);

                Condition castleKingSideCond2 = Condition.builder()
                        .reference(new Reference(Location.VECTOR, Vector2D.at(7, 0)))
                        .property(new Property<>("hasMoved"))
                        .propertyState(PropertyState.FALSE)
                        .build();
                Condition castleKingSideCond3 = Condition.builder()
                        .reference(new Reference(Location.PATH_TO_VECTOR, Vector2D.at(7, 0)))
                        .propertyState(PropertyState.DOES_NOT_EXIST)
                        .build();
                ExtraMovement kingSideRookMovement = new ExtraMovement(Vector2D.at(7, 0), Vector2D.at(5, 0));

                Movement castleKingSide = new Movement(new Path(Vector2D.at(2, 0)), MovementType.ADVANCE, false,
                        false, true, List.of(selfNotMoved, castleKingSideCond2, castleKingSideCond3),
                        kingSideRookMovement);

                Condition castleQueenSideCond2 = Condition.builder()
                        .reference(new Reference(Location.VECTOR, Vector2D.at(0, 0)))
                        .property(new Property<>("hasMoved"))
                        .propertyState(PropertyState.FALSE)
                        .build();
                Condition castleQueenSideCond3 = Condition.builder()
                        .reference(new Reference(Location.PATH_TO_VECTOR, Vector2D.at(0, 0)))
                        .propertyState(PropertyState.DOES_NOT_EXIST)
                        .build();
                ExtraMovement queenSideRookMovement = new ExtraMovement(Vector2D.at(0, 0), Vector2D.at(3, 0));

                Movement castleQueenSide = new Movement(new Path(Vector2D.at(2, 0)), MovementType.ADVANCE, false,
                        true, true, List.of(selfNotMoved, castleQueenSideCond2, castleQueenSideCond3),
                        queenSideRookMovement);

                return new Piece(PieceType.KING, colour, vector, kingBaseMoveV, kingBaseMoveH, kingBaseMoveD,
                        castleKingSide, castleQueenSide);
            }
            case PAWN -> {
                Movement pawnBaseMove = new Movement(new Path(Vector2D.at(0, 1)), MovementType.ADVANCE, false,
                        false, false, List.of(noPieceAtDestination));
                Movement fastAdvance = new Movement(new Path(Vector2D.at(0, 1), Vector2D.at(0, 2)),
                        MovementType.ADVANCE, false, false, true, List.of(selfNotMoved, noPieceAtDestination));

                Movement capture = new Movement(new Path(Vector2D.at(1, 1)), MovementType.ADVANCE, false, true, false,
                        List.of(oppositeColour, hasPieceAtDestination));

                Condition enPassantCond1 = Condition.builder()
                        .reference(new Reference(Location.LAST_MOVED))
                        .property(new Property<>("type"))
                        .propertyState(PropertyState.EQUAL)
                        .expected(PieceType.PAWN)
                        .build();
                Condition enPassantCond2 = Condition.builder()
                        .reference(new Reference(Location.LAST_MOVED))
                        .propertyState(PropertyState.EQUAL)
                        .compare(new Reference(Location.DESTINATION, Direction.BACK, null))
                        .build();
                Condition enPassantCond3 = Condition.builder()
                        .reference(new Reference(Location.LAST_MOVED))
                        .property(new Property<>("lastMoveDistance"))
                        .propertyState(PropertyState.EQUAL)
                        .expected(2)
                        .build();

                ExtraMovement extraMovement = new ExtraMovement(new Reference(Location.DESTINATION, Direction.BACK, null),
                        new Vector2D(), new Vector2D(), false);
                Movement enPassant = new Movement(new Path(Vector2D.at(1, 1)), MovementType.ADVANCE, false, true,
                        false, List.of(noPieceAtDestination, enPassantCond1, enPassantCond2, enPassantCond3), extraMovement);
                return new Piece(PieceType.PAWN, colour, vector, pawnBaseMove, fastAdvance, capture, enPassant);
            }
        }
        return new Piece();
    }

}
