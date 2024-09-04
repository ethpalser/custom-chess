package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Direction;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.custom.Location;
import com.ethpalser.chess.view.ReferenceView;

public class ReferenceFactory {

    private final Plane<Piece> board;
    private final Log<Point, Piece> log;

    public ReferenceFactory(Plane<Piece> board, Log<Point, Piece> log) {
        this.board = board;
        this.log = log;
    }

    public Reference<Piece> build(Location location, Direction direction, Point start, int shiftX, int shiftY) {
        switch (location) {
            case PIECE -> {
                return new PieceReference(board.get(start), direction, shiftX, shiftY);
            }
            case POINT -> {
                return new AbsoluteReference<>(start);
            }
            case LAST_MOVED -> {
                return new LogReference<>(this.log);
            }
            default -> {
                return new PathReference<>(location, start, this.pathEnd(start, shiftX, shiftY));
            }
        }
    }

    public Reference<Piece> build(ReferenceView view) {
        if (view == null) {
            return null;
        }
        switch (view.getLocation()) {
            case LAST_MOVED -> {
                return new LogReference<>(this.log);
            }
            case PATH -> {
                return new PathReference<>(view.getLocation(), view.getPoint(), this.pathEnd(view.getPoint(),
                        view.getXOffset(), view.getYOffset()));
            }
            case PIECE -> {
                return new PieceReference(this.board.get(view.getPoint()), Direction.AT,
                        view.getXOffset(), view.getYOffset());
            }
            case POINT -> {
                return new AbsoluteReference<>(view.getPoint());
            }
            default -> {
                return null;
            }
        }
    }

    private Point pathEnd(Point start, int shiftX, int shiftY) {
        return new Point(start.getX() + shiftX, start.getY() + shiftY);
    }

}
