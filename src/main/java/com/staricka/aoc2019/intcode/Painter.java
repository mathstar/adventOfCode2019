package com.staricka.aoc2019.intcode;

import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridValue;

public class Painter {
    private final ExpandingGrid<HullState> hullState;
    private int x = 0;
    private int y = 0;
    private Heading heading = Heading.UP;
    private final IntCodeProgram program;

    public Painter(final String program) throws Exception {
        hullState = new ExpandingGrid<>();

        this.program = new IntCodeProgram(program, this::getCameraState, this::handleControllerOutput);
    }

    public void run() throws Exception {
        program.run();
        AocDay.logInfo(hullState::toString);
    }

    public int getPaintedCount() {
        return hullState.getFilledTiles().size();
    }

    public void forcePaint(final int x, final int y, final HullState paint) {
        hullState.put(x, y, paint);
    }

    private Integer colorOutput = null;

    private void handleControllerOutput(final int output) {
        if (colorOutput == null) {
            colorOutput = output;
        } else {
            step(colorOutput, output);
            colorOutput = null;
        }
    }

    private int getCameraState() {
        final HullState localHullState = hullState.get(x, y);
        if (localHullState == null) {
            return 0;
        }
        switch (localHullState) {
            case PAINTED_BLACK:
                return 0;
            case PAINTED_WHITE:
                return 1;
        }
        throw new RuntimeException("Unknown camera state");
    }

    private void step(final int colorSignal, final int turnSignal) {
        AocDay.logInfo("CS %d TS %d", colorSignal, turnSignal);
        switch (colorSignal) {
            case 0: {
                hullState.put(x, y, HullState.PAINTED_BLACK);
                break;
            }
            case 1: {
                hullState.put(x, y, HullState.PAINTED_WHITE);
                break;
            }
        }
        switch (turnSignal) {
            case 0: {
                heading = heading.left90();
                break;
            }
            case 1: {
                heading = heading.right90();
                break;
            }
        }
        switch (heading) {
            case UP: {
                y--;
                break;
            }
            case RIGHT: {
                x++;
                break;
            }
            case DOWN: {
                y++;
                break;
            }
            case LEFT: {
                x--;
                break;
            }
        }
    }

    public enum HullState implements GridValue {
        PAINTED_WHITE('█'), PAINTED_BLACK(' ');

        final char printValue;

        HullState(final char printValue) {
            this.printValue = printValue;
        }

        @Override
        public char getPrintValue() {
            return printValue;
        }
    }

    private enum Heading {
        UP, RIGHT, DOWN, LEFT;

        public Heading left90() {
            switch (this) {
                case UP:
                    return LEFT;
                case RIGHT:
                    return UP;
                case DOWN:
                    return RIGHT;
                case LEFT:
                    return DOWN;
            }
            throw new RuntimeException("Unknown heading");
        }

        public Heading right90() {
            switch (this) {
                case UP:
                    return RIGHT;
                case RIGHT:
                    return DOWN;
                case DOWN:
                    return LEFT;
                case LEFT:
                    return UP;
            }
            throw new RuntimeException("Unknown heading");
        }
    }
}
