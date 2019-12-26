package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;
import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridValue;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day15 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day15.txt")) {
            final GridTraverser gridTraverser = new GridTraverser(inputStream.getLine());
            logInfo("Distance to oxygen: %d", gridTraverser.findOxygen());
        }
    }

    @Override
    public void part2() throws Exception {

    }

    private class GridState {
        private final int x;
        private final int y;
        private final GridState predecessor;
        private final int distanceFromOrigin;

        private IntCodeProgram programHere;
        private Queue<Integer> commandQueue;
        private Queue<Integer> statusQueue;

        public GridState(final int x, final int y) {
            this.x = x;
            this.y = y;
            this.predecessor = null;
            this.distanceFromOrigin = 0;
        }

        public GridState(final int x, final int y, final GridState predecessor) {
            this.x = x;
            this.y = y;
            this.predecessor = predecessor;
            this.distanceFromOrigin = predecessor.distanceFromOrigin + 1;
        }

        public void setProgramHere(final IntCodeProgram programHere, final Queue<Integer> commandQueue,
                final Queue<Integer> statusQueue) {
            this.programHere = programHere;
            this.commandQueue = commandQueue;
            this.statusQueue = statusQueue;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public GridState getPredecessor() {
            return predecessor;
        }

        public int getDistanceFromOrigin() {
            return distanceFromOrigin;
        }

        public IntCodeProgram getProgramHere() {
            return programHere;
        }

        public Queue<Integer> getCommandQueue() {
            return commandQueue;
        }

        public Queue<Integer> getStatusQueue() {
            return statusQueue;
        }
    }

    private class GridTraverser {
        private final PriorityQueue<GridState> searchQueue;
        private final String program;
        private final ExpandingGrid<MapValue> grid;

        public GridTraverser(final String program) {
            this.program = program;

            searchQueue = new PriorityQueue<>(Comparator.comparing(GridState::getDistanceFromOrigin));
            grid = new ExpandingGrid<>();

            final GridState origin = new GridState(0, 0);
            final Queue<Integer> originCommandQueue = new ArrayDeque<>();
            final Queue<Integer> originStatusQueue = new ArrayDeque<>();
            final IntCodeProgram originProgram = new IntCodeProgram(program, originCommandQueue, originStatusQueue);
            origin.setProgramHere(originProgram, originCommandQueue, originStatusQueue);
            searchQueue.add(origin);
            grid.put(0, 0, MapValue.FREE);
        }

        public int findOxygen() throws Exception {
            while (true) {
                final Integer distance = step();
                if (distance != null) {
                    return distance;
                }
            }
        }

        private Integer step() throws Exception {
            final GridState state = searchQueue.poll();

            Integer oxygenDistance = null;
            for (final MovementCommand possibleCommand : MovementCommand.values()) {
                final int candidateX = possibleCommand.applyX(state.getX());
                final int candidateY = possibleCommand.applyY(state.getY());
                final Queue<Integer> commandQueue = state.getCommandQueue();
                final Queue<Integer> statusQueue = state.getStatusQueue();
                final IntCodeProgram program = state.getProgramHere();

                if (grid.get(candidateX, candidateY) == null) {
                    commandQueue.add(possibleCommand.getCode());
                    program.runUntilInput();
                    final StatusCode statusCode = StatusCode.fromCode(statusQueue.poll());

                    switch (statusCode) {
                        case WALL: {
                            grid.put(candidateX, candidateY, MapValue.WALL);
                            break;
                        }
                        case FREE: {
                            grid.put(candidateX, candidateY, MapValue.FREE);

                            final GridState candidateState = new GridState(candidateX, candidateY, state);
                            final Queue<Integer> candidateCommandQueue = new ArrayDeque<>();
                            final Queue<Integer> candidateStatusQueue = new ArrayDeque<>();
                            final IntCodeProgram candidateProgram =
                                    new IntCodeProgram(program, candidateCommandQueue, candidateStatusQueue);
                            candidateState
                                    .setProgramHere(candidateProgram, candidateCommandQueue, candidateStatusQueue);

                            commandQueue.add(possibleCommand.reverse().getCode());
                            program.runUntilInput();
                            statusQueue.remove();

                            searchQueue.add(candidateState);
                            break;
                        }
                        case OXYGEN: {
                            grid.put(candidateX, candidateY, MapValue.OXYGEN);

                            final GridState candidateState = new GridState(candidateX, candidateY, state);
                            final Queue<Integer> candidateCommandQueue = new ArrayDeque<>();
                            final Queue<Integer> candidateStatusQueue = new ArrayDeque<>();
                            final IntCodeProgram candidateProgram =
                                    new IntCodeProgram(program, candidateCommandQueue, candidateStatusQueue);
                            candidateState
                                    .setProgramHere(candidateProgram, candidateCommandQueue, candidateStatusQueue);

                            commandQueue.add(possibleCommand.reverse().getCode());
                            program.runUntilInput();
                            statusQueue.remove();

                            oxygenDistance = candidateState.getDistanceFromOrigin();

                            searchQueue.add(candidateState);
                            break;
                        }
                    }
                }
            }
            return oxygenDistance;
        }
    }

    private enum MovementCommand {
        NORTH(1, 0, -1), SOUTH(2, 0, 1), WEST(3, -1, 0), EAST(4, 1, 0);

        private final int code;
        private final int diffX;
        private final int diffY;

        MovementCommand(final int code, final int diffX, final int diffY) {
            this.code = code;
            this.diffX = diffX;
            this.diffY = diffY;
        }

        public int getCode() {
            return code;
        }

        public MovementCommand reverse() {
            switch (this) {
                case NORTH:
                    return SOUTH;
                case EAST:
                    return WEST;
                case SOUTH:
                    return NORTH;
                case WEST:
                    return EAST;
                default:
                    throw new RuntimeException("Unknown direction");
            }
        }

        public int applyX(final int initial) {
            return initial + diffX;
        }

        public int applyY(final int initial) {
            return initial + diffY;
        }
    }

    private enum StatusCode {
        WALL(0), FREE(1), OXYGEN(2);

        private static final Map<Integer, StatusCode> REVERSE_LOOKUP_MAP = Collections.unmodifiableMap(
                Arrays.stream(values()).collect(Collectors.toMap(StatusCode::getCode, Function.identity())));
        private final int code;

        StatusCode(final int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static StatusCode fromCode(final int code) {
            final StatusCode statusCode = REVERSE_LOOKUP_MAP.get(code);
            if (statusCode == null) {
                throw new RuntimeException("Unknown status code");
            }
            return statusCode;
        }
    }

    private enum MapValue implements GridValue {
        FREE('.'), WALL('#'), OXYGEN('O');

        private final char printValue;

        MapValue(final char printValue) {
            this.printValue = printValue;
        }

        @Override
        public char getPrintValue() {
            return printValue;
        }
    }
}
