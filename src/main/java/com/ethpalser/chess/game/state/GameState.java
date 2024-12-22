package com.ethpalser.chess.game.state;

import com.ethpalser.chess.game.event.GameEvent;

public interface GameState {

    GameState update(GameEvent event);

    Iterable<GameEvent> updates();

}
