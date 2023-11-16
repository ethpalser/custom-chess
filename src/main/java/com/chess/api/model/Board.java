package com.chess.api.model;

import com.chess.api.model.piece.Bishop;
import com.chess.api.model.piece.King;
import com.chess.api.model.piece.Knight;
import com.chess.api.model.piece.Pawn;
import com.chess.api.model.piece.Piece;
import com.chess.api.model.piece.Queen;
import com.chess.api.model.piece.Rook;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Board {

    private List<Piece> pieces;
    private Colour[][] colours;

    public Board() {
        List<Piece> pieceList = new ArrayList<>();
        Colour[][] occupiedList = new Colour[8][8];

        int y = 0;
        for (int x = 0; x < 8; x++) {
            pieceList.add(switch (x) {
                case 0, 7 -> new Rook(Colour.WHITE, new Coordinate(x, y));
                case 1, 6 -> new Knight(Colour.WHITE, new Coordinate(x, y));
                case 2, 5 -> new Bishop(Colour.WHITE, new Coordinate(x, y));
                case 3 -> new Queen(Colour.WHITE, new Coordinate(x, y));
                case 4 -> new King(Colour.WHITE, new Coordinate(x, y));
                default -> throw new IllegalStateException("Unexpected value: " + x);
            });
            occupiedList[x][y] = Colour.WHITE;
        }

        y = 1;
        for (int x = 0; x < 8; x++) {
            pieceList.add(new Pawn(Colour.WHITE, new Coordinate(x, y)));
            occupiedList[x][y] = Colour.WHITE;
        }

        y = 6;
        for (int x = 0; x < 8; x++) {
            pieceList.add(new Pawn(Colour.BLACK, new Coordinate(x, y)));
            occupiedList[x][y] = Colour.BLACK;
        }

        y = 7;
        for (int x = 0; x < 8; x++) {
            pieceList.add(switch (x) {
                case 0, 7 -> new Rook(Colour.BLACK, new Coordinate(x, y));
                case 1, 6 -> new Knight(Colour.BLACK, new Coordinate(x, y));
                case 2, 5 -> new Bishop(Colour.BLACK, new Coordinate(x, y));
                case 3 -> new Queen(Colour.BLACK, new Coordinate(x, y));
                case 4 -> new King(Colour.BLACK, new Coordinate(x, y));
                default -> throw new IllegalStateException("Unexpected value: " + x);
            });
            occupiedList[x][y] = Colour.BLACK;
        }
        this.pieces = pieceList;
        this.colours = occupiedList;
    }

}
