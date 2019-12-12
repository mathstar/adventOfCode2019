package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.Painter;
import com.staricka.aoc2019.intcode.Painter.HullState;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

public class Day11 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day11.txt")) {
            // turns out the control program does not handle multithreaded execution
            final Painter painter = new Painter(inputStream.getLine());
            painter.run();
            logInfo("Painted: %d", painter.getPaintedCount());
        }
    }

    @Override
    public void part2() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day11.txt")) {
            final Painter painter = new Painter(inputStream.getLine());
            painter.forcePaint(0, 0, HullState.PAINTED_WHITE);
            painter.run();
            logInfo("Painted: %d", painter.getPaintedCount());
        }
    }
}
