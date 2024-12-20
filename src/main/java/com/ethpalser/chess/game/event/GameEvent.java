package com.ethpalser.chess.game.event;

import com.ethpalser.chess.game.GameContext;

public interface GameEvent {

    EventType type();

    void execute(GameContext context);

    void unExecute(GameContext context);

}
