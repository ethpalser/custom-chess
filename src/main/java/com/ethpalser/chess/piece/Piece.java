package com.ethpalser.chess.piece;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.game.GameContext;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.space.Coordinate;
import java.util.List;

public interface Piece {

    String getCode();

    Colour getColour();

    Coordinate getCoordinate();

    void setCoordinate(Coordinate coordinate);

    MoveSet getMoves(GameContext.Record context);

    default boolean canMove(Coordinate target, GameContext.Record context) {
        if (context == null) {
            return false;
        }
        return this.getMoves(context).moves().stream().anyMatch(m -> m.path().toSet().contains(target));
    }

    boolean getHasMoved();

    void setHasMoved(boolean hasMoved);

    default void move(Coordinate point) {
        if (point == null) {
            throw new IllegalArgumentException("piece cannot move to null");
        }
        if (point.equals(this.getCoordinate())) {
            return;
        }
        this.setCoordinate(point);
        this.setHasMoved(true);
    }

    boolean canPromote(Board<Coordinate> board);

    List<String> promoteOptions();

}
