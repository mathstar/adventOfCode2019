package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

public class Day2 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (AocInputStream inputStream = new AocInputStream("day2a.txt")) {
            String input = inputStream.getAll();
            IntCodeProgram program = new IntCodeProgram(input);
            program.setValue(1, 12);
            program.setValue(2, 2);
            program.run();
            logInfo("Position 0: %d", program.getValue(0));
        }
    }

    @Override
    public void part2() throws Exception {
        try (AocInputStream inputStream = new AocInputStream("day2a.txt")) {
            String input = inputStream.getAll();
            for(int noun = 0; noun <= 99; noun++) {
                for(int verb = 0; verb <= 99; verb++) {
                    IntCodeProgram program = new IntCodeProgram(input);
                    program.setValue(1, noun);
                    program.setValue(2, verb);
                    program.run();
                    if(program.getValue(0) == 19690720) {
                        logInfo("Noun %d; Verb %d", noun, verb);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void test() throws Exception {
        try (AocInputStream inputStream = new AocInputStream("day2s.txt")) {
            String input = inputStream.getAll();
            IntCodeProgram program = new IntCodeProgram(input);
            program.run();
            program.printMemory();
        }
    }
}
