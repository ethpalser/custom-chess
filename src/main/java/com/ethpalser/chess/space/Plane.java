package com.ethpalser.chess.space;

import com.ethpalser.chess.annotation.ClassPreamble;
import java.util.List;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-05-14",
        majorVersion = 2,
        minorVersion = 6,
        lastModified = "2025-01-13"
)
public class Plane implements Space {

    private static final int DIMENSION = 2;
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;
    private final boolean[][] unavailable;

    public Plane() {
        this.minX = Integer.MIN_VALUE >> Plane.DIMENSION;
        this.maxX = Integer.MAX_VALUE >> Plane.DIMENSION;
        this.minY = Integer.MIN_VALUE >> Plane.DIMENSION;
        this.maxY = Integer.MAX_VALUE >> Plane.DIMENSION;
        int width = Math.abs(this.minX) + Math.abs(this.maxX);
        int height = Math.abs(this.minY) + Math.abs(this.maxY);
        this.unavailable = new boolean[width][height];
    }

    public Plane(int width, int height) {
        this.minX = 0;
        this.minY = 0;
        this.maxX = width - 1;
        this.maxY = height - 1;
        this.unavailable = new boolean[width][height];
    }

    public Plane(int width, int height, List<Coordinate> unavailable) {
        this(width, height);
        for (Coordinate c : unavailable) {
            if (c.getDimension() > 1) {
                this.unavailable[c.getValue(1)][c.getValue(2)] = true;
            } else {
                this.unavailable[c.getValue(1)][0] = true;
            }
        }
    }

    @Override
    public int getDimension() {
        return Plane.DIMENSION;
    }

    @Override
    public int max(int dimension) {
        return switch (dimension) {
            case 1 -> this.maxX;
            case 2 -> this.maxY;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public int min(int dimension) {
        return switch (dimension) {
            case 1 -> this.minX;
            case 2 -> this.minY;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public boolean isUnavailable(Coordinate coordinate) {
        if (coordinate.getDimension() > 1) {
            return this.unavailable[coordinate.getValue(1)][coordinate.getValue(2)];
        } else {
            return this.unavailable[coordinate.getValue(1)][0];
        }
    }
}
