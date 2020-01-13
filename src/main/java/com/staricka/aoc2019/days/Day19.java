package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;
import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridValue;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Day19 extends AocDay {
    @Override
    public void part1() throws Exception {
        try(final AocInputStream inputStream = new AocInputStream("day19.txt")) {
            final String program = inputStream.getLine();
            int affected = 0;
            final ExpandingGrid<GridValue> grid = new ExpandingGrid<>();
            for(int x = 0; x < 50; x++) {
                for (int y = 0; y < 50; y++) {
                    final Queue<Integer> input = new ArrayDeque<>();
                    input.add(x);
                    input.add(y);
                    final Queue<Integer> output = new ArrayDeque<>();
                    final IntCodeProgram intCodeProgram = new IntCodeProgram(program, input, output);
                    intCodeProgram.run();
                    final Integer status = output.poll();
                    if(status != null && status == 1) {
                        affected++;
                        grid.put(x,y,new BeamPoint());
                    }
                }
            }
            logInfo("Affected: %d", affected);
            logInfo(grid);
        }
    }

    private class BeamPoint implements GridValue {
        @Override
        public char getPrintValue() {
            return '#';
        }
    }

    @Override
    public void part2() throws Exception {
        try(final AocInputStream inputStream = new AocInputStream("day19.txt")) {
            final String program = inputStream.getLine();
            final Map<Integer, BeamLayer> layers = new HashMap<>();
            final ExpandingGrid<GridValue> grid = new ExpandingGrid<>();
            for(int x = 900; true; x++) {
                Integer min = null;
                Integer max;
                for(int y = 0; true; y++) {
                    final Queue<Integer> input = new ArrayDeque<>();
                    input.add(x);
                    input.add(y);
                    final Queue<Integer> output = new ArrayDeque<>();
                    final IntCodeProgram intCodeProgram = new IntCodeProgram(program, input, output);
                    intCodeProgram.run();
                    final Integer status = output.poll();
                    if(status != null && status == 0) {
                        if(min != null) {
                            max = y - 1;
                            break;
                        } else if (y > 2 * x) {
                            min = -1;
                            max = -1;
                            break;
                        }
                    } else if (status != null && status == 1) {
                        grid.put(x, y, new BeamPoint());
                        if(min == null) {
                            min = y;
                        }
                    }
                }
                layers.put(x, new BeamLayer(min, max));
                if(max - min >= 99) {
                    if (x >= 999) {
                        boolean found = true;
                        for (int x1 = x - 99; x1 < x; x1++) {
                            BeamLayer layer = layers.get(x1);
                            if (Math.min(layer.max, max) - Math.max(layer.min, min) < 99) {
                                found = false;
                                break;
                            }
                        }
                        if(found) {
                            int resultX = x - 99;
                            long resultY = Math.max(layers.get(resultX).min, min);
                            logInfo("%d", (long)resultX * 10000 + resultY);
                            return;
                        }
                    }
                }
            }
        }
    }

    private static class BeamLayer {
        final int min;
        final int max;

        public BeamLayer(final int min, final int max) {
            this.min = min;
            this.max = max;
        }
    }
}
