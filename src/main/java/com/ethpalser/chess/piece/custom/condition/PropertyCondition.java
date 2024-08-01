package com.ethpalser.chess.piece.custom.condition;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.Action;
import com.ethpalser.chess.piece.custom.Piece;
import com.ethpalser.chess.piece.custom.reference.Reference;
import java.util.List;

public class PropertyCondition implements Conditional {

    private final Reference reference;
    private final Property<Piece> property;
    private final Comparator comparator;
    private final Object expected;

    public PropertyCondition(Reference reference, Comparator comparator) {
        this(reference, comparator, null, null);
    }

    public PropertyCondition(Reference reference, Comparator comparator, Property<Piece> property,
            Object expected) {
        if (reference == null || comparator == null) {
            throw new NullPointerException();
        }
        if (property == null && !Comparator.canReferenceSelf(comparator)) {
            throw new IllegalArgumentException("Cannot use a Comparator that requires an expected value.");
        }
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
            default -> {
                return false;
            }
        }
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
