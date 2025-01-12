package com.ethpalser.chess.game.log;

import com.ethpalser.chess.game.event.GameEvent;
import com.ethpalser.chess.game.event.GameEventProxy;
import com.ethpalser.chess.move.notation.ChessNotation;

public class ChessLog extends GameLog<ChessLog.Entry> {

    public ChessLog() {
        super();
    }

    public ChessLog(String[] entryList) {
        super();
        for (String entry : entryList) {
            ChessNotation notation = new ChessNotation(entry);
            GameEvent event = new GameEventProxy(notation);
            this.push(new Entry(notation, event));
        }
    }

    public record Entry(ChessNotation notation, GameEvent eventList) {
        public Entry {
            if (notation == null || eventList == null) {
                throw new IllegalArgumentException("ChessLog Entry has at least one null argument");
            }
        }
    }

}