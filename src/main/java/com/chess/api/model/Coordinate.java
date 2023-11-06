package com.chess.api.model;

public class Coordinate {

    int posX;
    int posY;

    public Coordinate(int x, int y) {
        if (x > 7 || x < 0 || y > 7 || y < 0) {
            throw new IndexOutOfBoundsException();
        }

        posX = x;
        posY = y;
    }

}
