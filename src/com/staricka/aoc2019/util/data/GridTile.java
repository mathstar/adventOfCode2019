package com.staricka.aoc2019.util.data;

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
}
