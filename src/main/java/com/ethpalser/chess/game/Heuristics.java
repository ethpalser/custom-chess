package com.ethpalser.chess.game;

import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Heuristics {

    private Heuristics(){}

    public static int pieceValue(GameContext context, Piece piece) {
        if (piece == null) {
            return 0;
        }
        int value;
        switch (PieceType.fromCode(piece.getCode())) {
            case PAWN -> value = 1;
            case BISHOP, KNIGHT -> value = 3;
            case ROOK -> value = 5;
            case QUEEN -> value = 9;
            case CUSTOM -> {
                // Currently, this uses MoveSet, but this would be more accurate to use its blueprint
                MoveSet moveSet = piece.getMoves(
                        context.getBoard(),
                        context.getLog(),
                        context.getThreats(Colour.opposite(piece.getColour()))
                );
                int numMoves = moveSet.getPoints().size();
                int base = (int) Math.ceil(numMoves / 3.0);
                value = base + base / 3;
            }
            default -> value = 0;
        }
        return value;
    }

    public static int pawnWall(List<Point> pawnThreats, List<Piece> pawns) {
        int sum = 0;
        // Pawn defends
        for (Piece piece : pawns) {
            for (Point p : pawnThreats) {
                // This is a pawn that is defended by at least one other pawn. Doubled-up defends count for one each.
                if (PieceType.PAWN.getCode().equals(piece.getCode()) && piece.getCoordinate().equals(p)) {
                    sum++; // Currently, an arbitrarily set amount
                }
            }
        }
        return sum;
    }

    public static int pawnCenterControl(List<Point> pawnThreats, int midX, int midY) {
        int midX2;
        int midY2;
        if (midX % 2 == 0) {
            midX2 = midX - 1;
        } else {
            midX2 = midX;
        }
        if (midY % 2 == 0) {
            midY2 = midY - 1;
        } else {
            midY2 = midY;
        }

        Point midPoint1 = new Point(midX, midY);
        Point midPoint2 = new Point(midX, midY2);
        Point midPoint3 = new Point(midX2, midY);
        Point midPoint4 = new Point(midX2, midY2);
        int sum = 0;
        for (Point p : pawnThreats) {
            // A pawn has threat over a centre position on the board, which is often valuable
            if (p.equals(midPoint1) || p.equals(midPoint2) || p.equals(midPoint3) || p.equals(midPoint4)) {
                sum++;  // Currently, an arbitrarily set amount
            }
        }
        return sum;
    }

    public static int doubleFilePawns(List<Piece> pawns) {
        Set<Integer> seen = new HashSet<>();
        int sum = 0;
        for (Piece p : pawns) {
            if (seen.contains(p.getCoordinate().getValue(1))) {
                sum -= 1;
            }
            seen.add(p.getCoordinate().getValue(1));
        }
        return sum;
    }

}
