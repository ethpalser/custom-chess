package com.ethpalser.chess.game.log;

import com.ethpalser.chess.game.event.GameEvent;
import com.ethpalser.chess.game.event.GameEventProxy;
import com.ethpalser.chess.move.notation.ChessNotation;
import java.util.ArrayList;
import java.util.List;

public class ChessLog extends GameLog<ChessLog.Entry> {

    public ChessLog() {
        super();
    }

    public ChessLog(List<ChessNotation> storedLog) {
        super();
        for (ChessNotation notation : storedLog) {
            // A mutable list is used as this list can be appended to with new events for the same/modified notation
            List<GameEvent> list = new ArrayList<>(2);
            list.add(new GameEventProxy(notation));
            this.push(new Entry(notation, list));
        }
    }

    public record Entry(ChessNotation notation, List<GameEvent> eventList) {
        public Entry {
            if (notation == null || eventList == null || eventList.isEmpty()) {
                throw new IllegalArgumentException("ChessLog Entry has at least one null argument");
            }
        }
    }

}