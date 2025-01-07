package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Reference;
import java.util.ArrayList;
import java.util.List;

public class ReferenceCondition implements Conditional {

    private final Reference target;
    private final Operator operator;
    private final Reference expected;

    public ReferenceCondition(Reference target, Operator operator, Reference expected) {
        this.target = target;
        this.operator = operator;
        this.expected = expected;
    }

    @Override
    public boolean isExpected(GameContext.Record context, Coordinate appliedTo) {
        if (operator == null) {
            return false;
        }
        if (target == null) {
            return expected == null;
        }

        List<Piece> tRefs = this.getReferences(this.target, context, appliedTo);
        switch (this.operator) {
            case FALSE -> {
                return tRefs.isEmpty();
            }
            case TRUE -> {
                return !tRefs.isEmpty();
            }
            case EQUAL -> {
                if (this.expected == null) {
                    return tRefs.isEmpty();
                }

                List<Piece> xRefs = this.getReferences(this.expected, context, appliedTo);
                for (Piece ref : tRefs) {
                    if (!xRefs.contains(ref))
                        return false;
                }
                return true;
            }
            case NOT_EQUAL -> {
                if (this.expected == null) {
                    return !tRefs.isEmpty();
                }

                List<Piece> xRefs = this.getReferences(this.expected, context, appliedTo);
                for (Piece ref : tRefs) {
                    if (!xRefs.contains(ref))
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
                ", comparator=" + operator +
                ", expected=" + expected +
                '}';
    }

    @Override
    public ConditionalView toView() {
        return new ConditionalView(ConditionalType.PIECE, this.target, null, this.comparator, this.expected);
    }

    private List<Piece> getReferences(Reference reference, GameContext.Record context, Coordinate appliedTo) {
        Board<Coordinate> board = context.getBoard();
        List<Piece> refs = new ArrayList<>();
        for (Coordinate c : reference.coordinates(context, appliedTo)) {
            Piece p = board.get(c);
            if (p != null) {
                refs.add(p);
            }
        }
        return refs;
    }
}
