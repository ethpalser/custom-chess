package com.ethpalser.chess.game.logic;

import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.PieceType;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Space;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Heuristics {

    private Heuristics() {
    }

    public static int pieceValue(GameContext.Record context, Piece piece) {
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
                MoveSet moveSet = piece.getMoves(context);
                int numMoves = moveSet.coordinates().size();
                int base = (int) Math.ceil(numMoves / 3.0);
                value = base + base / 3;
            }
            default -> value = 0;
        }
        return value;
    }

    public static int pawnValue(GameContext.Record context, Colour colour) {
        List<Piece> pawns = new ArrayList<>();
        List<Coordinate> pawnAttacks = new ArrayList<>();
        List<Coordinate> pawnDefends = new ArrayList<>();
        for (Piece p : context.getBoard()) {
            if (PieceType.PAWN.toCode().equals(p.getCode()) && colour.equals(p.getColour())) {
                pawns.add(p);
                MoveSet moveSet = p.getMoves(context);
                pawnAttacks.addAll(moveSet.attacks());
                pawnDefends.addAll(moveSet.defends());
            }
        }
        Space space = context.getBoard().space();
        int midX = (space.max(Space.AXIS.X) + space.min(Space.AXIS.X)) / 2;
        int midY = (space.max(Space.AXIS.Y) + space.min(Space.AXIS.Y)) / 2;
        int pawnCenter = pawnCenterControl(pawnAttacks, midX, midY);
        int pawnWall = pawnWall(pawnDefends, pawns);
        int pawnDoubled = doubleFilePawns(pawns);
        return pawnCenter + pawnWall + pawnDoubled;
    }

    private static int pawnWall(List<Coordinate> pawnThreats, List<Piece> pawns) {
        int sum = 0;
        // Pawn defends
        for (Piece piece : pawns) {
            for (Coordinate p : pawnThreats) {
                // This is a pawn that is defended by at least one other pawn. Doubled-up defends count for one each.
                if (PieceType.PAWN.toCode().equals(piece.getCode()) && piece.getCoordinate().equals(p)) {
                    sum++; // Currently, an arbitrarily set amount
                }
            }
        }
        return sum;
    }

    private static int pawnCenterControl(List<Coordinate> pawnThreats, int midX, int midY) {
        int midX2 = midX % 2 != 0 ? midX + 1 : midX;
        int midY2 = midY % 2 != 0 ? midY + 1 : midY;
        Coordinate midPoint1 = new Point(midX, midY);
        Coordinate midPoint2 = new Point(midX, midY2);
        Coordinate midPoint3 = new Point(midX2, midY);
        Coordinate midPoint4 = new Point(midX2, midY2);
        int sum = 0;
        for (Coordinate p : pawnThreats) {
            // A pawn has threat over a centre position on the board, which is often valuable
            if (p.equals(midPoint1) || p.equals(midPoint2) || p.equals(midPoint3) || p.equals(midPoint4)) {
                sum++;  // Currently, an arbitrarily set amount
            }
        }
        return sum;
    }

    private static int doubleFilePawns(List<Piece> pawns) {
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
