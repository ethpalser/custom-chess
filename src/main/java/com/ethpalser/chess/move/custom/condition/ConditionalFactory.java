package com.ethpalser.chess.move.custom.condition;

import com.ethpalser.chess.space.PathOptions;

public class ConditionalFactory {

    private static ConditionalFactory factory;

    private ConditionalFactory() {
    }

    public static ConditionalFactory getInstance() {
        if (factory == null) {
            factory = new ConditionalFactory();
        }
        return factory;
    }

    public Conditional create(ConditionalOptions spec) {
        if (spec == null) {
            return null;
        }
        switch (spec.type()) {
            case PIECE -> {
                if (spec.optional() != null) {
                    return new PieceCompareConditional(spec.primary(), spec.operator(), spec.optional());
                }
                return new PieceStateConditional(spec.primary(), spec.operator(), spec.property(), spec.expected());
            }
            case LOG -> {
                if (spec.property() != null) {
                    return new GameHistoryConditional(spec.operator(), spec.property(), spec.expected());
                }
                return fail();
            }
            case BOARD -> {
                if (spec.optional() != null) {
                    return new PathStateConditional(new PathOptions(PathOptions.Type.CUSTOM, spec.primary(),
                            spec.optional()), spec.operator(), spec.expected());
                }
                return fail();
            }
            default -> {
                return fail();
            }
        }
    }

    private Conditional fail() {
        return (context, appliedTo) -> false;
    }

}
