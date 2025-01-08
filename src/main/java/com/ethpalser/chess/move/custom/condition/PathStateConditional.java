package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.PathFactory;
import com.ethpalser.chess.space.PathOptions;

public class PathStateConditional implements Conditional {

    private final PathOptions pathOptions;
    private final Operator operator;
    private final Object expected;

    public PathStateConditional(PathOptions pathOptions, Operator operator, Object expected) {
        this.pathOptions = pathOptions;
        this.operator = operator;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(GameContext.Record context, Coordinate appliedTo) {
        PathFactory factory = new PathFactory(context, appliedTo);
        Path path = factory.create(this.pathOptions);
        return switch (this.operator) {
            case TRUE -> !path.isEmpty();
            case FALSE -> path.isEmpty();
            case EQUAL -> (this.expected == null && path.isEmpty())
                    || (this.expected instanceof Path && this.expected.equals(path));
            case NOT_EQUAL -> (this.expected == null && !path.isEmpty())
                    || (this.expected instanceof Path && !this.expected.equals(path));
        };
    }
}
