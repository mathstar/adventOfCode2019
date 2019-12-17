package com.staricka.aoc2019.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AocRunner {
    private final static Pattern dayPattern = Pattern.compile("^([0-9][0-9]?)([ab]?)(d?)(t?)$");

    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws Exception {
        final BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Run which day?");
        final String dayRequest = stdinReader.readLine();

        final Matcher matcher = dayPattern.matcher(dayRequest);
        if (!matcher.matches()) {
            System.out.println("Invalid day");
            System.exit(1);
        }

        final boolean runPart1Only = matcher.group(2).equals("a");
        final boolean runPart2Only = matcher.group(2).equals("b");
        final boolean runTest = matcher.group(4).equals("t");
        final boolean debug = matcher.group(3).equals("d");

        final int dayNumber = Integer.parseInt(matcher.group(1));

        System.out.println(String.format("Running day %d%s", dayNumber,
                runPart1Only ? " part 1" : (runPart2Only ? " part 2" : (runTest ? " test" : ""))));

        try {
            Class<?> dayClass = ClassLoader.getSystemClassLoader().loadClass("com.staricka.aoc2019.days.Day" + dayNumber);
            if (!AocDay.class.isAssignableFrom(dayClass)) {
                System.out.println("Day class must implement AocDay");
                System.exit(1);
            }
            AocDay day = ((Class<? extends AocDay>) dayClass).getConstructor().newInstance();
            day.setDebug(debug);
            if (runPart1Only) {
                day.part1();
            } else if (runPart2Only) {
                day.part2();
            } else if (runTest) {
                day.test();
            } else {
                day.part1();
                day.part2();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Day class not found");
            System.exit(1);
        }
    }
}
