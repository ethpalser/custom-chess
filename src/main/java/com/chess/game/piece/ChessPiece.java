package com.chess.game.piece;

import com.chess.game.Space2D;
import com.chess.game.Vector2D;
import com.chess.game.movement.ActionRecord;
import java.util.Collection;
import java.util.Set;

public interface ChessPiece {

    String getCode();

    Set<Vector2D> getMovement(Space2D<Piece> board);

    boolean canPerformMove(Space2D<Piece> board, Collection<ActionRecord> log, Vector2D destination);

}
