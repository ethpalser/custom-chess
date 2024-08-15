package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.reference.Reference;
import java.util.List;

public class PropertyCondition<T> implements Conditional<T> {

    private final Reference<T> reference;
    private final Property<T> property;
    private final Comparator comparator;
    private final Object expected;

    public PropertyCondition(Reference<T> reference, Comparator comparator) {
        this(reference, comparator, null, null);
    }

    public PropertyCondition(Reference<T> reference, Comparator comparator, Property<T> property, Object expected) {
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
    public boolean isExpected(Plane<T> plane) {
        List<T> refList = this.reference.getReferences(plane);

        boolean refExists = false;
        for (T ref : refList) {
            if (ref != null) {
                Object refProp = this.property != null ? this.property.fetch(ref) : null;
                if (!isExpectedState(refProp)) {
                    return false;
                }
                refExists = true;
            }
        }
        return refExists || Comparator.DOES_NOT_EXIST.equals(comparator);
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
