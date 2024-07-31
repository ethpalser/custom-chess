package com.chess.game.piece;

import com.chess.game.Colour;
import com.chess.game.Space2D;
import com.chess.game.Vector2D;
import com.chess.game.movement.ActionRecord;
import java.util.Collection;
import java.util.Set;

public interface ChessPiece {

    String getCode();

    Colour getColour();

    Set<Vector2D> getMoves(Space2D<Piece> board);

    boolean canMove(Space2D<Piece> board, Collection<ActionRecord> log, Vector2D destination);

    void move(Vector2D destination);
}
