package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.custom.Location;
import java.util.List;

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
}