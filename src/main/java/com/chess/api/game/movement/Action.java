package com.chess.api.game.movement;

import com.chess.api.game.Colour;
import com.chess.api.game.Vector2D;

public record Action(Colour colour, Vector2D start, Vector2D end) {

}
