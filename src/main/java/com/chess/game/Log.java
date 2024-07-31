package com.chess.game;

import com.chess.game.movement.ActionRecord;
import com.chess.game.piece.Piece;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Log {

    private final Deque<ActionRecord> log;

    public Log() {
        this.log = new ArrayDeque<>();
    }

    public Log(Space2D<Piece> board, List<String> logStrings) {
        this();
        for (String s : logStrings) {
            ActionRecord rec = new ActionRecord(board, s);
            this.log.push(rec);
            // Manually move the piece on the board, ignoring all checks
            board.put(rec.getAction().getEnd(), board.remove(rec.getAction().getStart()));
        }
    }

    // push

    // peek

    // pop

    // undo

    // redo

    // restore

}
