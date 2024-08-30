package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Positional;

public class LogCondition<T extends Positional> implements Conditional<T> {

    private final Log<Point, T> log;
    private final Comparator comparator;
    private final PropertyType propType;
    private final Object expected;

    public LogCondition(Log<Point, T> log, Comparator comparator, PropertyType propType, Object expected) {
        this.log = log;
        this.comparator = comparator;
        this.propType = propType;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(Plane<T> plane) {
        if (this.log.peek() == null) {
            return false;
        }
        switch (this.propType) {
            case HAS_MOVED -> {
                return switch (this.comparator) {
                    case TRUE -> this.log.peek().isFirstOccurrence();
                    case FALSE -> !this.log.peek().isFirstOccurrence();
                    default -> false;
                };
            }
            case DISTANCE_MOVED -> {
                Point start = this.log.peek().getStart();
                Point end = this.log.peek().getEnd();
                int diff;
                if (start == null || end == null) {
                    diff = 0;
                }
                else {
                    diff = Math.max(
                            Math.abs(start.getX() - end.getX()),
                            Math.abs(start.getY() - end.getY())
                    );
                }

                return switch (this.comparator) {
                    case EQUAL -> expected.equals(diff);
                    case NOT_EQUAL -> !expected.equals(diff);
                    default -> false;
                };
            }
            default -> {
                return false;
            }
        }
    }
}
