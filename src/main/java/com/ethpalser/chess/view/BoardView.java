package com.ethpalser.chess.view;

import com.ethpalser.chess.piece.Piece;
import java.util.List;
import java.util.stream.Collectors;

public class BoardView {

    private final int width;
    private final int length;
    private final List<String> pieces;

    BoardView(List<Piece> pieces, int width, int length) {
        if (pieces == null) {
            this.pieces = List.of();
        } else {
            this.pieces = pieces.stream().map(Piece::toString).collect(Collectors.toList());
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
