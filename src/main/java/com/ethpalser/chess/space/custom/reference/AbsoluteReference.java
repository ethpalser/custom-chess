package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.custom.Location;
import com.ethpalser.chess.view.ReferenceView;
import java.util.List;

public class AbsoluteReference<T extends Positional> implements Reference<T> {

    private final Coordinate point;

    public AbsoluteReference(Coordinate point) {
        this.point = point;
    }

    @Override
    public Location getLocation() {
        return Location.POINT;
    }

    @Override
    public List<T> getReferences(Board<Coordinate> plane) {
        T ref = (T) plane.get(this.point);
        if (ref == null) {
            return List.of();
        }
        return List.of(ref);
    }

    @Override
    public ReferenceView toView() {
        return new ReferenceView(Location.POINT, (Point) this.point, 0, 0);
    }
}
