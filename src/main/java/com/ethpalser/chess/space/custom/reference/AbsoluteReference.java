package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.custom.Location;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String toJson() {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>(4);
        map.put("location", Location.POINT.toString());
        map.put("point", this.point);
        map.put("xOffset", "0");
        map.put("yOffset", "0");
        return gson.toJson(map);
    }
}
