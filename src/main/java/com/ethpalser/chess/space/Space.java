package com.ethpalser.chess.space;

public interface Space {

    class AXIS {
        public static final int X = 1;
        public static final int Y = 2;
        public static final int Z = 3;
        private AXIS(){}
    }

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
     * @param dimension
     * @return The highest value in the given dimension.
     */
    int max(int dimension);

    /**
     * Returns the lowest value allowed in the given dimension. The first dimension should start at 1.
     * The min cannot be greater than the max.
     *
     * @param dimension
     * @return The lowest value in the given dimension.
     */
    int min(int dimension);

    /**
     * Returns the length from lowest to highest value in the given dimension. The first dimension should start at 1.
     *
     * @param dimension
     * @return The length from lowest to highest value in the given dimension.
     */
    default int length(int dimension) {
        return Math.abs(this.min(dimension)) + Math.abs(this.max(dimension)) + 1;
    }

    /**
     * Determines if the given coordinate is available to use. Unavailable coordinates are within bounds, and may be
     * holes in space or used coordinates that cannot be reused.
     *
     * @param coordinate {@link Coordinate}
     * @return True if this coordinate can be used, otherwise false
     */
    default boolean isUnavailable(Coordinate coordinate) {
        return false;
    }

    /**
     * Checks if this coordinate does not exist in this space. This may or may not check if this coordinate is in the
     * same dimension. Regardless, each value should be within the bounds of each dimension.
     *
     * @param coordinate {@link Coordinate}
     * @return If all values are within the bounds of min and max.
     */
    default boolean isOutOfBounds(Coordinate coordinate) {
        if (coordinate == null) {
            return true;
        }
        int[] check = new int[this.getDimension()];
        // Truncates the numbers to check to the highest dimension of this space, or expands it with zeros
        for (int i = 0; i < coordinate.getDimension(); i++) {
            check[i] = coordinate.getValue(i + 1);
        }

        for (int i = 0; i < check.length; i++) {
            if (this.max(i + 1) < check[i] || check[i] < this.min(i + 1))
                return true;
        }
        return false;
    }

    default String printBounds() {
        StringBuilder sb = new StringBuilder();
        for (int d = 1; d <= this.getDimension(); d++) {
            sb.append(d).append(": [")
                    .append(this.min(d)).append(",")
                    .append(this.max(d))
                    .append("]");
            if (d != this.getDimension()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

}
