package com.chess.api.model.movement.condition;

import com.chess.api.model.Board;
import com.chess.api.model.Coordinate;
import com.chess.api.model.piece.Piece;
import java.util.Iterator;
import java.util.List;
import lombok.Builder;

@Builder
public class Condition {

    private final Reference reference;
    private final Property<Piece> property;
    private final PropertyState propertyState;
    private final Object expected;
    private final Reference compare; // Todo: Make this less hacky and prone to error

    public Condition() {
        this(new Reference(), new Property<>(null), null, null);
    }

    public Condition(Reference reference, Property<Piece> property, PropertyState propertyState, Object expected) {
        this(reference, property, propertyState, expected, null);
    }

    public Condition(Reference reference, Property<Piece> property, PropertyState propertyState, Object expected,
            Reference compare) {
        this.reference = reference;
        this.property = property;
        this.propertyState = propertyState;
        this.expected = expected;
        this.compare = compare;
    }

    public Boolean evaluate(Board board, Coordinate start, Coordinate end) {
        List<Piece> list = board.getReferencePiece(this.reference, start, end);
        Iterator<Piece> iterator = list.iterator();
        boolean result = true;

        while (result && iterator.hasNext()) {
            Piece piece = iterator.next();
            if (PropertyState.DOES_NOT_EXIST.equals(this.propertyState) && piece != null) {
                return false;
            } else if (piece == null) {
                continue;
            }

            Object propVal = this.property != null ? this.property.fetch(piece) : null;
            switch (this.propertyState) {
                case TRUE -> result = Boolean.TRUE.equals(propVal);
                case FALSE -> result = Boolean.FALSE.equals(propVal);
                case EQUAL -> result = (this.expected == null && propVal == null) ||
                        (this.expected != null && propVal != null
                                && propVal.getClass().equals(this.expected.getClass())
                                && propVal.equals(this.expected))
                        || (this.compare != null && piece.equals(board.getReferencePiece(this.compare, start, end).get(0)));
                case OPPOSITE -> {
                    // The start piece is implied for OPPOSITE, as the condition's value is not known until runtime
                    Piece currPiece = board.getPiece(start);
                    Object currVal = this.property != null ? this.property.fetch(currPiece) : null;
                    result = (currVal == null && propVal == null) ||
                            currVal != null && propVal != null
                                    && propVal.getClass().equals(currVal.getClass())
                                    && propVal.equals(currVal);
                }
                default -> {
                    return false;
                }
            }
        }
        return result;
    }
}

