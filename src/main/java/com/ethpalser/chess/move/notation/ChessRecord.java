package com.ethpalser.chess.move.notation;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;

public record ChessRecord(
        Coordinate source,
        Colour sourceColour,
        String sourceCode,
        Coordinate target,
        Colour targetColour,
        String targetCode,
        boolean sourceHasMoved,
        boolean targetHasMoved,
        String promoteCode,
        boolean isFollowUp) {

    private ChessRecord(Builder builder) {
        this(builder.source, builder.sourceColour, builder.sourceCode, builder.target, builder.targetColour,
                builder.targetCode, builder.sourceHasMoved, builder.targetHasMoved, builder.promoteCode,
                builder.isFollowUp);
    }

    public static class Builder {

        private Coordinate source;
        private Coordinate target;
        private Colour sourceColour;
        private Colour targetColour;
        private String sourceCode;
        private String targetCode;
        private boolean sourceHasMoved; // This depends on the record being created before the movement
        private boolean targetHasMoved;
        private String promoteCode;
        private boolean isFollowUp;

        /**
         * Create a blank ChessRecord builder. Not recommended for general use.
         */
        public Builder() {
            this(null, null, null, null);
        }

        public Builder(Coordinate source, Coordinate target, Piece moving, Piece captured) {
            this.source = source;
            this.target = target;
            if (moving != null) {
                this.sourceColour = moving.getColour();
                this.sourceCode = moving.getCode();
                this.sourceHasMoved = moving.getHasMoved();
            } else {
                this.sourceColour = null;
                this.sourceCode = null;
                this.sourceHasMoved = false;
            }
            if (captured != null) {
                this.targetColour = captured.getColour();
                this.targetCode = captured.getCode();
                this.targetHasMoved = captured.getHasMoved();
            } else {
                this.targetColour = null;
                this.targetCode = null;
                this.targetHasMoved = false;
            }
            this.promoteCode = null;
            this.isFollowUp = false;
        }

        public Builder original(ChessRecord rec) {
            this.source = rec.source;
            this.target = rec.target;
            this.sourceColour = rec.sourceColour;
            this.targetColour = rec.targetColour;
            this.sourceCode = rec.sourceCode;
            this.targetCode = rec.targetCode;
            this.promoteCode = rec.promoteCode;
            this.isFollowUp = rec.isFollowUp;
            return this;
        }

        public Builder sourceCoordinate(Coordinate coordinate) {
            this.source = coordinate;
            return this;
        }

        public Builder sourceColour(Colour colour) {
            this.sourceColour = colour;
            return this;
        }

        public Builder sourceCode(String code) {
            this.sourceCode = code;
            return this;
        }

        public Builder targetCoordinate(Coordinate coordinate) {
            this.target = coordinate;
            return this;
        }

        public Builder targetColour(Colour colour) {
            this.targetColour = colour;
            return this;
        }

        public Builder targetCode(String code) {
            this.targetCode = code;
            return this;
        }

        public Builder promoteCode(String code) {
            this.promoteCode = code;
            return this;
        }

        public Builder isFollowUp(boolean isFollowUp) {
            this.isFollowUp = isFollowUp;
            return this;
        }

        public ChessRecord build() {
            return new ChessRecord(this);
        }

    }

}
