package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

import java.util.ArrayDeque;
import java.util.Queue;

public class Day19 extends AocDay {
    @Override
    public void part1() throws Exception {
        try(final AocInputStream inputStream = new AocInputStream("day19.txt")) {
            final String program = inputStream.getLine();
            int affected = 0;
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
                    }
                }
            }
            logInfo("Affected: %d", affected);
        }
    }

    @Override
    public void part2() throws Exception {

    }
}
