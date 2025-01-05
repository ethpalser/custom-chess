package com.ethpalser.chess.move.notation;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.PieceRecord;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;

public class VerboseNotationFormat implements ChessNotationFormat {

    private final Board<Coordinate> board;

    public VerboseNotationFormat(Board<Coordinate> board) {
        this.board = board;
    }

    @Override
    public String format(ChessRecord chessRecord) {
        if (chessRecord == null) {
            throw new IllegalArgumentException("Cannot format a null chess record");
        }
        StringBuilder sb = new StringBuilder();

        if (chessRecord.source() != null) {
            if (chessRecord.sourceColour() != null && chessRecord.sourceCode() != null) {
                sb.append(chessRecord.sourceColour().toCode());
                sb.append(this.pieceCodeString(chessRecord.sourceCode()));
            } else {
                throw new IllegalStateException("Chess record is missing the moving piece's colour and code");
            }
            sb.append(this.coordinateString(chessRecord.source()));
        }

        if (chessRecord.target() != null) {
            if (chessRecord.targetColour() != null && chessRecord.targetCode() != null) {
                sb.append("*");
                sb.append(chessRecord.targetColour().toCode());
                sb.append(this.pieceCodeString(chessRecord.targetCode()));
            } else {
                sb.append(" ");
            }
            sb.append(this.coordinateString(chessRecord.target()));
        }

        if (chessRecord.promoteCode() != null) {
            sb.append("=");
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

        String[] components = chessNotation.split("[*=\\w]");
        if (components.length == 1) {
            String part = components[0];
            // expecting a colour before the notation's alias, as to identify the turn player
            Colour colour = Colour.fromCode(part.substring(0, 1));
            ChessNotationAlias alias = ChessNotationAlias.fromString(part.substring(1));
            return this.parseAlias(colour, alias);
        }

        ChessRecord.Builder builder = new ChessRecord.Builder();
        int partNum = 0;
        for (String part : components) {
            // Assume the longest has: source, target, promotion; and not all parts are needed but always this order
            PieceRecord pieceRecord = PieceRecord.fromString(part);
            if (partNum == 0) {
                builder.sourceCoordinate(pieceRecord.coordinate());
                builder.sourceColour(pieceRecord.colour());
                builder.sourceCode(pieceRecord.code());
            }
            if (partNum == 1) {
                builder.targetCoordinate(pieceRecord.coordinate());
                builder.targetColour(pieceRecord.colour());
                builder.targetCode(pieceRecord.code());
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
        return "" + ('a' + coordinate.getValue(1)) + coordinate.getValue(2);
    }

    private ChessRecord parseAlias(Colour colour, ChessNotationAlias alias) {
        int minX = this.board.space().min(0);
        int maxX = this.board.space().max(0);
        int y = Colour.WHITE.equals(colour) ? this.board.space().min(1) : this.board.space().max(1);
        switch (alias) {
            case QUEEN_SIDE_CASTLE -> {
                ChessRecord.Builder builder = new ChessRecord.Builder();
                // Assumes the king starts at 'e0' or 'e8' (8 is the max y value for a standard board)
                builder.sourceCoordinate(new Point(4, y))
                        .sourceColour(Colour.WHITE)
                        .sourceCode(PieceType.KING.toCode())
                        .targetCoordinate(new Point(2, y))
                        .followingRecord(
                                (new ChessRecord.Builder())
                                        .sourceCoordinate(new Point(minX, y))
                                        .sourceColour(Colour.WHITE)
                                        .sourceCode(PieceType.ROOK.toCode())
                                        .targetCoordinate(new Point(3, y))
                                        .build()
                        );
                return builder.build();
            }
            case KING_SIDE_CASTLE -> {
                ChessRecord.Builder builder = new ChessRecord.Builder();
                // Assumes the king starts at 'e0' or 'e8' (8 is the max y value for a standard board)
                builder.sourceCoordinate(new Point(4, y))
                        .sourceColour(Colour.WHITE)
                        .sourceCode(PieceType.KING.toCode())
                        .targetCoordinate(new Point(6, y))
                        .followingRecord(
                                (new ChessRecord.Builder())
                                        .sourceCoordinate(new Point(maxX, y))
                                        .sourceColour(Colour.WHITE)
                                        .sourceCode(PieceType.ROOK.toCode())
                                        .targetCoordinate(new Point(5, y))
                                        .build()
                        );
                return builder.build();
            }
            default -> {
                return null;
            }
        }
    }
}
