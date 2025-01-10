package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.move.config.Reference;
import java.util.ArrayList;
import java.util.List;

public class PieceStateConditional implements Conditional {

    private final Reference reference;
    private final PropertyType property;
    private final Operator operator;
    private final Object expected;

    public PieceStateConditional(Reference primary, Operator operator, PropertyType property, Object expected) {
        if (primary == null || operator == null) {
            throw new IllegalArgumentException("at least one argument is null of: primary reference or operator");
        }
        this.reference = primary;
        this.operator = operator;
        this.property = property;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(GameContext.Record context, Coordinate appliedTo) {
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
        return switch (this.operator) {
            case FALSE -> Boolean.FALSE.equals(objProperty);
            case TRUE -> Boolean.TRUE.equals(objProperty);
            case EQUAL -> (this.expected == null && objProperty == null)
                    || (this.expected != null && objProperty != null
                    && objProperty.getClass().equals(this.expected.getClass())
                    && objProperty.equals(this.expected));
            case NOT_EQUAL -> (this.expected == null && objProperty != null)
                    || (objProperty != null && !objProperty.equals(this.expected));
        };
    }
}
