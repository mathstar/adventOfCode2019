package com.staricka.aoc2019.days;

import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;
import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridValue;

public class Day18 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day18s1.txt")) {
            final Map map = new Map(inputStream.getAll());
            logInfo(map);
        }
    }

    @Override
    public void part2() throws Exception {

    }

    private static class Map {
        private final ExpandingGrid<MapValue> grid;

        public Map(final String input) {
            grid = new ExpandingGrid<>();
            int x = 0;
            int y = 0;
            for (final char c : input.toCharArray()) {
                if (c == '\n') {
                    y++;
                    x = 0;
                } else {
                    grid.put(x, y, fromChar(c));
                    x++;
                }
            }
        }

        public void traverseMap() {

        }

        @Override
        public String toString() {
            return grid.toString();
        }
    }

    private static class SearchNode implements Comparable<SearchNode> {
        private final int x;
        private final int y;
        private int distanceFromOrigin;
        private SearchNode predecessor;

        public SearchNode(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getDistanceFromOrigin() {
            return distanceFromOrigin;
        }

        public void setDistanceFromOrigin(final int distanceFromOrigin) {
            this.distanceFromOrigin = distanceFromOrigin;
        }

        public SearchNode getPredecessor() {
            return predecessor;
        }

        public void setPredecessor(final SearchNode predecessor) {
            this.predecessor = predecessor;
        }

        @Override
        public int compareTo(SearchNode o) {
            return o.distanceFromOrigin - distanceFromOrigin;
        }
    }

    private static MapValue fromChar(final char c) {
        switch (c) {
            case '#':
                return BasicMapValue.WALL;
            case '.':
                return BasicMapValue.FREE;
            case '@':
                return BasicMapValue.AVATAR;
            default:
                if (Character.isLowerCase(c)) {
                    return new Key(c);
                } else {
                    return new Door(c);
                }
        }
    }

    private interface MapValue extends GridValue {
    }

    private enum BasicMapValue implements MapValue {
        WALL('#'), FREE('.'), AVATAR('@');

        private final char printValue;

        BasicMapValue(final char printValue) {
            this.printValue = printValue;
        }

        @Override
        public char getPrintValue() {
            return printValue;
        }
    }

    private static class Key implements MapValue {
        private final char symbol;

        public Key(final char symbol) {
            this.symbol = symbol;
        }

        @Override
        public char getPrintValue() {
            return symbol;
        }
    }

    private static class Door implements MapValue {
        private final char symbol;

        public Door(final char symbol) {
            this.symbol = symbol;
        }

        @Override
        public char getPrintValue() {
            return symbol;
        }
    }
}
