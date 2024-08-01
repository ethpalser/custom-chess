package com.chess.game.movement;

import com.chess.game.Vector2D;
import java.util.Optional;
import java.util.Set;

public interface ChessMove extends Set<Vector2D> {

    Set<Vector2D> getMoves();

    Optional<MoveRecord> getFollowupMove();

}
