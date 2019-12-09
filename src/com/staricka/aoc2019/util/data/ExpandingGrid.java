package com.staricka.aoc2019.util.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ExpandingGrid<T extends GridValue> {
    private final Map<Integer, Map<Integer, T>> grid;
    private char emptyRepresentation = '.';

    public ExpandingGrid() {
        grid = new TreeMap<>();
    }

    public ExpandingGrid(char emptyRepresentation) {
        this();
        this.emptyRepresentation = emptyRepresentation;
    }

    public void put(final int x, final int y, T value) {
        Map<Integer, T> row = grid.computeIfAbsent(y, z -> new TreeMap<>());
        row.put(x, value);
    }

    public void compute(final int x, final int y, final Function<T, T> transform) {
        Map<Integer, T> row = grid.computeIfAbsent(y, z -> new TreeMap<>());
        row.put(x, transform.apply(row.get(x)));
    }

    public T get(final int x, final int y) {
        Map<Integer, T> row = grid.get(y);
        if (row != null) {
            return row.get(x);
        } else {
            return null;
        }
    }

    public List<GridTile<T>> getFilledTiles() {
        List<GridTile<T>> tiles = new ArrayList<>();
        for(final Entry<Integer, Map<Integer, T>> row : grid.entrySet()) {
            for (final Entry<Integer, T> column : row.getValue().entrySet()) {
                tiles.add(new GridTile<>(column.getKey(), row.getKey(), column.getValue()));
            }
        }
        return tiles;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        int minX =
                grid.values().stream().flatMap(m -> m.keySet().stream()).mapToInt(Integer::intValue).min().getAsInt();
        int maxX =
                grid.values().stream().flatMap(m -> m.keySet().stream()).mapToInt(Integer::intValue).max().getAsInt();
        int width = maxX - minX + 1;

        Integer lastRow = null;
        for (final Entry<Integer, Map<Integer, T>> row : grid.entrySet()) {
            if (lastRow != null) {
                int rowsToSkip = row.getKey() - lastRow - 1;
                IntStream.of(rowsToSkip).forEach(n0 -> {
                    IntStream.of(width).forEach(n1 -> stringBuilder.append(emptyRepresentation));
                    stringBuilder.append('\n');
                });
            }
            for(int x = minX; x <= maxX; x++) {
                T value = row.getValue().get(x);
                if(value != null) {
                    stringBuilder.append(value.getPrintValue());
                } else {
                    stringBuilder.append(emptyRepresentation);
                }
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}
