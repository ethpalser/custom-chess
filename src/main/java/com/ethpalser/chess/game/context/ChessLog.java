package com.ethpalser.chess.game.context;

import com.ethpalser.chess.game.event.GameEvent;
import com.ethpalser.chess.game.event.GameEventProxy;
import com.ethpalser.chess.move.notation.ChessNotation;
import com.ethpalser.chess.move.notation.ChessRecord;
import com.ethpalser.chess.piece.Colour;
import java.util.Objects;

public class ChessLog extends GameLog<ChessLog.Entry> {

    public ChessLog() {
        super();
    }

    public ChessLog(String[] entryList) {
        super();
        Colour prevColour = Colour.NO_COLOUR;
        for (String entry : entryList) {
            ChessNotation notation = new ChessNotation(entry);
            ChessRecord rec = notation.toRecord();
            // Specific cases are determined by ReadyState and AwaitState. This should be consistent with those.
            Colour player;
            if (rec.isFollowUp() && !Colour.NO_COLOUR.equals(prevColour)) {
                player = prevColour;
            } else {
                player = rec.sourceColour();
            }
            prevColour = rec.sourceColour();
            GameEvent event = new GameEventProxy(player, notation);
            this.push(new Entry(notation, event));
        }
    }

    public ChessLog(ChessLog original) {
        super(original);
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