package com.ethpalser.chess.game.log;

import com.ethpalser.chess.game.event.GameEvent;
import com.ethpalser.chess.game.event.GameEventProxy;
import com.ethpalser.chess.move.notation.ChessNotation;
import java.util.Objects;

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

    public record Entry(ChessNotation notation, GameEvent event) {
        public Entry {
            if (notation == null || event == null) {
                throw new IllegalArgumentException("ChessLog Entry has at least one null argument");
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return Objects.equals(notation, entry.notation) && Objects.equals(event, entry.event);
        }

        @Override
        public int hashCode() {
            return Objects.hash(notation, event);
        }
    }

}