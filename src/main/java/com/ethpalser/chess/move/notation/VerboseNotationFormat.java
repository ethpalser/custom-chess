package com.ethpalser.chess.move.notation;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Space;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-30",
        majorVersion = 1,
        minorVersion = 5,
        lastModified = "2025-01-13"
)
public class VerboseNotationFormat implements ChessNotationFormat {

    private static final char CAPTURED_CHAR = '*';
    private static final char FOLLOW_UP_CHAR = '>';
    private static final char PROMOTE_CHAR = '=';

    public VerboseNotationFormat() {
        // No dependencies
    }

    @Override
    public String format(ChessRecord chessRecord) {
        if (chessRecord == null) {
            throw new IllegalArgumentException("Cannot format a null chess record");
        }
        StringBuilder sb = new StringBuilder();

        // Apply symbol indicating a follow-up first
        if (chessRecord.isFollowUp()) {
            sb.append(FOLLOW_UP_CHAR);
        }
        // Add the moving piece's code, colour and location
        if (chessRecord.source() != null) {
            if (chessRecord.sourceColour() != null && chessRecord.sourceCode() != null) {
                sb.append(chessRecord.sourceColour().toCode());
                sb.append(this.pieceCodeString(chessRecord.sourceCode()));
            } else {
                throw new IllegalStateException("Chess record is missing the moving piece's colour and code");
            }
            sb.append(this.coordinateString(chessRecord.source()));
        }
        // Then add the target location and the captured piece's info, if there was a piece captured
        if (chessRecord.target() != null) {
            if (chessRecord.targetColour() != null && chessRecord.targetCode() != null) {
                sb.append(CAPTURED_CHAR);
                sb.append(chessRecord.targetColour().toCode());
                sb.append(this.pieceCodeString(chessRecord.targetCode()));
            } else {
                sb.append(" ");
            }
            sb.append(this.coordinateString(chessRecord.target()));
        }

        if (chessRecord.promoteCode() != null) {
            sb.append(PROMOTE_CHAR);
            sb.append(this.pieceCodeString(chessRecord.promoteCode()));
        }

        return sb.toString();
    }

    @Override
    public String format(ChessRecord chessRecord, ChessNotationAlias alias) {
        if (chessRecord == null) {
            throw new IllegalArgumentException("Cannot format a null chess record");
        }
        if (alias == null) {
            return this.format(chessRecord);
        }
        // Colour must be provided for this format to handle correctly
        return chessRecord.sourceColour() + alias.toString();
    }

    @Override
    public ChessRecord parse(String chessNotation) {
        if (chessNotation == null) {
            return null;
        }
        boolean isFollowUp = chessNotation.charAt(0) == FOLLOW_UP_CHAR;
        if (isFollowUp) {
            chessNotation = chessNotation.substring(1); // Remove the followup indicator
        }
        String[] components = chessNotation.split("[\\w" + CAPTURED_CHAR + PROMOTE_CHAR + "]");

        ChessRecord.Builder builder = new ChessRecord.Builder()
                .isFollowUp(isFollowUp);
        int partNum = 0;
        for (String part : components) {
            // Assume the longest has: source, target, promotion; and not all parts are needed but always this order
            PieceRecord pieceRecord = PieceRecord.fromString(part);
            if (partNum == 0) {
                builder.sourceCoordinate(pieceRecord.coordinate())
                        .sourceColour(pieceRecord.colour())
                        .sourceCode(pieceRecord.code());
            }
            if (partNum == 1) {
                builder.targetCoordinate(pieceRecord.coordinate())
                        .targetColour(pieceRecord.colour())
                        .targetCode(pieceRecord.code());
            }
            if (partNum == 2) {
                builder.promoteCode(pieceRecord.code());
            }
            partNum++;
        }
        return builder.build();
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
        return "" + (char) ('a' + coordinate.getValue(Space.AXIS.X)) + (1 + coordinate.getValue(Space.AXIS.Y));
    }
}
