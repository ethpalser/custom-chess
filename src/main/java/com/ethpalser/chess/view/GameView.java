package com.ethpalser.chess.view;

import com.ethpalser.chess.game.Game;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.move.custom.CustomMove;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.CustomPiece;
import java.util.ArrayList;
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
        this.log = game.getLog().stream().map(LogEntry::toString).collect(Collectors.toList());
        this.board = new BoardView(
                new ArrayList<>(game.getBoard().getPieces().values()),
                game.getBoard().getPieces().width(),
                game.getBoard().getPieces().length()
        );
        Map<String, List<MoveView>> specList = new HashMap<>();
        for (Piece p : game.getBoard().getPieces()) {
            if (p instanceof CustomPiece && !"PRNBQK".contains(p.getCode()) && specList.get(p.getCode()) == null) {
                specList.put(
                        p.getCode(),
                        ((CustomPiece) p).getMoveSpecs().stream().map(CustomMove::toView).collect(Collectors.toList())
                );
            }
        }
        this.pieceSpecs = specList;
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
