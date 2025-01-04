package com.ethpalser.chess.space;

/**
 * An immutable representation of 1 to n values describing a singular point in a nth dimension space.
 */
public interface Coordinate {

    /**
     * Describes what the dimension this coordinate correctly exists within.
     *
     * @return The dimension this coordinate exists within. A two-dimensional coordinate returns 2.
     */
    int getDimension();

    int getValue(int dimension);

    int[] getValues();

    default int toIndex(Space space) {
        int size = this.getDimension();
        int[] distances = new int[size];
        int[] coefficients = new int[size];
        coefficients[0] = 1;

        int index = 0;
        for (int i = 0; i < size && i < space.getDimension(); i++) {
            int d = i + 1;
            distances[i] = space.length(d);
            if (i + 1 < size) { // ex. In a 3 x 3 x 3 space, coeff[0] = 1, coeff[1] = 3, coeff[2] = 9
                coefficients[i + 1] = coefficients[i] * distances[i];
            }
            index += coefficients[i] * this.getValue(d);
        }
        return index;
    }

    /**
     * Create a new Coordinate by adding each dimension's value with each given value multiplied by a magnitude, with
     * each dimension in index order. No change is made if the magnitude is 0 or no values are provided. Only up to
     * the coordinate's dimension is used, providing more does nothing and providing less only adds to the highest
     * dimension the values go up to.<br/><br/>
     * Ex. Coordinate (1, 2, 3) translated by values [4, 5] with magnitude 2 results in (9, 12, 3)
     *
     * @param values List of integers that add to this coordinate
     * @return A new coordinate moved by the given values
     */
    Coordinate translate(int magnitude, int... values);

    default Coordinate translate(int magnitude, Direction direction) {
        if (direction == null) {
            return this.translate(magnitude, 0);
        }
        return this.translate(magnitude, direction.vector());
    }
    
    default Coordinate translate(int magnitude, Coordinate coordinate) {
        if (coordinate == null) {
            return this.translate(magnitude, 0);
        }
        return this.translate(magnitude, coordinate.getValues());
    }

    static Coordinate at(int... points) {
        return new Coordinate() {
            @Override
            public int getDimension() {
                return points.length;
            }

            @Override
            public int getValue(int dimension) {
                return points[dimension];
            }

            @Override
            public int[] getValues() {
                return points;
            }

            @Override
            public Coordinate translate(int magnitude, int... values) {
                int[] newValues = new int[points.length];
                for (int i = 0; i < newValues.length; i++) {
                    if (i >= values.length) {
                        newValues[i] = 0;
                    } else {
                        newValues[i] = magnitude * values[i];
                    }
                }
                return Coordinate.at(newValues);
            }
        };
    }

}
