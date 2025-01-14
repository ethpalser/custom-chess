package com.ethpalser.chess.game.event;

import com.ethpalser.chess.annotation.ClassPreamble;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;

/**
 * Container for an attempted piece movement for a Player of this colour, Point start and Point end.
 *
 * @deprecated Since January 12, 2025. Replaced by MoveEvent.
 */
@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2023-12-01",
        lastModified = "2025-01-13"
)
@Deprecated(since = "2025-01-12")
public class Action {

    private final Colour colour;
    private final Coordinate start;
    private final Coordinate end;

    private Action() {
        colour = Colour.WHITE;
        start = new Point();
        end = new Point();
    }

    public Action(Colour colour, Coordinate start, Coordinate end) {
        this.colour = colour;
        this.start = start;
        this.end = end;
    }

    public Colour getColour() {
        return colour;
    }

    public Coordinate getStart() {
        return start;
    }

    public Coordinate getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "Action{" +
                "colour=" + colour +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
