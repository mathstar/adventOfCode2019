package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;
import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridTile;
import com.staricka.aoc2019.util.data.GridValue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
        try (final AocInputStream inputStream = new AocInputStream("day17.txt")) {
            final String program = inputStream.getLine();

            final StringBuilder mapper = new StringBuilder();
            final IntCodeProgram asciiProgram =
                    new IntCodeProgram(program, () -> -1, out -> mapper.append((char) (int) out));
            asciiProgram.run();
            final View view = new View(mapper.toString());
            List<String> instructions = view.generatePath();

            //            final List<String> sequenceA = findLongestRepeatingSection(instructions);
            //            instructions = substituteSequences(instructions, sequenceA, "A");
            //            logInfo(sequenceA);
            //            logInfo(instructions);
            //
            //            final List<String> sequenceB = findLongestRepeatingSection(instructions);
            //            instructions = substituteSequences(instructions, sequenceB, "B");
            //            logInfo(sequenceB);
            //            logInfo(instructions);
            //
            //            final List<String> sequenceC = findShortestRemaining(instructions);
            //            instructions = substituteSequences(instructions, sequenceC, "C");
            //            logInfo(sequenceC);
            //            logInfo(instructions);

            // cheaty hard-coded sequences from manual inspection
            final List<String> sequenceA = Arrays.asList("R", "8", "L", "4", "R", "4", "R", "10", "R", "8");
            final List<String> sequenceB = Arrays.asList("L", "12", "L", "12", "R", "8", "R", "8");
            final List<String> sequenceC = Arrays.asList("R", "10", "R", "4", "R", "4");

            instructions = substituteSequences(instructions, sequenceA, "A");
            instructions = substituteSequences(instructions, sequenceB, "B");
            instructions = substituteSequences(instructions, sequenceC, "C");

            final List<String> movementRoutine = new ArrayList<>();
            for (final String instruction : instructions) {
                if (instruction.startsWith("A")) {
                    movementRoutine.add("A");
                } else if (instruction.startsWith("B")) {
                    movementRoutine.add("B");
                } else if (instruction.startsWith("C")) {
                    movementRoutine.add("C");
                } else {
                    throw new RuntimeException("Failed to reduce to three sequences");
                }
            }

            final Queue<Integer> inputQueue = new ArrayDeque<>();
            final Queue<Integer> outputQueue = new ArrayDeque<>();
            final IntCodeProgram botProgram = new IntCodeProgram(program, inputQueue, outputQueue);
            botProgram.setValue(0, 2);

            for (final char c : movementRoutine.stream().collect(Collectors.joining(",")).toCharArray()) {
                inputQueue.add((int) c);
            }
            inputQueue.add((int) '\n');

            for (final char c : sequenceA.stream().collect(Collectors.joining(",")).toCharArray()) {
                inputQueue.add((int) c);
            }
            inputQueue.add((int) '\n');

            for (final char c : sequenceB.stream().collect(Collectors.joining(",")).toCharArray()) {
                inputQueue.add((int) c);
            }
            inputQueue.add((int) '\n');

            for (final char c : sequenceC.stream().collect(Collectors.joining(",")).toCharArray()) {
                inputQueue.add((int) c);
            }
            inputQueue.add((int) '\n');

            inputQueue.add((int) 'n');
            inputQueue.add((int) '\n');

            botProgram.run();
            //logInfo(outputQueueToString(outputQueue));
            logInfo("Dust collected: %s", getFinalValue(outputQueue));
        }
    }

    private String outputQueueToString(final Queue<Integer> output) {
        final StringBuilder stringBuilder = new StringBuilder();
        while (!output.isEmpty()) {
            stringBuilder.append((char) (int) output.remove());
        }
        return stringBuilder.toString();
    }

    private int getFinalValue(final Queue<Integer> output) {
        int last = -1;
        while (!output.isEmpty()) {
            last = output.remove();
        }
        return last;
    }

    private List<String> substituteSequences(final List<String> input, final List<String> sequence,
            final String replacement) {
        final List<String> substituted = new ArrayList<>();
        int counter = 0;
        for (int index = 0; index < input.size(); index++) {
            if (index + sequence.size() - 1 < input.size() && sequence
                    .equals(input.subList(index, index + sequence.size()))) {
                substituted.add(replacement + counter++);
                index = index + sequence.size() - 1;
            } else {
                substituted.add(input.get(index));
            }
        }
        return substituted;
    }

    private List<String> findLongestRepeatingSection(final List<String> input) {
        List<String> longest = null;
        for (int length = 1; length < input.size(); length++) {
            boolean foundLonger = false;
            for (int start = 0; start < input.size() - (length - 1); start++) {
                final List<String> substring = input.subList(start, start + length);
                int count = 0;
                for (int seekStart = 0; seekStart < input.size() - (length - 1); seekStart++) {
                    if (substring.equals(input.subList(seekStart, seekStart + length))) {
                        count++;
                        seekStart = seekStart + length - 1;
                    }
                }
                if (count > 1 && !foundLonger) {
                    foundLonger = true;
                    longest = substring;
                }
            }
            if (!foundLonger) {
                break;
            }
        }
        return longest;
    }

    private List<String> findShortestRemaining(final List<String> input) {
        Integer shortestStart = null;
        Integer shortestEnd = null;
        Integer currentStart = null;
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).startsWith("A") || input.get(i).startsWith("B")) {
                if (currentStart != null) {
                    int currentLength = i - currentStart;
                    if (shortestEnd == null || currentLength < shortestEnd - shortestStart) {
                        shortestStart = currentStart;
                        shortestEnd = i;
                    }
                }
            } else if (currentStart == null) {
                currentStart = i;
            }
        }
        return input.subList(shortestStart, shortestEnd);
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

        public List<String> generatePath() {
            GridTile<MapValue> bot = grid.getFilledTiles().stream()
                    .filter(t -> t.getValue() == MapValue.ROBOT_UP || t.getValue() == MapValue.ROBOT_DOWN
                            || t.getValue() == MapValue.ROBOT_LEFT || t.getValue() == MapValue.ROBOT_RIGHT).findAny()
                    .get();
            final List<String> instructions = new ArrayList<>();

            while (true) {
                int ahead = 0;
                int x = bot.getValue().updateX(bot.getX());
                int y = bot.getValue().updateY(bot.getY());
                while (grid.get(x, y) == MapValue.SCAFFOLD) {
                    ahead++;
                    x = bot.getValue().updateX(x);
                    y = bot.getValue().updateY(y);
                }

                if (ahead > 0) {
                    instructions.add(String.valueOf(ahead + 1));
                }

                x = bot.getValue().reverseX(x);
                y = bot.getValue().reverseY(y);
                bot = new GridTile<>(x, y, bot.getValue());

                final Turn turn = findTurn(bot);
                if (turn == null) {
                    break;
                }
                instructions.add(turn.name());
                final MapValue turnedBot = bot.getValue().rotateRobot(turn);
                bot = new GridTile<>(turnedBot.updateX(x), turnedBot.updateY(y), turnedBot);
            }

            return instructions;
        }

        private Turn findTurn(final GridTile<MapValue> bot) {
            final int x = bot.getX();
            final int y = bot.getY();

            final MapValue leftTurn = bot.getValue().rotateRobot(Turn.L);
            final MapValue aheadLeft = grid.get(leftTurn.updateX(x), leftTurn.updateY(y));
            if (aheadLeft == MapValue.SCAFFOLD) {
                return Turn.L;
            }

            final MapValue rightTurn = bot.getValue().rotateRobot(Turn.R);
            final MapValue aheadRight = grid.get(rightTurn.updateX(x), rightTurn.updateY(y));
            if (aheadRight == MapValue.SCAFFOLD) {
                return Turn.R;
            }
            return null;
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

        public MapValue rotateRobot(final Turn turn) {
            switch (this) {
                case ROBOT_UP:
                    switch (turn) {
                        case L:
                            return ROBOT_LEFT;
                        case R:
                            return ROBOT_RIGHT;
                    }
                case ROBOT_DOWN:
                    switch (turn) {
                        case L:
                            return ROBOT_RIGHT;
                        case R:
                            return ROBOT_LEFT;
                    }
                case ROBOT_LEFT:
                    switch (turn) {
                        case L:
                            return ROBOT_DOWN;
                        case R:
                            return ROBOT_UP;
                    }
                case ROBOT_RIGHT:
                    switch (turn) {
                        case L:
                            return ROBOT_UP;
                        case R:
                            return ROBOT_DOWN;
                    }
            }
            throw new RuntimeException("Invalid turn");
        }

        public int updateX(final int initial) {
            switch (this) {
                case ROBOT_LEFT:
                    return initial - 1;
                case ROBOT_RIGHT:
                    return initial + 1;
                case ROBOT_UP:
                case ROBOT_DOWN:
                    return initial;
            }
            throw new RuntimeException("Invalid value");
        }

        public int updateY(final int initial) {
            switch (this) {
                case ROBOT_UP:
                    return initial - 1;
                case ROBOT_DOWN:
                    return initial + 1;
                case ROBOT_LEFT:
                case ROBOT_RIGHT:
                    return initial;
            }
            throw new RuntimeException("Invalid value");
        }

        public int reverseX(final int initial) {
            switch (this) {
                case ROBOT_LEFT:
                    return initial + 1;
                case ROBOT_RIGHT:
                    return initial - 1;
                case ROBOT_UP:
                case ROBOT_DOWN:
                    return initial;
            }
            throw new RuntimeException("Invalid value");
        }

        public int reverseY(final int initial) {
            switch (this) {
                case ROBOT_UP:
                    return initial + 1;
                case ROBOT_DOWN:
                    return initial - 1;
                case ROBOT_LEFT:
                case ROBOT_RIGHT:
                    return initial;
            }
            throw new RuntimeException("Invalid value");
        }

        public static MapValue reverseLookup(final char character) {
            return REVERSE_LOOKUP_MAP.get(character);
        }
    }

    private enum Turn {
        L, R;
    }
}
