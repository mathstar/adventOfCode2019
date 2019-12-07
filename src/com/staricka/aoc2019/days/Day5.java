package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

public class Day5 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (AocInputStream inputStream = new AocInputStream("day5a.txt")){
            String program = inputStream.getAll();
            String input = "1\n";
            IntCodeProgram intCodeProgram = new IntCodeProgram(program, new StringReader(input), System.out);
            intCodeProgram.run();
        }
    }

    @Override
    public void part2() throws Exception {
        try (AocInputStream inputStream = new AocInputStream("day5a.txt")){
            String program = inputStream.getAll();
            String input = "5\n";
            IntCodeProgram intCodeProgram = new IntCodeProgram(program, new StringReader(input), System.out);
            intCodeProgram.run();
        }
    }

    @Override
    public void test() throws Exception {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String program = inputReader.readLine();
        IntCodeProgram intCodeProgram = new IntCodeProgram(program, System.in, System.out);
        intCodeProgram.run();
    }
}
