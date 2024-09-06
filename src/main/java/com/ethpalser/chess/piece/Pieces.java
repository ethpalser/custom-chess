package com.ethpalser.chess.piece;

import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.piece.standard.Bishop;
import com.ethpalser.chess.piece.standard.King;
import com.ethpalser.chess.piece.standard.Knight;
import com.ethpalser.chess.piece.standard.Pawn;
import com.ethpalser.chess.piece.standard.Queen;
import com.ethpalser.chess.piece.standard.Rook;
import com.ethpalser.chess.space.Point;

/**
 * Class of static functions related to pieces.
 */
public class Pieces {

    public static boolean isKing(Piece piece) {
        return piece != null && PieceType.KING.getCode().equals(piece.getCode());
    }

    public static boolean isAllied(Colour player, Piece piece) {
        if (player == null || piece == null) {
            throw new IllegalArgumentException("args cannot be null args:[player=" + player + ", piece=" + piece + "]");
        }
        return player.equals(piece.getColour());
    }

    public static boolean isOpponent(Colour player, Piece piece) {
        if (player == null || piece == null) {
            throw new IllegalArgumentException("args cannot be null args:[player=" + player + ", piece=" + piece + "]");
        }
        return !player.equals(piece.getColour());
    }

    public static String asString(Piece piece) {
        if (piece == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(piece.getColour().toCode());
        sb.append(piece.getCode());
        sb.append(piece.getPoint());
        if (piece.getHasMoved()) {
            sb.append("*");
        }
        return sb.toString();
    }

    public static Piece fromString(String string) {
        if (string == null) {
            return null;
        }
        PieceStringTokenizer pt = new PieceStringTokenizer(string);
        Colour colour = Colour.fromCode(pt.nextToken());
        String code = pt.nextToken();
        Point point = new Point(pt.nextToken() + pt.nextToken());
        boolean moved = "".equals(pt.nextToken());
        Piece piece;
        switch (PieceType.fromCode(code)) {
            case PAWN -> piece = new Pawn(colour, point, moved);
            case ROOK -> piece = new Rook(colour, point, moved);
            case KNIGHT -> piece = new Knight(colour, point, moved);
            case BISHOP -> piece = new Bishop(colour, point, moved);
            case QUEEN -> piece = new Queen(colour, point, moved);
            case KING -> piece = new King(colour, point, moved);
            case CUSTOM -> piece = new CustomPiece(code, colour, point, moved); // Skeleton of a custom piece
            default -> piece = null;
        }
        return piece;
    }

}
