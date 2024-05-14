package com.chess.game.movement;

import com.chess.game.Colour;
import com.chess.game.Vector2D;

/**
 * Container for an attempted piece movement for a Player of this colour, Vector2D start and Vector2D end
 */
public class Action {

    private final Colour colour;
    private final Vector2D start;
    private final Vector2D end;

    private Action() {
        colour = Colour.WHITE;
        start = new Vector2D();
        end = new Vector2D();
    }

    public Action(Colour colour, Vector2D start, Vector2D end) {
        this.colour = colour;
        this.start = start;
        this.end = end;
    }

    public Colour getColour() {
        return colour;
    }

    public Vector2D getStart() {
        return start;
    }

    public Vector2D getEnd() {
        return end;
    }

}
