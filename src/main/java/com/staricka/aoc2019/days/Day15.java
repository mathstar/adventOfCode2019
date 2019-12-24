package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;
import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridValue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day15 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day15.txt")) {
            final Mapper mapper = new Mapper(inputStream.getLine());
            mapper.run();
            mapper.join();
        }
    }

    @Override
    public void part2() throws Exception {

    }

    private boolean seekAtDepth(final int depth, final String program) {
        final Queue<Integer> commandQueue = new ArrayDeque<>();
        final Queue<Integer> statusQueue = new ArrayDeque<>();
        final IntCodeProgram controller = new IntCodeProgram(program, commandQueue, statusQueue);
    }

    private boolean seekAtDepth(final int depth, final Queue<Integer> commandQueue, final Queue<Integer> statusQueue,
            final IntCodeProgram controller) {

    }

    private class MapperController {
        private final ScheduledThreadPoolExecutor executor;
        private final List<MapperWorker> workers;
        private final ExpandingGrid<MapValue> grid;

        private int depth;

        public MapperController() {
            executor = new ScheduledThreadPoolExecutor(50);
            workers = new ArrayList<>();
            grid = new ExpandingGrid<>();
            depth = 0;
        }

        public int getAllowedDepth() {
            return depth;
        }

        public MapValue getValue(final int x, final int y) {
            return grid.get(x, y);
        }

        public synchronized void submitValue(final int x, final int y, final MapValue value) {
            grid.put(x, y, value);
        }

        public synchronized void submitWorker(final MapperWorker mapperWorker) {
            workers.add(mapperWorker);
            executor.submit(mapperWorker::run);
        }

        public void run() throws Exception {
            while (true) {
                depth++;
                final List<MapperWorker> startingWorkers = new ArrayList<>(workers);
                for (final MapperWorker worker : startingWorkers) {
                    while (worker.executedDepth < depth) {
                        Thread.sleep(10);
                    }
                }

                if (workers.stream().anyMatch(MapperWorker::isFoundOxygen)) {
                    stop();
                    return;
                }
            }
        }

        private void stop() {
            executor.shutdownNow();
        }
    }

    private class MapperWorker {
        private MapperController mapperController;
        private IntCodeProgram botController;
        private int executedDepth;
        private boolean foundOxygen;
        private int x;
        private int y;

        public MapperWorker(final MapperController mapperController, final String program) {
            this.mapperController = mapperController;
            executedDepth = 0;
            foundOxygen = false;
            botController = new IntCodeProgram(program);
        }

        public int getExecutedDepth() {
            return executedDepth;
        }

        public boolean isFoundOxygen() {
            return foundOxygen;
        }

        private Integer handleInput() {

        }

        private void handleOutput(final Integer output) {
            final StatusCode statusCode = StatusCode.fromCode(output);
            logInfo(grid);
            final MovementCommand lastCommand = steps.peek();
            switch (statusCode) {
                case FREE: {
                    updatePosition(lastCommand);
                    grid.put(x, y, MapValue.FREE);
                    break;
                }
                case OXYGEN: {
                    updatePosition(lastCommand);
                    grid.put(x, y, MapValue.OXYGEN);
                    break;
                }
                case WALL: {
                    int originalX = x;
                    int originalY = y;
                    updatePosition(lastCommand);
                    grid.put(x, y, MapValue.WALL);
                    x = originalX;
                    y = originalY;
                    steps.pop();
                    break;
                }
            }
        }

        private void updatePosition(final MovementCommand command) {
            switch (command) {
                case NORTH: {
                    y--;
                    break;
                }
                case EAST: {
                    x++;
                    break;
                }
                case SOUTH: {
                    y++;
                    break;
                }
                case WEST: {
                    x--;
                    break;
                }
            }
        }

        private MapValue getNorth() {
            return mapperController.getValue(x, y - 1);
        }

        private MapValue getSouth() {
            return mapperController.getValue(x, y + 1);
        }

        private MapValue getWest() {
            return mapperController.getValue(x - 1, y);
        }

        private MapValue getEast() {
            return mapperController.getValue(x + 1, y);
        }

        public void run() {
        }
    }

    private class Mapper {
        private final IntCodeProgram controller;
        private final ExpandingGrid<MapValue> grid;
        private final Stack<MovementCommand> steps;

        private int x;
        private int y;

        private Thread controlThread;

        public Mapper(final String program) {
            controller = new IntCodeProgram(program, this::handleInput, this::handleOutput);
            grid = new ExpandingGrid<>();
            grid.put(0, 0, MapValue.FREE);
            steps = new Stack<>();
            x = 0;
            y = 0;
        }

        private Integer handleInput() {
            MovementCommand command = null;
            if (getNorth() == null) {
                command = MovementCommand.NORTH;
            } else if (getEast() == null) {
                command = MovementCommand.EAST;
            } else if (getSouth() == null) {
                command = MovementCommand.SOUTH;
            } else if (getWest() == null) {
                command = MovementCommand.WEST;
            } else if (!steps.empty()) {
                return steps.pop().reverse().getCode();
            } else {
                stop();
                return -1;
            }
            steps.push(command);
            return command.getCode();
        }

        private void handleOutput(final Integer output) {
            final StatusCode statusCode = StatusCode.fromCode(output);
            logInfo(grid);
            final MovementCommand lastCommand = steps.peek();
            switch (statusCode) {
                case FREE: {
                    updatePosition(lastCommand);
                    grid.put(x, y, MapValue.FREE);
                    break;
                }
                case OXYGEN: {
                    updatePosition(lastCommand);
                    grid.put(x, y, MapValue.OXYGEN);
                    break;
                }
                case WALL: {
                    int originalX = x;
                    int originalY = y;
                    updatePosition(lastCommand);
                    grid.put(x, y, MapValue.WALL);
                    x = originalX;
                    y = originalY;
                    steps.pop();
                    break;
                }
            }
        }

        public void run() {
            controlThread = new Thread(() -> {
                try {
                    controller.run();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            });
            controlThread.start();
        }

        public void join() throws Exception {
            //controlThread.join();
        }

        private void stop() {
            controlThread.interrupt();
            logInfo(grid);
        }

        private void updatePosition(final MovementCommand command) {
            switch (command) {
                case NORTH: {
                    y--;
                    break;
                }
                case EAST: {
                    x++;
                    break;
                }
                case SOUTH: {
                    y++;
                    break;
                }
                case WEST: {
                    x--;
                    break;
                }
            }
        }

        private MapValue getNorth() {
            return grid.get(x, y - 1);
        }

        private MapValue getSouth() {
            return grid.get(x, y + 1);
        }

        private MapValue getWest() {
            return grid.get(x - 1, y);
        }

        private MapValue getEast() {
            return grid.get(x + 1, y);
        }
    }

    private enum MovementCommand {
        NORTH(1), SOUTH(2), WEST(3), EAST(4);

        private final int code;

        MovementCommand(final int code) {
            this.code = code;
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
