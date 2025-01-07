package com.ethpalser.chess.view;

import com.ethpalser.chess.move.custom.condition.Operator;
import com.ethpalser.chess.move.custom.condition.PropertyType;
import com.ethpalser.chess.space.Reference;

public class ConditionalView {

    private final ReferenceView target;
    private final PropertyType field;
    private final Operator assertion;
    private final Object expected;

    public ConditionalView(Reference target, PropertyType field, Operator assertion,
            Object expected) {
        if (target == null) {
            this.target = null;
        } else {
            this.target = target.toView();
        }
        this.field = field;
        this.assertion = assertion;
        this.expected = expected;
    }

    public ReferenceView getTarget() {
        return target;
    }

    public PropertyType getField() {
        return field;
    }

    public Operator getAssertion() {
        return assertion;
    }

    public Object getExpected() {
        return expected;
    }
}
