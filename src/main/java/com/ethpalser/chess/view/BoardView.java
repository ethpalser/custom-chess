package com.ethpalser.chess.view;

import com.ethpalser.chess.piece.Piece;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoardView {

    private final int width;
    private final int length;
    private final List<String> pieces;

    BoardView(Iterable<Piece> pieces, int width, int length) {
        if (pieces == null) {
            this.pieces = List.of();
        } else {
            List<String> pStrings = new ArrayList<>();
            for (Piece p : pieces) {
                pStrings.add(p.toString());
            }
            this.pieces = pStrings;
        }
        this.width = width;
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public List<String> getPieces() {
        return pieces;
    }
}
