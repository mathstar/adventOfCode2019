package com.staricka.aoc2019.days;

import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;
import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day3 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("/day3a.txt")) {
            ExpandingGrid<GridValue> grid = generateGrid(inputStream.lines());
            int min = closestIntersection(grid);
            logInfo("Min distance to intersection: %d", min);
        }
    }

    @Override
    public void part2() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("/day3a.txt")) {
            ExpandingGrid<GridValue> grid = generateGrid(inputStream.lines());
            int min = grid.getFilledTiles().stream().filter(tile -> tile.getValue() instanceof WireUnit)
                    .filter(tile -> ((WireUnit) tile.getValue()).wires.size() > 1).mapToInt(
                            tile -> ((WireUnit) tile.getValue()).wires.values().stream().mapToInt(Integer::intValue)
                                    .sum()).min().getAsInt();
            logInfo("Min heat to intersection: %d", min);
        }
    }

    @Override
    public void test() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("/day3s1.txt")) {
            ExpandingGrid<GridValue> grid = generateGrid(inputStream.lines());
            logInfo(grid.toString());
            int min = closestIntersection(grid);
            logInfo("Min distance to intersection: %d", min);
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
                writer.write(grid.toString());
            }
        }
    }

    private int closestIntersection(final ExpandingGrid<GridValue> grid) {
        logDebug(() -> grid.getFilledTiles().stream().filter(tile -> tile.getValue() instanceof WireUnit)
                .filter(tile -> ((WireUnit) tile.getValue()).wires.size() > 1).collect(Collectors.toList()).toString());
        return grid.getFilledTiles().stream().filter(tile -> tile.getValue() instanceof WireUnit)
                .filter(tile -> ((WireUnit) tile.getValue()).wires.size() > 1)
                .mapToInt(tile -> Math.abs(tile.getX()) + Math.abs(tile.getY())).min().getAsInt();
    }

    private ExpandingGrid<GridValue> generateGrid(final Stream<String> wires) {
        final ExpandingGrid<GridValue> grid = new ExpandingGrid<>();
        grid.put(0, 0, new Origin());
        AtomicInteger wireCount = new AtomicInteger(0);
        wires.forEach(line -> {
            int wire = wireCount.getAndIncrement();
            AtomicInteger x = new AtomicInteger(0);
            AtomicInteger y = new AtomicInteger(0);
            AtomicInteger heat = new AtomicInteger(0);
            Arrays.stream(line.split(",")).forEach(step -> {
                logDebug(step);
                final String direction = step.substring(0, 1);
                final int distance = Integer.parseInt(step.substring(1));
                switch (direction) {
                    case "U": {
                        IntStream.range(0, distance).forEach(n -> {
                            grid.compute(x.get(), y.decrementAndGet(), update(wire, heat.incrementAndGet()));
                        });
                        break;
                    }
                    case "D": {
                        IntStream.range(0, distance).forEach(n -> {
                            grid.compute(x.get(), y.incrementAndGet(), update(wire, heat.incrementAndGet()));
                        });
                        break;
                    }
                    case "L": {
                        IntStream.range(0, distance).forEach(n -> {
                            grid.compute(x.decrementAndGet(), y.get(), update(wire, heat.incrementAndGet()));
                        });
                        break;
                    }
                    case "R": {
                        IntStream.range(0, distance).forEach(n -> {
                            grid.compute(x.incrementAndGet(), y.get(), update(wire, heat.incrementAndGet()));
                        });
                        break;
                    }
                }
                logDebug(grid::toString);
            });
        });
        return grid;
    }

    private Function<GridValue, GridValue> update(final int wire, final int heat) {
        return gridValue -> {
            if (gridValue == null) {
                return new WireUnit(wire, heat);
            } else if (gridValue instanceof WireUnit) {
                return new WireUnit((WireUnit) gridValue, wire, heat);
            } else {
                throw new RuntimeException("collision");
            }
        };
    }

    private class WireUnit implements GridValue {
        private final Map<Integer, Integer> wires;

        WireUnit(final int wire, final int heat) {
            this.wires = Collections.singletonMap(wire, heat);
        }

        WireUnit(final WireUnit current, final int wire, final int heat) {
            this.wires = new HashMap<>(current.wires);
            this.wires.putIfAbsent(wire, heat);
        }

        @Override
        public char getPrintValue() {
            return String.valueOf(wires.size()).charAt(0);
        }

        @Override
        public String toString() {
            return wires.toString();
        }
    }

    private class Origin implements GridValue {
        @Override
        public char getPrintValue() {
            return 'o';
        }
    }
}
