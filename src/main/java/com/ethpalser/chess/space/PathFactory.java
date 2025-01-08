package com.ethpalser.chess.space;

import com.ethpalser.chess.game.GameContext;
import java.util.LinkedList;
import java.util.List;

public class PathFactory {

    private final GameContext.Record context;
    private final Coordinate relativeTo;

    public PathFactory(GameContext.Record context, Coordinate relativeTo) {
        this.context = context;
        this.relativeTo = relativeTo;
    }

    public Path create(PathOptions options) {
        Space space = context.getBoard().space();
        switch (options.type()) {
            case HORIZONTAL -> {
                return new Path(space, new Point(1, 0), new int[]{1, 0});
            }
            case VERTICAL -> {
                return new Path(space, new Point(0, 1), new int[]{0, 1});
            }
            case DIAGONAL -> {
                return new Path(space, new Point(1, 1), new int[]{1, 1});
            }
            case CUSTOM -> {
                if (options.start() == null) {
                    return new Path(List.of());
                }
                List<Coordinate> startPath = options.start().coordinates(context, relativeTo);
                if (startPath == null || startPath.isEmpty()) {
                    return new Path(List.of());
                }

                if (options.end() == null) {
                    return new Path(startPath);
                }
                List<Coordinate> endPath = options.end().coordinates(context, relativeTo);
                if (endPath == null || endPath.isEmpty()) {
                    return new Path(startPath);
                }
                // Assume that this is intended, as a Path-based reference was used
                if (startPath.size() > 1 || endPath.size() > 1) {
                    List<Coordinate> list = new LinkedList<>(startPath);
                    list.addAll(endPath);
                    return new Path(list);
                }
                // Otherwise, assume a linear path between these two points is expected
                Coordinate start = startPath.get(0);
                Coordinate end = endPath.get(0);
                return this.pathBetweenCoordinates(start, end);
            }
        }
        return new Path(List.of());
    }

    private Path pathBetweenCoordinates(Coordinate start, Coordinate end) {
        int xEnd = end.getValue(Space.AXIS.X);
        int yEnd = end.getValue(Space.AXIS.Y);
        int xDiff = start.getValue(Space.AXIS.X) - xEnd;
        int yDiff = start.getValue(Space.AXIS.Y) - yEnd;
        int xDir = xDiff != 0 ? xDiff / Math.abs(xDiff) : 0;
        int yDir = yDiff != 0 ? yDiff / Math.abs(yDiff) : 0;
        // Fill in the gap between these two points
        List<Coordinate> list = new LinkedList<>();
        Coordinate point = start;
        do {
            list.add(point);
            point = point.translate(1, xDir, yDir);
        } while (point.getValue(Space.AXIS.X) != xEnd || point.getValue(Space.AXIS.Y) != xEnd);
        // Loop only continues until the end point is reached on either axis, so this is added after
        list.add(end);
        return new Path(list);
    }
}