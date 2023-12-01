package com.chess.api.game;

import com.chess.api.game.piece.Piece;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Game {

    private final Board board;
    private Colour turn;

    public Game() {
        this.board = new Board();
        this.turn = Colour.WHITE;
    }

    public void movePiece(@NonNull Vector2D position, @NonNull Vector2D destination) {
        Piece piece = board.getPiece(position);
        if (piece == null || !turn.equals(piece.getColour())) {
            return;
        }
        this.board.movePiece(position, destination);
        this.turn = turn.equals(Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
    }

}
