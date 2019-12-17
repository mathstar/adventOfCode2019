package com.staricka.aoc2019.days;

import com.staricka.aoc2019.intcode.IntCodeProgram;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day7 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day7a.txt")) {
            iteratePhaseSettings(inputStream.getLine());
        }
    }

    @Override
    public void part2() throws Exception {
        try(final AocInputStream inputStream = new AocInputStream("day7a.txt")) {
            iteratePhaseSettingsLoop(inputStream.getLine());
        }
    }

    @Override
    public void test() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day7s1.txt")) {
            iteratePhaseSettings(inputStream.getLine());
        }
    }

    private void iteratePhaseSettings(final String program) {
        AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);
        getPhaseStream().forEach(phase0 -> {
            getPhaseStream(phase0).forEach(phase1 -> {
                getPhaseStream(phase0, phase1).forEach(phase2 -> {
                    getPhaseStream(phase0, phase1, phase2).forEach(phase3 -> {
                        getPhaseStream(phase0, phase1, phase2, phase3).forEach(phase4 -> {
                            try {
                                logInfo("Testing phase: %d %d %d %d %d", phase0, phase1, phase2, phase3, phase4);
                                int output = executeAmplifiers(program, phase0, phase1, phase2, phase3, phase4);
                                logInfo("Output: %d", output);
                                max.getAndUpdate(current -> Integer.max(current, output));
                            } catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    });
                });
            });
        });
        logInfo("Max output: %d", max.get());
    }

    private void iteratePhaseSettingsLoop(final String program) {
        AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);
        getPhaseStreamLoop().forEach(phase0 -> {
            getPhaseStreamLoop(phase0).forEach(phase1 -> {
                getPhaseStreamLoop(phase0, phase1).forEach(phase2 -> {
                    getPhaseStreamLoop(phase0, phase1, phase2).forEach(phase3 -> {
                        getPhaseStreamLoop(phase0, phase1, phase2, phase3).forEach(phase4 -> {
                            try {
                                logInfo("Testing phase: %d %d %d %d %d", phase0, phase1, phase2, phase3, phase4);
                                int output = executeAmplifiersLoop(program, phase0, phase1, phase2, phase3, phase4);
                                logInfo("Output: %d", output);
                                max.getAndUpdate(current -> Integer.max(current, output));
                            } catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    });
                });
            });
        });
        logInfo("Max output: %d", max.get());
    }

    private IntStream getPhaseStream(final int... phases) {
        final Set<Integer> used = Arrays.stream(phases).boxed().collect(Collectors.toSet());
        return IntStream.range(0, 5).filter(i -> !used.contains(i));
    }

    private IntStream getPhaseStreamLoop(final int... phases) {
        final Set<Integer> used = Arrays.stream(phases).boxed().collect(Collectors.toSet());
        return IntStream.range(5, 10).filter(i -> !used.contains(i));
    }

    private int executeAmplifiers(final String program, final int phase0, final int phase1, final int phase2,
            final int phase3, final int phase4) throws Exception {
        final StringWriter output0 = new StringWriter();
        final IntCodeProgram program0 = new IntCodeProgram(program, getInput(phase0, 0), output0);
        program0.run();
        final StringWriter output1 = new StringWriter();
        final IntCodeProgram program1 = new IntCodeProgram(program, getInput(phase1, output0), output1);
        program1.run();
        final StringWriter output2 = new StringWriter();
        final IntCodeProgram program2 = new IntCodeProgram(program, getInput(phase2, output1), output2);
        program2.run();
        final StringWriter output3 = new StringWriter();
        final IntCodeProgram program3 = new IntCodeProgram(program, getInput(phase3, output2), output3);
        program3.run();
        final StringWriter output4 = new StringWriter();
        final IntCodeProgram program4 = new IntCodeProgram(program, getInput(phase4, output3), output4);
        program4.run();
        return readOutput(output4);
    }

    private int executeAmplifiersLoop(final String program, final int phase0, final int phase1, final int phase2,
            final int phase3, final int phase4) throws Exception {
        PipedInputStream inputStream0 = new PipedInputStream();
        PipedInputStream inputStream1 = new PipedInputStream();
        PipedInputStream inputStream2 = new PipedInputStream();
        PipedInputStream inputStream3 = new PipedInputStream();
        PipedInputStream inputStream4 = new PipedInputStream();
        PipedOutputStream outputStream0 = new PipedOutputStream(inputStream1);
        PipedOutputStream outputStream1 = new PipedOutputStream(inputStream2);
        PipedOutputStream outputStream2 = new PipedOutputStream(inputStream3);
        PipedOutputStream outputStream3 = new PipedOutputStream(inputStream4);
        PipedOutputStream outputStream4 = new PipedOutputStream(inputStream0);

        BufferedWriter writer0 = new BufferedWriter(new OutputStreamWriter(outputStream4));
        BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(outputStream0));
        BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(outputStream1));
        BufferedWriter writer3 = new BufferedWriter(new OutputStreamWriter(outputStream2));
        BufferedWriter writer4 = new BufferedWriter(new OutputStreamWriter(outputStream3));

        StringWriter outputWriter = new StringWriter();

        writer0.write(phase0 + "\n");
        writer0.write("0\n");
        writer1.write(phase1 + "\n");
        writer2.write(phase2 + "\n");
        writer3.write(phase3 + "\n");
        writer4.write(phase4 + "\n");
        writer0.flush();
        writer1.flush();
        writer2.flush();
        writer3.flush();
        writer4.flush();

        final IntCodeProgram program0 = new IntCodeProgram(program, inputStream0, outputStream0);
        final IntCodeProgram program1 = new IntCodeProgram(program, inputStream1, outputStream1);
        final IntCodeProgram program2 = new IntCodeProgram(program, inputStream2, outputStream2);
        final IntCodeProgram program3 = new IntCodeProgram(program, inputStream3, outputStream3);
        final IntCodeProgram program4 = new IntCodeProgram(program, inputStream4, outputStream4, outputWriter);

        List<FutureTask<Void>> tasks = new ArrayList<>(5);
        tasks.add(program0.runAsync());
        tasks.add(program1.runAsync());
        tasks.add(program2.runAsync());
        tasks.add(program3.runAsync());
        tasks.add(program4.runAsync());

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
        tasks.forEach(executor::execute);

        tasks.stream().forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        executor.shutdown();

        outputWriter.flush();
        return Integer.parseInt(outputWriter.toString().split("\n")[0]);
    }

    private Reader getInput(final int phase, final int input) {
        final String inputString = phase + "\n" + input + "\n";
        return new StringReader(inputString);
    }

    private Reader getInput(final int phase, final StringWriter inputWriter) {
        String input = inputWriter.toString();
        input = input.split("\n")[0];
        final String inputString = phase + "\n" + input + "\n";
        return new StringReader(inputString);
    }

    private int readOutput(final StringWriter outputWriter) {
        String ouput = outputWriter.toString();
        ouput = ouput.split("\n")[0];
        return Integer.parseInt(ouput);
    }
}
