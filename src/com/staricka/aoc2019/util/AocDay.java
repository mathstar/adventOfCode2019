package com.staricka.aoc2019.util;

import java.util.function.Supplier;

public abstract class AocDay {
    private static boolean debug;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(final boolean debug) {
        AocDay.debug = debug;
    }

    public static void logInfo(final String message, Object... args) {
        System.out.println(String.format(message, args));
    }

    public static void logDebug(final String message, Object... args) {
        if (debug) {
            System.out.println(String.format(message, args));
        }
    }

    public static void logDebug(final Supplier<String> expensiveFunction) {
        if (debug) {
            System.out.println(expensiveFunction.get());
        }
    }

    public abstract void part1() throws Exception;

    public abstract void part2() throws Exception;

    public void test() throws Exception {
    }
}
