package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.game.view.ReferenceView;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.custom.Location;
import java.util.List;

public class AbsoluteReference<T extends Positional> implements Reference<T> {

    private final Point point;

    public AbsoluteReference(Point point) {
        this.point = point;
    }

    @Override
    public Location getLocation() {
        return Location.POINT;
    }

    @Override
    public List<T> getReferences(Plane<T> plane) {
        T ref = plane.get(this.point);
        if (ref == null) {
            return List.of();
        }
        return List.of(ref);
    }

    @Override
    public ReferenceView toView() {
        return new ReferenceView(Location.POINT, this.point, 0, 0);
    }
}
