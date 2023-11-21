package com.chess.api.model.movement;

import com.chess.api.model.Colour;
import com.chess.api.model.Coordinate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Movement {

    private final PathType pathType;
    private final boolean mirrorXAxis;
    private final boolean mirrorYAxis;
    private final boolean reverseForBlack;
    private final boolean[][] blueprint;

    public Movement() {
        this.blueprint = new boolean[8][8];
        this.pathType = PathType.NONE;
        this.mirrorXAxis = false;
        this.mirrorYAxis = false;
        this.reverseForBlack = false;
    }

    public Movement(PathType pathType, boolean mirrorXAxis, boolean mirrorYAxis, boolean reverseForBlack,
            boolean[][] moveBlueprint) {
        this.pathType = pathType;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.reverseForBlack = reverseForBlack;
        this.blueprint = moveBlueprint;
    }

    public Movement(PathType pathType, boolean mirrorXAxis, boolean mirrorYAxis, boolean reverseForBlack,
            Coordinate... blueprintCoordinates) {
        this.pathType = pathType;
        this.mirrorXAxis = mirrorXAxis;
        this.mirrorYAxis = mirrorYAxis;
        this.reverseForBlack = reverseForBlack;

        boolean[][] moveCoordinates = new boolean[Coordinate.MAX_X + 1][Coordinate.MAX_Y + 1];
        for (Coordinate c : blueprintCoordinates) {
            moveCoordinates[c.getX()][c.getY()] = true;
        }
        this.blueprint = moveCoordinates;
    }

    public List<Coordinate> getCoordinates() {
        List<Coordinate> coordinates = new ArrayList<>();
        for (int y = 0; y < this.blueprint[0].length; y++) {
            for (int x = 0; x < this.blueprint.length; x++) {
                if (this.blueprint[x][y]) {
                    coordinates.add(new Coordinate(x, y));
                }
            }
        }
        return coordinates;
    }

    public List<Coordinate> getCoordinates(Coordinate offset, Colour colour) {
        final int coX = offset.getX();
        final int coY = offset.getY();

        List<Coordinate> coordinates = new ArrayList<>();
        if (!this.reverseForBlack || this.mirrorXAxis) {
            coordinates.addAll(this.getCoordinatesNoMirror(coX, coY));
        }
        if ((this.reverseForBlack && Colour.BLACK.equals(colour)) || this.mirrorXAxis) {
            coordinates.addAll(this.getCoordinatesMirrorX(coX, coY));
        }
        if (this.mirrorYAxis) {
            coordinates.addAll(this.getCoordinatesMirrorY(coX, coY));
        }
        if (this.mirrorXAxis && this.mirrorYAxis) {
            coordinates.addAll(this.getCoordinatesMirrorBoth(coX, coY));
        }
        return coordinates;
    }

    private List<Coordinate> getCoordinatesNoMirror(int coX, int coY) {
        List<Coordinate> coordinates = new ArrayList<>();

        for (int y = coY; y <= Coordinate.MAX_Y && y < blueprint[0].length; y++) {
            for (int x = coX; x <= Coordinate.MAX_X && x < blueprint.length; x++) {
                if (blueprint[x - coX][y - coY]) {
                    coordinates.add(new Coordinate(x, y));
                }
            }
        }
        return coordinates;
    }

    private List<Coordinate> getCoordinatesMirrorX(int coX, int coY) {
        List<Coordinate> coordinates = new ArrayList<>();

        for (int y = coY - 1; y >= 0; y--) {
            if (coY - y > blueprint[0].length)
                continue;
            for (int x = coX; x <= Coordinate.MAX_X && x < blueprint.length; x++) {
                if (blueprint[x - coX][coY - y]) {
                    coordinates.add(new Coordinate(x, y));
                }
            }
        }
        return coordinates;
    }

    private List<Coordinate> getCoordinatesMirrorY(int coX, int coY) {
        List<Coordinate> coordinates = new ArrayList<>();

        for (int y = coY; y <= Coordinate.MAX_Y && y < blueprint[0].length; y++) {
            for (int x = coX - 1; x >= 0; x--) {
                if (coX - x < blueprint[0].length && blueprint[coX - x][y - coY]) {
                    coordinates.add(new Coordinate(x, y));
                }
            }
        }
        return coordinates;
    }

    private List<Coordinate> getCoordinatesMirrorBoth(int coX, int coY) {
        List<Coordinate> coordinates = new ArrayList<>();

        for (int y = coY - 1; y >= 0; y--) {
            if (coY - y > blueprint[0].length)
                continue;
            for (int x = coX - 1; x >= 0; x--) {
                if (coX - x < blueprint[0].length && blueprint[coX - x][coY - y]) {
                    coordinates.add(new Coordinate(x, y));
                }
            }
        }
        return coordinates;
    }

    public boolean[][] drawCoordinates() {
        List<Coordinate> coordinates = this.getCoordinates();
        boolean[][] boardMove = new boolean[Coordinate.MAX_X + 1][Coordinate.MAX_Y + 1];
        for (Coordinate c : coordinates) {
            boardMove[c.getX()][c.getY()] = true;
        }
        return boardMove;
    }

    public boolean[][] drawCoordinates(Coordinate offset) {
        List<Coordinate> coordinates = this.getCoordinates(offset, Colour.WHITE);
        boolean[][] boardMove = new boolean[Coordinate.MAX_X + 1][Coordinate.MAX_Y + 1];
        for (Coordinate c : coordinates) {
            boardMove[c.getX()][c.getY()] = true;
        }
        return boardMove;
    }

    public boolean[][] drawCoordinates(Coordinate offset, Colour colour) {
        List<Coordinate> coordinates = this.getCoordinates(offset, colour);
        boolean[][] boardMove = new boolean[Coordinate.MAX_X + 1][Coordinate.MAX_Y + 1];
        for (Coordinate c : coordinates) {
            boardMove[c.getX()][c.getY()] = true;
        }
        return boardMove;
    }


}
