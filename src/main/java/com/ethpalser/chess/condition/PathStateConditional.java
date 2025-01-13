package com.ethpalser.chess.condition;

import com.ethpalser.chess.game.context.GameContext;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.move.config.PathFactory;
import com.ethpalser.chess.move.config.PathOptions;
import java.util.LinkedList;
import java.util.List;

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
        List<Piece> piecesOfPath;
        if (context == null || context.getBoard() == null) {
            piecesOfPath = null;
        } else {
            piecesOfPath = new LinkedList<>();
            for (Coordinate c : path) {
                Piece p = context.getBoard().get(c);
                if (p != null) {
                    piecesOfPath.add(p);
                }
            }
        }
        return switch (this.operator) {
            case TRUE -> piecesOfPath != null && !piecesOfPath.isEmpty();
            case FALSE -> piecesOfPath != null && piecesOfPath.isEmpty();
            case EQUAL -> (this.expected == null && path.isEmpty())
                    || (this.expected instanceof Path && this.expected.equals(path));
            case NOT_EQUAL -> (this.expected == null && !path.isEmpty())
                    || (this.expected instanceof Path && !this.expected.equals(path));
        };
    }
}
