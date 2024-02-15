package com.chess.api.game.condition;

import com.chess.api.game.Board;
import com.chess.api.game.movement.Action;
import com.chess.api.game.piece.Piece;
import com.chess.api.game.reference.Location;
import com.chess.api.game.reference.Reference;
import java.util.List;
import lombok.NonNull;

public class PropertyCondition implements Conditional {

    private final Reference reference;
    private final Property<Piece> property;
    private final Comparator comparator;
    private final Object expected;

    public PropertyCondition() {
        this(new Reference(), Comparator.EXIST, null, null);
    }

    public PropertyCondition(@NonNull Reference reference,
            Comparator comparator) {
        if (!Comparator.canReferenceSelf(comparator)) {
            throw new IllegalArgumentException("Cannot use a Comparator that requires an expected value.");
        }
        this.reference = reference;
        this.comparator = comparator;
        this.property = null;
        this.expected = null;
    }

    public PropertyCondition(@NonNull Reference reference, Comparator comparator, Property<Piece> property,
            Object expected) {
        this.reference = reference;
        this.comparator = comparator;
        this.property = property;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(Board board, Action action) {
        List<Piece> list = this.reference.getPieces(board, action);

        boolean hasPiece = false;
        for (Piece piece : list) {
            if (piece == null) {
                continue;
            }
            hasPiece = true;
            Object pieceProperty = this.property != null ? this.property.fetch(piece) : null;
            if (!isExpectedState(pieceProperty)) {
                return false;
            }
        }
        // Does not exist check failing Todo: fix it!
        return hasPiece || Comparator.DOES_NOT_EXIST.equals(comparator);
    }

    private boolean isExpectedState(Object objProperty) {
        switch (this.comparator) {
            case EXIST -> {
                return objProperty != null;
            }
            case DOES_NOT_EXIST -> {
                return objProperty == null;
            }
            case FALSE -> {
                return Boolean.FALSE.equals(objProperty);
            }
            case TRUE -> {
                return Boolean.TRUE.equals(objProperty);
            }
            case EQUAL -> {
                return (this.expected == null && objProperty == null) || (this.expected != null && objProperty != null
                        && objProperty.getClass().equals(this.expected.getClass()) && objProperty.equals(this.expected));
            }
            case NOT_EQUAL -> {
                return (this.expected == null && objProperty != null) || (objProperty != null
                        && !objProperty.equals(this.expected));
            }
        }
        return false;
    }

    public static Conditional startNotMoved() {
        Reference reference = new Reference(Location.START);
        return new PropertyCondition(reference, Comparator.FALSE, new Property<>("hasMoved"), false);
    }

    public static Conditional destinationEmpty() {
        Reference reference = new Reference(Location.DESTINATION);
        return new PropertyCondition(reference, Comparator.DOES_NOT_EXIST);
    }

    public static Conditional destinationNotEmpty() {
        Reference reference = new Reference(Location.DESTINATION);
        return new PropertyCondition(reference, Comparator.EXIST);
    }

    public static Conditional destinationColourNotEqual() {
        Reference reference = new Reference(Location.DESTINATION);
        return new PropertyCondition(reference, Comparator.NOT_EQUAL, new Property<>("colour"), null);
    }

    @Override
    public String toString() {
        return "PropertyCondition{" +
                "reference=" + reference +
                ", property=" + property +
                ", comparator=" + comparator +
                ", expected=" + expected +
                '}';
    }
}
