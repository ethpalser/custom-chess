package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.custom.Location;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogReference<T extends Comparable<T>, U extends Positional> implements Reference<U> {

    private final Log<T, U> log;

    public LogReference(Log<T, U> log) {
        this.log = log;
    }

    @Override
    public Location getLocation() {
        return Location.LAST_MOVED;
    }

    @Override
    public List<U> getReferences(Plane<U> plane) {
        if (this.log == null) {
            return List.of();
        }
        LogEntry<T, U> entry = log.peek();
        if (entry != null) {
            if (entry.getStartObject() != null) {
                return List.of(entry.getStartObject());
            } else if (entry.getEndObject() != null) {
                return List.of(entry.getEndObject());
            }
        }
        return List.of();
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>(4);
        map.put("location", Location.LAST_MOVED.toString());
        map.put("point", null);
        map.put("xOffset", "0");
        map.put("yOffset", "0");
        return gson.toJson(map);
    }
}