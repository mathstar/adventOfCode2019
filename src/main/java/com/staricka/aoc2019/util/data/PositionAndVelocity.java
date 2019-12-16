package com.staricka.aoc2019.util.data;

import java.util.Objects;

public class PositionAndVelocity {
    private final ThreeCoordinate position;
    private final ThreeCoordinate velocity;

    public PositionAndVelocity(final ThreeCoordinate position) {
        this.position = position;
        velocity = ThreeCoordinate.ORIGIN;
    }

    public PositionAndVelocity(final ThreeCoordinate position, final ThreeCoordinate velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public ThreeCoordinate getPosition() {
        return position;
    }

    public ThreeCoordinate getVelocity() {
        return velocity;
    }

    public PositionAndVelocity applyVelocity() {
        return new PositionAndVelocity(position.add(velocity), velocity);
    }

    public PositionAndVelocity updatePosition(final ThreeCoordinate position) {
        return new PositionAndVelocity(position, velocity);
    }

    public PositionAndVelocity updateVelocity(final ThreeCoordinate velocity) {
        return new PositionAndVelocity(position, velocity);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PositionAndVelocity that = (PositionAndVelocity) o;
        return Objects.equals(position, that.position) && Objects.equals(velocity, that.velocity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, velocity);
    }

    @Override
    public String toString() {
        return String.format("pos=%s, vel=%s", position, velocity);
    }

    public String toPaddedString(final int digits) {
        return String.format("pos=%s, vel=%s", position.toPaddedString(digits), velocity.toPaddedString(digits));
    }

    public String toPaddedString(final int digitsX, final int digitsY, final int digitsZ) {
        return String.format("pos=%s, vel=%s", position.toPaddedString(digitsX, digitsY, digitsZ),
                velocity.toPaddedString(digitsX, digitsY, digitsZ));
    }

    public String toPaddedString(final int digitsXP, final int digitsYP, final int digitsZP, final int digitsXV,
            final int digitsYV, final int digitsZV) {
        return String.format("pos=%s, vel=%s", position.toPaddedString(digitsXP, digitsYP, digitsZP),
                velocity.toPaddedString(digitsXV, digitsYV, digitsZV));
    }
}
