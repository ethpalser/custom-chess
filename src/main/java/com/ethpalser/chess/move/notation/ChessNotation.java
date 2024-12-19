package com.ethpalser.chess.move.notation;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;

public class ChessNotation {

    public static final String QUEEN_SIDE_CASTLE_ALIAS = "O-O-O";
    public static final String KING_SIDE_CASTLE_ALIAS = "O-O";
    public static final String EN_PASSANT_SPECIAL_CASE = "e.p.";

    // Store the expected notation that must be built
    private final String string;

    /**
     * Create a ChessNotation string using an action's alias. This alias should be well known, or it will not
     * be understood by players. For example, the King's Castle actions have aliases O-O-O or O-O for queen-side
     * castle and king-side castle respectively.
     *
     * @param alias String of a specific, unique chess notation
     */
    public ChessNotation(String alias) {
        this.string = alias;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public static ChessNotationBuilder Builder(Board<Coordinate> board) {
        return new ChessNotationBuilder(board);
    }

    private static class ChessNotationBuilder {
        private final Board<Coordinate> board;

        private ChessNotationFormat format;
        private Coordinate source;
        private Coordinate target;
        private String promoteCode;
        private String specialCase;

        private ChessNotationBuilder(Board<Coordinate> board) {
            this.format = ChessNotationFormat.VERBOSE;
            this.board = board;
        }

        public ChessNotationBuilder format(ChessNotationFormat format) {
            this.format = format;
            return this;
        }

        public ChessNotationBuilder source(Coordinate coordinate) {
            this.source = coordinate;
            return this;
        }

        public ChessNotationBuilder target(Coordinate coordinate) {
            this.target = coordinate;
            return this;
        }

        public ChessNotationBuilder promoteTo(String code) {
            this.promoteCode = code;
            return this;
        }

        public ChessNotationBuilder bySpecialCase(String conditionIdentifier) {
            this.specialCase = conditionIdentifier;
            return this;
        }

        public ChessNotation build() {
           String notation = switch (this.format) {
               case VERBOSE -> this.verboseNotation();
               case COORDINATE -> "";
           };
           return new ChessNotation(notation);
        }

        // region PRIVATE METHODS

        // Example: wPe7*bRf8=Q
        private String verboseNotation() {
            StringBuilder sb = new StringBuilder();

            if (this.source != null) {
                Piece sourcePiece = this.board.get(this.source);
                if (sourcePiece != null) {
                    sb.append(sourcePiece.getColour().toCode());
                    sb.append(this.pieceCodeString(sourcePiece.getCode()));
                } else {
                    sb.append("_"); // This case should not happen in normal games
                }
                sb.append(this.coordinateString(this.source));
            }

            if (this.target != null) {
                Piece targetPiece = this.board.get(this.target);
                if (targetPiece != null) {
                    sb.append("*");
                    sb.append(targetPiece.getColour().toCode());
                    sb.append(this.pieceCodeString(targetPiece.getCode()));
                } else {
                    sb.append(" ");
                }
                sb.append(this.coordinateString(this.target));
            }

            if (this.promoteCode != null) {
                sb.append("=");
                sb.append(this.pieceCodeString(this.promoteCode));
            }
            // Extended on the notation, movement may indicate it had a special condition or non-standard
            if (this.specialCase != null) {
                sb.append(" ").append(this.specialCase);
            }

            return sb.toString();
        }

        private String pieceCodeString(String code) {
            if (PieceType.CUSTOM.equals(PieceType.fromCode(code))) {
                return "(" + code + ")";
            } else {
                return code;
            }
        }

        private String coordinateString(Coordinate coordinate) {
            // Currently, limited to a 2-dimensional coordinate from at-largest a 26 x 26 space
            return "" + ('a' + coordinate.getValue(1)) + coordinate.getValue(2);
        }

        // endregion

    }


}
