package com.staricka.aoc2019.util.data;

import java.util.Objects;

public class GridTile<T extends GridValue> {
    private final int x;
    private final int y;
    private final T value;

    public GridTile(final int x, final int y, final T value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + value.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GridTile<?> gridTile = (GridTile<?>) o;
        return x == gridTile.x && y == gridTile.y && Objects.equals(value, gridTile.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, value);
    }
}
