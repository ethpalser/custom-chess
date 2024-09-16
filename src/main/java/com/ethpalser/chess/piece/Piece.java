package com.ethpalser.chess.piece;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.move.MoveSet;
import com.ethpalser.chess.move.map.ThreatMap;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Positional;
import java.util.List;

public interface Piece extends Positional {

    String getCode();

    Colour getColour();

    Point getPoint();

    void setPoint(Point point);

    MoveSet getMoves(Plane<Piece> board);

    default MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log) {
        return this.getMoves(board, log, null, false, false);
    }

    default MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log, ThreatMap threats) {
        return this.getMoves(board, log, threats, false, false);
    }

    MoveSet getMoves(Plane<Piece> board, Log<Point, Piece> log, ThreatMap threats, boolean onlyAttacks,
            boolean includeDefends);

    default boolean canMove(Plane<Piece> board, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board).toSet().stream().anyMatch(m -> m.getPath().toSet().contains(destination));
    }

    default boolean canMove(Plane<Piece> board, Log<Point, Piece> log, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board, log).toSet().stream().anyMatch(m -> m.getPath().toSet().contains(destination));
    }

    default boolean canMove(Plane<Piece> board, Log<Point, Piece> log, ThreatMap threats, Point destination) {
        if (board == null || destination == null) {
            return false;
        }
        return this.getMoves(board, log, threats).toSet().stream().anyMatch(m -> m.getPath().toSet().contains(destination));
    }

    boolean getHasMoved();

    void setHasMoved(boolean hasMoved);

    default void move(Point point) {
        if (point == null) {
            throw new IllegalArgumentException("piece cannot move to null");
        }
        if (point.equals(this.getPoint())) {
            return;
        }
        this.setPoint(point);
        this.setHasMoved(true);
    }

    boolean canPromote(Plane<Piece> board);

    List<String> promoteOptions();

}
