package com.ethpalser.chess.view;

import com.ethpalser.chess.space.custom.reference.Reference;

public class ActionView {

    private final ReferenceView target;
    private final ReferenceView destination;

    public ActionView(Reference<?> target, Reference<?> destination) {
        if (target == null) {
            this.target = null;
        } else {
            this.target = target.toView();
        }
        if (destination == null) {
            this.destination = null;
        } else {
            this.destination = destination.toView();
        }
    }

    public ReferenceView getTarget() {
        return target;
    }

    public ReferenceView getDestination() {
        return destination;
    }
}
