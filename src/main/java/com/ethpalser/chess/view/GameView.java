package com.ethpalser.chess.view;

import com.ethpalser.chess.game.Game;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.MoveSpec;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.CustomPiece;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameView {

    private final int turn;
    private final BoardView board;
    private final Map<String, List<MoveView>> pieceSpecs;
    private final List<String> log;

    public GameView(Game game) {
        this.turn = game.getTurn();
        this.log = game.info().context().getLog().stream().map(LogEntry::toString).collect(Collectors.toList());
        this.board = new BoardView(
                game.info().context().getBoard(),
                8,
                8
        );
        this.pieceSpecs = new HashMap<>();
    }

    public int getTurn() {
        return turn;
    }

    public List<String> getLog() {
        return log;
    }

    public BoardView getBoard() {
        return board;
    }

    public Map<String, List<MoveView>> getPieceSpecs() {
        return pieceSpecs;
    }

}
