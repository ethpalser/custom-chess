package com.ethpalser.chess.space.custom.reference;

import com.ethpalser.chess.board.Board;
import com.ethpalser.chess.log.Log;
import com.ethpalser.chess.log.LogEntry;
import com.ethpalser.chess.space.Coordinate;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Positional;
import com.ethpalser.chess.space.Space;
import com.ethpalser.chess.space.custom.Location;
import com.ethpalser.chess.view.ReferenceView;
import java.util.List;

public class LogReference<T extends Coordinate, U extends Positional> implements Reference<U> {

    private final Log<T, U> log;

    public LogReference(Log<T, U> log) {
        this.log = log;
    }

    @Override
    public Location getLocation() {
        return Location.LAST_MOVED;
    }

    @Override
    public List<U> getReferences(Board<Coordinate> plane) {
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
    public ReferenceView toView() {
        return new ReferenceView(Location.LAST_MOVED, null, 0, 0);
    }
}