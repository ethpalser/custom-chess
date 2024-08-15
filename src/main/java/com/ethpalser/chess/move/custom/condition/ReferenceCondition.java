package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.reference.Reference;
import java.util.List;

public class ReferenceCondition<T> implements Conditional<T> {

    private final Reference<T> target;
    private final Comparator comparator;
    private final Reference<T> expected;

    public ReferenceCondition(Reference<T> target, Comparator comparator, Reference<T> expected) {
        this.target = target;
        this.comparator = comparator;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(Plane<T> plane) {
        List<T> tRefs = this.target.getReferences(plane);

        switch (this.comparator) {
            case FALSE, DOES_NOT_EXIST -> {
                return tRefs == null || tRefs.isEmpty();
            }
            case TRUE, EXIST -> {
                return tRefs != null && !tRefs.isEmpty();
            }
            case EQUAL -> {
                List<T> xRefs = this.expected.getReferences(plane);
                for (T ref : tRefs) {
                    if (!xRefs.contains(ref))
                        return false;
                }
                return true;
            }
            case NOT_EQUAL -> {
                List<T> xPieces = this.expected.getReferences(plane);
                for (T ref : tRefs) {
                    if (!xPieces.contains(ref))
                        return true;
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public String toString() {
        return "ReferenceCondition{" +
                "target=" + target +
                ", comparator=" + comparator +
                ", expected=" + expected +
                '}';
    }
}
