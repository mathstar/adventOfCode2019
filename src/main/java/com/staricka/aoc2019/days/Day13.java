package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;
import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridTile;
import com.staricka.aoc2019.util.data.GridValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day13 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day13.txt")) {
            Game game = new Game(inputStream.getLine());
            game.run();
            logInfo("Number of block tiles: %d",
                    game.gameGrid.getFilledTiles().stream().filter(t -> t.getValue() == Tile.BLOCK).count());
        }
    }

    @Override
    public void part2() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day13.txt")) {
            Game game = new Game(inputStream.getLine());
            game.runWithCoins();
        }
    }

    public class Game {
        private final IntCodeProgram controller;
        private final ExpandingGrid<Tile> gameGrid;
        private final Queue<Integer> outputStack;
        private final BufferedReader inputReader;

        public Game(final String controlProgram) {
            this.controller = new IntCodeProgram(controlProgram, this::getInput, this::handleOutput);
            gameGrid = new ExpandingGrid<>();
            outputStack = new LinkedList<>();
            inputReader = new BufferedReader(new InputStreamReader(System.in));
        }

        public void run() throws Exception {
            controller.run();
        }

        public void runWithCoins() throws Exception {
            controller.setValue(0, 2);
            controller.run();
        }

        private void handleOutput(final int output) {
            outputStack.add(output);
            if (outputStack.size() >= 3) {
                if (outputStack.peek() == -1) {
                    outputStack.remove();
                    outputStack.remove();
                    logInfo("Score: %d", outputStack.remove());
                } else {
                    gameGrid.put(outputStack.remove(), outputStack.remove(), Tile.lookup(outputStack.remove()));
                }
            }
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        private int getInput() {
            try {
                System.out.println(gameGrid.toString());
                /* Manual play frame-at-a-time:
                final String line = inputReader.readLine();
                final char c = line.length() > 0 ? line.charAt(0) : 's';
                switch (c) {
                    case 'a':
                        return -1;
                    case 'd':
                        return 1;
                    default:
                        return 0;
                }
                 */
                int paddleX = gameGrid.getFilledTiles().stream().filter(v -> v.getValue() == Tile.HORIZONTAL_PADDLE)
                        .mapToInt(GridTile::getX).findFirst().getAsInt();
                int ballX = gameGrid.getFilledTiles().stream().filter(v -> v.getValue() == Tile.BALL)
                        .mapToInt(GridTile::getX).findFirst().getAsInt();
                if (paddleX < ballX) {
                    return 1;
                } else if (paddleX > ballX) {
                    return -1;
                }
                return 0;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private enum Tile implements GridValue {
        EMPTY(0, ' '), WALL(1, '█'), BLOCK(2, '#'), HORIZONTAL_PADDLE(3, '-'), BALL(4, '•');

        private final int id;
        private final char symbol;
        private static final Map<Integer, Tile> lookupMap =
                Arrays.stream(values()).collect(Collectors.toMap(Tile::getId, Function.identity()));

        Tile(final int id, final char symbol) {
            this.id = id;
            this.symbol = symbol;
        }

        @Override
        public char getPrintValue() {
            return symbol;
        }

        public int getId() {
            return id;
        }

        public static Tile lookup(final int id) {
            return lookupMap.get(id);
        }
    }
}
