package com.staricka.aoc2019.util.data;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreeCoordinate {
    private static final Pattern STRING_INPUT_PATTERN =
            Pattern.compile("<x= *(-?[0-9]+), y= *(-?[0-9]+), z= *(-?[0-9]+)>");

    public static final ThreeCoordinate ORIGIN = new ThreeCoordinate(0, 0, 0);

    private final int x;
    private final int y;
    private final int z;

    public ThreeCoordinate(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ThreeCoordinate(final String string) {
        final Matcher matcher = STRING_INPUT_PATTERN.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid coordinate string");
        }
        x = Integer.parseInt(matcher.group(1));
        y = Integer.parseInt(matcher.group(2));
        z = Integer.parseInt(matcher.group(3));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public ThreeCoordinate add(final ThreeCoordinate other) {
        return new ThreeCoordinate(x + other.x, y + other.y, z + other.z);
    }

    public ThreeCoordinate add(final int xIncrement, final int yIncrement, final int zIncrement) {
        return new ThreeCoordinate(x + xIncrement, y + yIncrement, z + zIncrement);
    }

    public int absoluteComponentSum() {
        return Math.abs(x) + Math.abs(y) + Math.abs(z);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ThreeCoordinate that = (ThreeCoordinate) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("<x=%d, y=%d, z=%d>", x, y, z);
    }

    public String toPaddedString(final int digits) {
        final String format = String.format("<x=%%%dd, y=%%%dd, z=%%%dd>", digits, digits, digits);
        return String.format(format, x, y, z);
    }

    public String toPaddedString(final int digitsX, final int digitsY, final int digitsZ) {
        final String format = String.format("<x=%%%dd, y=%%%dd, z=%%%dd>", digitsX, digitsY, digitsZ);
        return String.format(format, x, y, z);
    }
}
