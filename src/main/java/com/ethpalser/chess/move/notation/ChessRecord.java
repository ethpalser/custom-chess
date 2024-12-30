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
        String promoteCode,
        ChessRecord followUpRecord) {

    private ChessRecord(Builder builder) {
        this(builder.source, builder.sourceColour, builder.sourceCode, builder.target, builder.targetColour,
                builder.targetCode, builder.promoteCode, builder.followUp);
    }

    public static class Builder {

        private Coordinate source;
        private Coordinate target;
        private Colour sourceColour;
        private Colour targetColour;
        private String sourceCode;
        private String targetCode;
        private String promoteCode;
        private ChessRecord followUp;

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

        public Builder sourcePiece(Piece piece) {
            this.sourceColour = piece.getColour();
            this.sourceCode = piece.getCode();
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

        public Builder targetPiece(Piece piece) {
            this.targetColour = piece.getColour();
            this.targetCode = piece.getCode();
            return this;
        }

        public Builder promoteCode(String code) {
            this.promoteCode = code;
            return this;
        }

        public Builder followingRecord(ChessRecord chessRecord) {
            this.followUp = chessRecord;
            return this;
        }

        public ChessRecord build() {
            return new ChessRecord(this);
        }

    }

}
