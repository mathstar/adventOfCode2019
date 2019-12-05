package com.staricka.aoc2019.days;

import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

public class Day1 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (AocInputStream inputStream = new AocInputStream("day1a.txt")) {
            int fuelRequired = inputStream.lines().mapToInt(Integer::parseInt).map(this::computeFuelRequired).sum();
            logInfo("Fuel required: %d", fuelRequired);
        }
    }

    @Override
    public void part2() throws Exception {
        try (AocInputStream inputStream = new AocInputStream("day1a.txt")) {
            int fuelRequired = inputStream.lines().mapToInt(Integer::parseInt).map(this::computeFuelRequiredWithCompensation).sum();
            logInfo("Fuel required: %d", fuelRequired);
        }
    }

    private int computeFuelRequired(final int mass) {
        return mass / 3 - 2;
    }

    private int computeFuelRequiredWithCompensation(final int mass) {
        final int fuelRequried = mass / 3 - 2;
        if(fuelRequried > 0) {
            return fuelRequried + computeFuelRequiredWithCompensation(fuelRequried);
        } else {
            return 0;
        }
    }
}
