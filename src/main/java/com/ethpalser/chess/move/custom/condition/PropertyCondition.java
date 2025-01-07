package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Reference;
import java.util.ArrayList;
import java.util.List;

public class PropertyCondition implements Conditional {

    private final Reference reference;
    private final PropertyType property;
    private final Operator operator;
    private final Object expected;

    public PropertyCondition(Reference reference, Operator operator) {
        this(reference, operator, null, null);
    }

    public PropertyCondition(Reference reference, Operator operator, PropertyType property, Object expected) {
        this.reference = reference;
        this.operator = operator;
        this.property = property;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(GameContext.Record context, Coordinate appliedTo) {
        if (this.operator == null) {
            return false;
        }
        if (this.reference == null) {
            return this.expected == null;
        }

        Board<Coordinate> board = context.getBoard();
        List<Piece> refList = new ArrayList<>();
        for (Coordinate c : this.reference.coordinates(context, appliedTo)) {
            // This condition requires pieces, and will verify the state of its properties
            Piece p = board.get(c);
            if (p != null) {
                refList.add(p);
            }
        }
        if (refList.isEmpty()) {
            return Operator.EQUAL.equals(this.operator) && this.expected == null;
        }

        Property<Piece> prop = this.property != null ? new Property<>(this.property.toString()) : null;
        boolean refExists = false;
        for (Piece ref : refList) {
            if (ref != null) {
                Object refProp = prop != null ? prop.fetch(ref) : null;
                if (!isExpectedState(refProp)) {
                    return false;
                }
                refExists = true;
            }
        }
        return refExists;
    }

    private boolean isExpectedState(Object objProperty) {
        switch (this.operator) {
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
                ", comparator=" + operator +
                ", expected=" + expected +
                '}';
    }

    @Override
    public ConditionalView toView() {
        return new ConditionalView(ConditionalType.FIELD, null, this.property, this.comparator, this.expected);
    }
}
