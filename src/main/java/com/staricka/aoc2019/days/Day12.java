package com.staricka.aoc2019.days;

import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;
import com.staricka.aoc2019.util.data.PositionAndVelocity;
import com.staricka.aoc2019.util.data.ThreeCoordinate;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day12.txt")) {
            final Satellites satellites = new Satellites();
            inputStream.lines().map(ThreeCoordinate::new).map(PositionAndVelocity::new).forEach(satellites::add);
            runSteps(1000, satellites);
            logInfo("Total energy: %d", satellites.getEnergy());
        }
    }

    @Override
    public void part2() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day12.txt")) {
            final Satellites satellites = new Satellites();
            inputStream.lines().map(ThreeCoordinate::new).map(PositionAndVelocity::new).forEach(satellites::add);
            logInfo("Time to loop: %d", satellites.findLoop());
        }
    }

    @Override
    public void test() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day12s1.txt")) {
            final Satellites satellites = new Satellites();
            inputStream.lines().map(ThreeCoordinate::new).map(PositionAndVelocity::new).forEach(satellites::add);
            runSteps(10, satellites);
            logInfo(satellites.getEnergy());
        }
    }

    private void runSteps(final int steps, final Satellites satellites) {
        IntStream.range(0, steps).forEach(step -> {
            logDebug(() -> String.format("After %d steps:\n%s\n", step, satellites));
            satellites.step();
        });
        logDebug(() -> String.format("After %d steps:\n%s\n", steps, satellites));
    }

    private static class Satellites {
        private List<PositionAndVelocity> satellites = new ArrayList<>();

        public void add(final PositionAndVelocity positionAndVelocity) {
            satellites.add(positionAndVelocity);
        }

        public void step() {
            satellites = new ArrayList<>(satellites);
            for (int i = 0; i < satellites.size(); i++) {
                for (int j = i + 1; j < satellites.size(); j++) {
                    PositionAndVelocity satelliteA = satellites.get(i);
                    PositionAndVelocity satelliteB = satellites.get(j);
                    boolean xGreater = satelliteA.getPosition().getX() > satelliteB.getPosition().getX();
                    boolean yGreater = satelliteA.getPosition().getY() > satelliteB.getPosition().getY();
                    boolean zGreater = satelliteA.getPosition().getZ() > satelliteB.getPosition().getZ();
                    boolean xEqual = satelliteA.getPosition().getX() == satelliteB.getPosition().getX();
                    boolean yEqual = satelliteA.getPosition().getY() == satelliteB.getPosition().getY();
                    boolean zEqual = satelliteA.getPosition().getZ() == satelliteB.getPosition().getZ();

                    satellites.set(i, satelliteA.updateVelocity(satelliteA.getVelocity()
                            .add(xGreater ? -1 : (xEqual ? 0 : 1), yGreater ? -1 : (yEqual ? 0 : 1),
                                    zGreater ? -1 : (zEqual ? 0 : 1))));
                    satellites.set(j, satelliteB.updateVelocity(satelliteB.getVelocity()
                            .add(xGreater ? 1 : (xEqual ? 0 : -1), yGreater ? 1 : (yEqual ? 0 : -1),
                                    zGreater ? 1 : (zEqual ? 0 : -1))));
                }
            }

            satellites = satellites.stream().map(PositionAndVelocity::applyVelocity).collect(Collectors.toList());
        }

        public long findLoop() {
            long steps = 0;
            Long firstX = null;
            Long firstY = null;
            Long firstZ = null;

            while (true) {
                step();
                steps++;

                boolean xZero = satellites.stream().allMatch(s -> s.getVelocity().getX() == 0);
                boolean yZero = satellites.stream().allMatch(s -> s.getVelocity().getY() == 0);
                boolean zZero = satellites.stream().allMatch(s -> s.getVelocity().getZ() == 0);

                if (xZero && firstX == null) {
                    firstX = steps;
                }
                if (yZero && firstY == null) {
                    firstY = steps;
                }
                if (zZero && firstZ == null) {
                    firstZ = steps;
                }
                if (firstX != null && firstY != null & firstZ != null) {
                    logInfo("fx %d fy %d fz %d", firstX, firstY, firstZ);
                    return ArithmeticUtils.lcm(ArithmeticUtils.lcm(firstX, firstY), firstZ) * 2;
                }
            }
        }

        public int getEnergy() {
            return satellites.stream()
                    .mapToInt(s -> s.getPosition().absoluteComponentSum() * s.getVelocity().absoluteComponentSum())
                    .sum();
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        private int getPadding(final Function<PositionAndVelocity, ThreeCoordinate> selectorA,
                final Function<ThreeCoordinate, Integer> selectorB) {
            return satellites.stream().map(selectorA).map(selectorB).mapToInt(i -> String.valueOf(i).length()).max()
                    .getAsInt();
        }

        @Override
        public String toString() {
            return satellites.stream()
                    .map(s -> s.toPaddedString(getPadding(PositionAndVelocity::getPosition, ThreeCoordinate::getX),
                            getPadding(PositionAndVelocity::getPosition, ThreeCoordinate::getY),
                            getPadding(PositionAndVelocity::getPosition, ThreeCoordinate::getZ),
                            getPadding(PositionAndVelocity::getVelocity, ThreeCoordinate::getX),
                            getPadding(PositionAndVelocity::getVelocity, ThreeCoordinate::getY),
                            getPadding(PositionAndVelocity::getVelocity, ThreeCoordinate::getZ)))
                    .collect(Collectors.joining("\n"));
        }
    }
}
