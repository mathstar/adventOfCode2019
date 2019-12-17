package com.staricka.aoc2019.days;

import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;
import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridTile;
import com.staricka.aoc2019.util.data.GridValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Day10 extends AocDay {
    private static final char EMPTY_REPRESENTATION = '.';
    private static final Map<Character, Supplier<CelestialObject>> LEGEND;

    static {
        final Map<Character, Supplier<CelestialObject>> temp = new HashMap<>();
        temp.put('#', Asteroid::new);
        temp.put('X', Asteroid::new);
        LEGEND = Collections.unmodifiableMap(temp);
    }

    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day10a.txt")) {
            final ExpandingGrid<CelestialObject> grid =
                    new ExpandingGrid<>(inputStream.getAll(), EMPTY_REPRESENTATION, LEGEND);
            logInfo("%s", grid.toString());

            getBase(grid);
        }
    }

    @Override
    public void part2() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day10a.txt")) {
            final ExpandingGrid<CelestialObject> grid =
                    new ExpandingGrid<>(inputStream.getAll(), EMPTY_REPRESENTATION, LEGEND);
            logInfo("%s", grid.toString());

            final Coordinate base = getBase(grid);
            final List<Coordinate> vaporizationOrder =
                    getVaporizationOrder(base, sortTargets(base, getTargets(base, grid)));
            logInfo("200th: %s", vaporizationOrder.get(199));
        }
    }

    @Override
    public void test() throws Exception {
        //        try (final AocInputStream inputStream = new AocInputStream("day10s4.txt")) {
        //            final ExpandingGrid<CelestialObject> grid =
        //                    new ExpandingGrid<>(inputStream.getAll(), EMPTY_REPRESENTATION, LEGEND);
        //            logInfo("%s", grid.toString());
        //
        //            final AtomicInteger bestCount = new AtomicInteger(Integer.MIN_VALUE);
        //            GridTile<CelestialObject> best = grid.getFilledTiles().stream().max(Comparator.comparing(tile -> {
        //                List<GridTile<CelestialObject>> others =
        //                        grid.getFilledTiles().stream().filter(t -> !t.equals(tile)).collect(Collectors.toList());
        //                long count =
        //                        others.stream().filter(other -> isUnblocked(new Coordinate(tile), new Coordinate(other), grid))
        //                                .count();
        //                logDebug("%d: %s", count, new Coordinate(tile));
        //                bestCount.updateAndGet(i -> Math.max(i, (int) count));
        //                return count;
        //            })).get();
        //
        //            logInfo("Best: %s with %d", new Coordinate(best), bestCount.get());
        //        }
        try (final AocInputStream inputStream = new AocInputStream("day10s4.txt")) {
            final ExpandingGrid<CelestialObject> grid =
                    new ExpandingGrid<>(inputStream.getAll(), EMPTY_REPRESENTATION, LEGEND);
            final Coordinate base = new Coordinate(11, 13);
            logInfo("%s", sortTargets(base, getTargets(base, grid)));
            logInfo("%s", getVaporizationOrder(base, sortTargets(base, getTargets(base, grid))));
        }
    }

    private static Coordinate getBase(final ExpandingGrid<CelestialObject> grid) {
        final AtomicInteger bestCount = new AtomicInteger(Integer.MIN_VALUE);
        GridTile<CelestialObject> best = grid.getFilledTiles().stream().max(Comparator.comparing(tile -> {
            List<GridTile<CelestialObject>> others =
                    grid.getFilledTiles().stream().filter(t -> !t.equals(tile)).collect(Collectors.toList());
            long count = others.stream().filter(other -> isUnblocked(new Coordinate(tile), new Coordinate(other), grid))
                    .count();
            logDebug("%d: %s", count, new Coordinate(tile));
            bestCount.updateAndGet(i -> Math.max(i, (int) count));
            return count;
        })).get();

        logInfo("Best: %s with %d", new Coordinate(best), bestCount.get());
        return new Coordinate(best);
    }

    private static boolean isUnblocked(final Coordinate start, final Coordinate end,
            final ExpandingGrid<CelestialObject> map) {
        final List<Coordinate> sightLine = getSightLine(start, end);
        boolean unblocked = sightLine.stream().noneMatch(coordinate -> map.isFilled(coordinate.x, coordinate.y));
        logDebug("%s %s %s: %s", unblocked, start, end, sightLine);
        return unblocked;
    }

    private static List<Coordinate> getSightLine(final Coordinate start, final Coordinate end) {
        final int diffX = Math.abs(end.x - start.x);
        final int diffY = Math.abs(end.y - start.y);
        final int diffShort = Math.min(diffX, diffY);
        final int diffLong = Math.max(diffX, diffY);
        final boolean shortX = diffX <= diffY;
        final boolean ascending = (start.x < end.x && start.y < end.y) || (start.x > end.x && start.y > end.y);
        final int lowX = Math.min(start.x, end.x);
        final int lowY = Math.min(start.y, end.y);
        final int highX = Math.max(start.x, end.x);
        final int highY = Math.max(start.y, end.y);

        final List<Coordinate> sightLine = new ArrayList<>();
        if (diffShort == 0) {
            for (int i = 1; i < diffLong; i++) {
                if (shortX) {
                    sightLine.add(new Coordinate(lowX, lowY + i));
                } else {
                    sightLine.add(new Coordinate(lowX + i, lowY));
                }
            }
        } else {
            for (int i = 1; i < diffShort; i++) {
                if (diffLong * i % diffShort == 0) {
                    int j = (int) ((double) diffLong / diffShort * i);
                    if (shortX) {
                        if (ascending) {
                            sightLine.add(new Coordinate(lowX + i, lowY + j));
                        } else {
                            sightLine.add(new Coordinate(lowX + i, highY - j));
                        }
                    } else {
                        if (ascending) {
                            sightLine.add(new Coordinate(lowX + j, lowY + i));
                        } else {
                            sightLine.add(new Coordinate(highX - j, lowY + i));
                        }
                    }
                }
            }
        }
        return sightLine;
    }

    private static List<Coordinate> getVaporizationOrder(final Coordinate base,
            final TreeMap<Double, Set<Coordinate>> targets) {
        final List<Coordinate> destroyed = new ArrayList<>();
        while (!targets.isEmpty()) {
            Iterator<Entry<Double, Set<Coordinate>>> iterator = targets.entrySet().iterator();
            while (iterator.hasNext()) {
                final Entry<Double, Set<Coordinate>> candidates = iterator.next();
                final Coordinate target =
                        candidates.getValue().stream().min(Comparator.comparing(c -> getDistance(base, c))).get();
                destroyed.add(target);
                candidates.getValue().remove(target);
                if (candidates.getValue().isEmpty()) {
                    iterator.remove();
                }
            }
        }
        return destroyed;
    }

    private static List<Coordinate> getTargets(final Coordinate start, final ExpandingGrid<CelestialObject> map) {
        return map.getFilledTiles().stream().map(tile -> new Coordinate(tile.getX(), tile.getY()))
                .filter(coordinate -> !coordinate.equals(start)).collect(Collectors.toList());
    }

    private static TreeMap<Double, Set<Coordinate>> sortTargets(final Coordinate start,
            final List<Coordinate> targets) {
        return targets.stream().collect(Collectors.toMap(t -> getAngle(start, t), t -> {
            Set<Coordinate> set = new HashSet<>();
            set.add(t);
            return set;
        }, (s1, s2) -> {
            Set<Coordinate> union = new HashSet<>();
            union.addAll(s1);
            union.addAll(s2);
            return union;
        }, TreeMap::new));
    }

    public static double getAngle(final Coordinate start, final Coordinate end) {
        final double diffX = end.x - start.x;
        final double diffY = end.y - start.y;
        return Math.toDegrees(Math.atan(diffY / diffX) + Math.PI / 2) + ((diffX < 0) ? 180 : 0);
    }

    public static double getDistance(final Coordinate start, final Coordinate end) {
        final double diffX = end.x - start.x;
        final double diffY = end.y - start.y;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public static class Coordinate {
        final int x;
        final int y;

        public Coordinate(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public Coordinate(final GridTile<?> gridTile) {
            this.x = gridTile.getX();
            this.y = gridTile.getY();
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return String.format("(%d,%d)", x, y);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private interface CelestialObject extends GridValue {
    }

    private static class Asteroid implements CelestialObject {
        @Override
        public char getPrintValue() {
            return '#';
        }

        @Override
        public String toString() {
            return new String(new char[] {getPrintValue()});
        }
    }
}
