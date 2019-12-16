package com.staricka.aoc2019.days;

import com.staricka.aoc2019.util.AocDay;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day4 extends AocDay {
    private final static Pattern duplicatePattern =
            Pattern.compile("(00)|(11)|(22)|(33)|(44)|(55)|(66)|(77)|(88)|(99)");
    private final static Pattern duplicateAmendedPattern = Pattern.compile(
            "((^|[^0])00($|[^0]))|((^|[^1])11($|[^1]))|((^|[^2])22($|[^2]))|((^|[^3])33($|[^3]))|((^|[^4])44($|[^4]))|((^|[^5])55($|[^5]))|((^|[^6])66($|[^6]))|((^|[^7])77($|[^7]))|((^|[^8])88($|[^8]))|((^|[^9])99($|[^9]))");

    @Override
    public void part1() throws Exception {
        final int rangeStart = 178416;
        final int rangeEnd = 676461;
        final AtomicInteger validCount = new AtomicInteger(0);
        final long candidates = IntStream.range(rangeStart, rangeEnd).parallel().filter(this::duplicateRule)
                .filter(this::increasingRule).count();
        logInfo("Candidates: %d", candidates);
    }

    @Override
    public void part2() throws Exception {
        final int rangeStart = 178416;
        final int rangeEnd = 676461;
        final AtomicInteger validCount = new AtomicInteger(0);
        final long candidates = IntStream.range(rangeStart, rangeEnd).parallel().filter(this::duplicateAmendedRule)
                .filter(this::increasingRule).count();
        logInfo("Candidates: %d", candidates);
    }

    private boolean duplicateRule(final int candidate) {
        final String candidateString = String.valueOf(candidate);
        return duplicatePattern.asPredicate().test(candidateString);
    }

    private boolean duplicateAmendedRule(final int candidate) {
        final String candidateString = String.valueOf(candidate);
        return duplicateAmendedPattern.asPredicate().test(candidateString);
    }

    private boolean increasingRule(final int candidate) {
        final String candidateString = String.valueOf(candidate);
        char[] chars = candidateString.toCharArray();
        char previous = 0;
        for (char c : chars) {
            if (c < previous) {
                return false;
            }
            previous = c;
        }
        return true;
    }
}
