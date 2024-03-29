package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * Represents a 2D coordinate in the game grid with methods to obtain neighboring coordinates.
 */
public class Coordinate implements Serializable {
    private final int column;
    private final int row;

    /**
     * Constructs a new Coordinate with the given column and row values.
     *
     * @param column The column value for the coordinate.
     * @param row    The row value for the coordinate.
     */
    public Coordinate(int row, int column) {
        this.column = column;
        this.row = row;
    }

    /**
     * Get the column of the coordinate
     *
     * @return the index of the column
     */
    public int getColumn() {
        return column;
    }

    /**
     * Get the row of the coordinate
     *
     * @return the index of the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the coordinate adjacent to the current coordinate in the specified direction.
     *
     * @param direction The direction of the adjacent coordinate to retrieve.
     *                  Must be one of the values in the {@link Direction} enumeration.
     * @return A new Coordinate object representing the adjacent coordinate in the specified direction.
     * @throws IllegalArgumentException If the direction parameter is not a valid value in the {@link Direction} enumeration.
     */
    public Coordinate getAdjacent(Direction direction) {
        return switch (direction) {
            case UP -> new Coordinate(row + 1, column);
            case DOWN -> new Coordinate(row - 1, column);
            case LEFT -> new Coordinate(row, column - 1);
            case RIGHT -> new Coordinate(row, column + 1);
        };
    }

    /**
     * Override of the equals method
     *
     * @param obj the object to compare
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Coordinate that = (Coordinate) obj;
        return column == that.column && row == that.row;
    }

    /**
     * Override of the hashCode method
     *
     * @return the hashcode of the object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + column;
        result = prime * result + row;
        return result;
    }

    /**
     * Override of the toString method
     *
     * @return the string representation of the object
     */
    @Override
    public String toString() {
        return "[" + row + "," + column + ']';
    }

    /**
     * Represents the four possible directions for moving from a coordinate in a 2D grid.
     */
    public enum Direction {
        /**
         * The UP direction.
         */
        UP,

        /**
         * The DOWN direction.
         */
        DOWN,

        /**
         * The LEFT direction.
         */
        LEFT,

        /**
         * The RIGHT direction.
         */
        RIGHT
    }
}