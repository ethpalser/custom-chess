package com.ethpalser.chess.game;

import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.piece.Colour;

/**
 * Container for an attempted piece movement for a Player of this colour, Point start and Point end
 */
public class Action {

    private final Colour colour;
    private final Point start;
    private final Point end;

    private Action() {
        colour = Colour.WHITE;
        start = new Point();
        end = new Point();
    }

    public Action(Colour colour, Point start, Point end) {
        this.colour = colour;
        this.start = start;
        this.end = end;
    }

    public Colour getColour() {
        return colour;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

}
