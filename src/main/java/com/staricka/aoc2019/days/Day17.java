package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;
import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day17 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day17.txt")) {
            final StringBuilder mapper = new StringBuilder();
            final IntCodeProgram asciiProgram =
                    new IntCodeProgram(inputStream.getLine(), () -> -1, out -> mapper.append((char) (int) out));
            asciiProgram.run();
            logInfo(mapper.toString());
            logInfo("Alignment parameter sum: %d", new View(mapper.toString()).getAlignmentParameterSum());
        }
    }

    @Override
    public void part2() throws Exception {

    }

    private class View {
        private final ExpandingGrid<MapValue> grid;

        public View(final String asciiView) {
            grid = new ExpandingGrid<>();

            int y = 0;
            for (final String line : asciiView.split("\n")) {
                int x = 0;
                for (char c : line.toCharArray()) {
                    grid.put(x, y, MapValue.reverseLookup(c));
                    x++;
                }
                y++;
            }
        }

        public int getAlignmentParameterSum() {
            return grid.getFilledTiles().stream().filter(t -> t.getValue() == MapValue.SCAFFOLD).filter(t -> {
                final int x = t.getX();
                final int y = t.getY();
                return Stream.of(grid.get(x - 1, y), grid.get(x + 1, y), grid.get(x, y - 1), grid.get(x, y + 1))
                        .allMatch(MapValue.SCAFFOLD::equals);
            }).mapToInt(t -> t.getX() * t.getY()).sum();
        }
    }

    private enum MapValue implements GridValue {
        SCAFFOLD('#'), OPEN('.'), ROBOT_UP('^'), ROBOT_DOWN('v'), ROBOT_LEFT('<'), ROBOT_RIGHT('>'), ROBOT_LOST_FOREVER(
                'X');

        private static final Map<Character, MapValue> REVERSE_LOOKUP_MAP = Collections.unmodifiableMap(
                Arrays.stream(values()).collect(Collectors.toMap(MapValue::getPrintValue, Function.identity())));

        private final char printValue;

        MapValue(final char printValue) {
            this.printValue = printValue;
        }

        @Override
        public char getPrintValue() {
            return printValue;
        }

        public static MapValue reverseLookup(final char character) {
            return REVERSE_LOOKUP_MAP.get(character);
        }
    }
}
