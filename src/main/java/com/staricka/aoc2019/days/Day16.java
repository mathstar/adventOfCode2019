package com.staricka.aoc2019.days;

import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day16 extends AocDay {
    private static final List<Integer> basePattern = Arrays.asList(0, 1, 0, -1);

    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day16.txt")) {
            List<Integer> signal = parseSignalString(inputStream.getLine());
            for (int i = 0; i < 100; i++) {
                signal = applyPhase(signal);
            }
            logInfo("After 100: %s", joinSignal(signal));
        }
    }

    @Override
    public void part2() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day16s2.txt")) {
            final String signalString = inputStream.getLine();
            final int messageOffset = Integer.parseInt(signalString.substring(0, 7));
            List<Integer> signal = repeatSignal(parseSignalString(signalString), 10000);
            signal = signal.subList(messageOffset - 2, signal.size());
            for (int i = 0; i < 100; i++) {
                signal = applyPhaseTruncated(signal, messageOffset - 2);
            }
            final StringBuilder outputBuilder = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                outputBuilder.append(signal.get((messageOffset + i) % signal.size()));
            }
            logInfo("Output: %s", outputBuilder);
        }
    }

    @Override
    public void test() throws Exception {
        final List<Integer> initial = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        final List<Integer> after1 = applyPhase(initial);
        final List<Integer> after2 = applyPhase(after1);
        logInfo("Input: %s\nAfter 1: %s\nAfter 2: %s", joinSignal(initial), joinSignal(after1), joinSignal(after2));
    }

    private List<Integer> parseSignalString(final String signal) {
        return Arrays.stream(signal.split("")).map(Integer::parseInt).collect(Collectors.toList());
    }

    private String joinSignal(final List<Integer> signal) {
        return signal.stream().map(String::valueOf).collect(Collectors.joining());
    }

    private List<Integer> repeatSignal(final List<Integer> signal, final int multiplier) {
        final List<Integer> output = new ArrayList<>(signal.size() * multiplier);
        for (int i = 0; i < multiplier; i++) {
            for (final Integer j : signal) {
                output.add(j);
            }
        }
        return output;
    }

    private List<Integer> multiplyPattern(final int multiple) {
        final List<Integer> multipliedPattern = new ArrayList<>(basePattern.size() * multiple);
        for (final Integer i : basePattern) {
            for (int j = 0; j < multiple; j++) {
                multipliedPattern.add(i);
            }
        }
        return multipliedPattern;
    }

    private int getMultiple(final List<Integer> pattern, final int index) {
        return pattern.get((index + 1) % pattern.size());
    }

    private int getMultiple(final int digit, final int index) {
        return basePattern.get((index + 1) % (basePattern.size() * digit) / digit);
    }

    private List<Integer> applyPhase(final List<Integer> input) {
        final List<Integer> output = new ArrayList<>(input.size());
        for (int i = 1; i <= input.size(); i++) {
            //final List<Integer> pattern = multiplyPattern(i);
            int sum = 0;
            for (int j = 0; j < input.size(); j++) {
                sum += input.get(j) * getMultiple(i, j);
            }
            final String sumString = String.valueOf(sum);
            output.add(Integer.valueOf(sumString.substring(sumString.length() - 1)));
        }
        return output;
    }

    private List<Integer> applyPhaseTruncated(final List<Integer> input, final int truncatedBy) {
        final List<Integer> output = new ArrayList<>(input.size());
        for (int i = 1; i <= input.size(); i++) {
            int sum = 0;
            for (int j = 0; j < input.size(); j++) {
                sum += input.get(j) * getMultiple(i + truncatedBy, j + truncatedBy);
            }
            final String sumString = String.valueOf(sum);
            output.add(Integer.valueOf(sumString.substring(sumString.length() - 1)));
        }
        return output;
    }
}
