package com.ethpalser.chess.space;

public interface Coordinate extends Comparable<Coordinate> {

    /**
     * Describes what the dimension this coordinate correctly exists within.
     *
     * @return The dimension this coordinate exists within. A two-dimensional coordinate returns 2.
     */
    int getDimension();

    int getValue(int dimension);

    int[] getValues();

    /**
     * Modify this Coordinate's values for each dimension using the provided list of values. The first value applies
     * to the first dimension. Providing less than the dimension this coordinate exists in will only move it
     * in the lower dimensions. Providing zero will not move the coordinate in that dimension.
     *
     * @param values List of integers that add to this coordinate
     * @return A new coordinate moved by the given values
     */
    Coordinate translate(int... values);



}
