package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

import java.io.StringReader;

public class Day9 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day9.txt")) {
            IntCodeProgram program = new IntCodeProgram(inputStream.getLine(), new StringReader("1\n"), System.out);
            program.run();
        }
    }

    @Override
    public void part2() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day9.txt")) {
            IntCodeProgram program = new IntCodeProgram(inputStream.getLine(), new StringReader("2\n"), System.out);
            program.run();
        }
    }
}
