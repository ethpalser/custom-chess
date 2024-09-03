package com.ethpalser.chess.game.view;

import com.ethpalser.chess.move.custom.condition.Comparator;
import com.ethpalser.chess.move.custom.condition.ConditionalType;
import com.ethpalser.chess.move.custom.condition.PropertyType;
import com.ethpalser.chess.space.custom.reference.Reference;

public class ConditionalView {

    private final ConditionalType type;
    private final ReferenceView target;
    private final PropertyType field;
    private final Comparator assertion;
    private final Object expected;

    public ConditionalView(ConditionalType type, Reference<?> target, PropertyType field, Comparator assertion,
            Object expected) {
        this.type = type;
        if (target == null) {
            this.target = null;
        } else {
            this.target = target.toView();
        }
        this.field = field;
        this.assertion = assertion;
        this.expected = expected;
    }

    public ConditionalType getType() {
        return type;
    }

    public ReferenceView getTarget() {
        return target;
    }

    public PropertyType getField() {
        return field;
    }

    public Comparator getAssertion() {
        return assertion;
    }

    public Object getExpected() {
        return expected;
    }
}
