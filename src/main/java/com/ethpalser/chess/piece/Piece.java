package com.ethpalser.chess.piece;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Positional;
import java.util.List;

public interface Piece extends Positional {

    String getCode();

    Colour getColour();

    Coordinate getCoordinate();

    void setCoordinate(Coordinate coordinate);

    MoveSet getMoves(Board<Coordinate> board);

    default MoveSet getMoves(Board<Coordinate> board, Log<Coordinate, Piece> log) {
        return this.getMoves(board, log, null, false, false);
    }

    default MoveSet getMoves(Board<Coordinate> board, Log<Coordinate, Piece> log, ThreatMap threats) {
        return this.getMoves(board, log, threats, false, false);
    }

    MoveSet getMoves(Board<Coordinate> board, Log<Coordinate, Piece> log, ThreatMap threats, boolean onlyAttacks,
            boolean includeDefends);

    default boolean canMove(Board<Coordinate> board, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board).toSet().stream().anyMatch(m -> m.getPath().toSet().contains(destination));
    }

    default boolean canMove(Board<Coordinate> board, Log<Coordinate, Piece> log, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board, log).toSet().stream().anyMatch(m -> m.getPath().toSet().contains(destination));
    }

    default boolean canMove(Board<Coordinate> board, Log<Coordinate, Piece> log, ThreatMap threats, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board, log, threats).toSet().stream().anyMatch(m -> m.getPath().toSet().contains(destination));
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
