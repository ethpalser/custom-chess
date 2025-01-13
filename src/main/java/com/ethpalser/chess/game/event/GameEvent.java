package com.ethpalser.chess.game.event;

import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.piece.Colour;

public interface GameEvent {

    Colour player();

    String choice();

    EventType type();

    void execute(GameContext context);

    void unExecute(GameContext context);

}
