package com.staricka.aoc2019.days;

import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day6 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day6a.txt")) {
            final Map<String, CelestialObject> universe = new HashMap<>();
            final CelestialObject centerOfMass = new CenterOfMass();
            universe.put(centerOfMass.identifier, centerOfMass);
            inputStream.lines().forEach(line -> addObject(universe, line));
            logInfo("Universe: %s", universe.keySet());

            final Set<CelestialObject> roots =
                    universe.values().stream().filter(c -> c.parent == null).collect(Collectors.toSet());
            logInfo("Root nodes: %s", roots);

            final OrbitCounts orbitCounts =
                    roots.stream().map(this::countOrbits).reduce(new OrbitCounts(), OrbitCounts::add);
            logInfo("Direct: %d Indirect: %d Total: %d", orbitCounts.direct, orbitCounts.indirect,
                    orbitCounts.direct + orbitCounts.indirect);
        }
    }

    @Override
    public void part2() throws Exception {
        try (final AocInputStream inputStream = new AocInputStream("day6a.txt")) {
            final Map<String, CelestialObject> universe = new HashMap<>();
            final CelestialObject centerOfMass = new CenterOfMass();
            universe.put(centerOfMass.identifier, centerOfMass);
            inputStream.lines().forEach(line -> addObject(universe, line));
            logInfo("Universe: %s", universe.keySet());

            LinkedHashSet<CelestialObject> santasOrbits = getOrbits(universe.get("SAN"));
            LinkedHashSet<CelestialObject> myOrbits = getOrbits(universe.get("YOU"));

            CelestialObject firstCommonOrbit = null;
            int santaJumps = 0;
            for (CelestialObject object : santasOrbits) {
                if (myOrbits.contains(object)) {
                    firstCommonOrbit = object;
                    break;
                }
                santaJumps++;
            }

            int myJumps = 0;
            for (CelestialObject object : myOrbits) {
                if (object.equals(firstCommonOrbit)) {
                    break;
                }
                myJumps++;
            }

            logInfo("Total jumps: %d", santaJumps + myJumps);
        }
    }

    private void addObject(final Map<String, CelestialObject> universe, final String relationship) {
        final String[] parse = relationship.split("\\)");
        final String parentIdentifier = parse[0];
        final String childIdentifier = parse[1];

        final CelestialObject parent = universe.computeIfAbsent(parentIdentifier, CelestialObject::new);
        final CelestialObject child = universe.computeIfAbsent(childIdentifier, CelestialObject::new);
        child.setParent(parent);
        parent.addChild(child);
    }

    private OrbitCounts countOrbits(final CelestialObject root) {
        int depth = 0;
        Set<CelestialObject> currentLevel = Collections.singleton(root);
        Set<CelestialObject> nextLevel = new HashSet<>();
        final OrbitCounts orbitCounts = new OrbitCounts();

        while (!currentLevel.isEmpty()) {
            for (final CelestialObject currentObject : currentLevel) {
                orbitCounts.direct += currentObject.children.size();
                orbitCounts.indirect += Math.max(0, depth - 1);
                nextLevel.addAll(currentObject.children);
            }
            depth++;
            currentLevel = nextLevel;
            nextLevel = new HashSet<>();
        }
        return orbitCounts;
    }

    private LinkedHashSet<CelestialObject> getOrbits(CelestialObject object) {
        final LinkedHashSet<CelestialObject> orbits = new LinkedHashSet<>();
        while (object.parent != null) {
            orbits.add(object.parent);
            object = object.parent;
        }
        return orbits;
    }

    private static class CelestialObject {
        private final String identifier;
        private List<CelestialObject> children = new ArrayList<>();
        private CelestialObject parent;

        private CelestialObject(final String identifier) {
            this.identifier = identifier;
        }

        private CelestialObject(final String identifier, final CelestialObject parent) {
            this.identifier = identifier;
            this.parent = parent;
        }

        public void setParent(final CelestialObject parent) {
            this.parent = parent;
        }

        private void addChild(final CelestialObject child) {
            children.add(child);
        }

        @Override
        public String toString() {
            return identifier;
        }
    }

    private static class CenterOfMass extends CelestialObject {
        private CenterOfMass() {
            super("COM");
        }
    }

    private static class OrbitCounts {
        private int direct = 0;
        private int indirect = 0;

        private OrbitCounts add(final OrbitCounts other) {
            final OrbitCounts sum = new OrbitCounts();
            sum.direct = direct + other.direct;
            sum.indirect = indirect + other.indirect;
            return sum;
        }

        private static OrbitCounts sum(final OrbitCounts a, final OrbitCounts b) {
            return a.add(b);
        }
    }
}
