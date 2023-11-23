package com.chess.api.model.movement.condition;

import com.chess.api.model.Board;
import com.chess.api.model.Coordinate;
import com.chess.api.model.piece.Piece;

public class Condition {

    private final Reference reference;
    private final Property<Piece> property;
    private final State state;
    private final Object value;

    public Condition() {
        this(new Reference(), new Property<>(null), null, null);
    }

    public Condition(Reference reference, Property<Piece> property, State state, Object value) {
        this.reference = reference;
        this.property = property;
        this.state = state;
        this.value = value;
    }

    public Boolean evaluate(Board board, Coordinate current, Coordinate next) {
        if (this.property == null) {
            return true;
        }

        Piece obj = this.getReferencePiece(board, current, next);
        Object propertyValue = this.property.fetch(obj);
        if (propertyValue == null || !propertyValue.getClass().equals(this.value.getClass())) {
            return false;
        }

        switch (state) {
            case TRUE -> {
                return this.value.equals(true);
            }
            case FALSE -> {
                return this.value.equals(false);
            }
            case EQUAL -> {
                return this.value.equals(obj);
            }
            case OPPOSITE -> {
                Piece currentPiece = board.getAt(current);
                Object currentValue = this.property.fetch(currentPiece);
                if(currentValue == null || !propertyValue.getClass().equals(currentValue.getClass())) {
                    return false;
                }
                return !this.value.equals(currentValue);
            }
            default -> {
                return false;
            }
        }
    }

    private Piece getReferencePiece(Board board, Coordinate current, Coordinate next) {
        return switch (reference.location()) {
            case LAST_MOVED -> board.getLastMoved();
            case AT_CURRENT -> board.getAt(current);
            case AT_DESTINATION -> board.getAt(next);
            case AT_COORDINATE -> board.getAt(reference.coordinate());
        };
    }
}

