package com.chess.api.game.movement;

import com.chess.api.game.Colour;
import com.chess.api.game.Vector2D;

/**
 * Container for an attempted piece movement for a Player of this colour, Vector2D start and Vector2D end
 */
public record Action(Colour colour, Vector2D start, Vector2D end) {

}
