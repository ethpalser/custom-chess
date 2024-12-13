package com.ethpalser.chess.space;

public interface Space {

    /**
     * Returns the highest dimension this coordinate space represents.
     *
     * @return The highest dimension of this space. A two-dimensional space returns 2.
     */
    int getDimension();

    /**
     * Returns the highest value allowed in the given dimension. The first dimension should start at 1.
     * The max cannot be lower than the min.
     *
     * @return The highest value in the given dimension.
     */
    int getDimensionMax(int dimension);

    /**
     * Returns the lowest value allowed in the given dimension. The first dimension should start at 1.
     * The min cannot be greater than the max.
     *
     * @return The lowest value in the given dimension.
     */
    int getDimensionMin(int dimension);

    /**
     * Checks if this coordinate does not exist in this space. This may or may not check if this coordinate is in the
     * same dimension. Regardless, each value should be within the bounds of each dimension.
     *
     * @param coordinate {@link Coordinate}
     * @return If all values are within the bounds of min and max.
     */
    default boolean isOutOfBounds(Coordinate coordinate) {
        int[] check = new int[this.getDimension()];
        // Truncates the numbers to check to the highest dimension of this space, or expands it with zeros
        for (int i = 0; i < coordinate.getDimension(); i++) {
            check[i] = coordinate.getValue(i + 1);
        }

        for (int i = 0; i < check.length; i++) {
            if (this.getDimensionMax(i + 1) < check[i] || check[i] < this.getDimensionMin(i + 1))
                return true;
        }
        return false;
    }

}
