package com.staricka.aoc2019.days;

import com.staricka.aoc2019.sif.SifImage;
import com.staricka.aoc2019.util.AocDay;
import com.staricka.aoc2019.util.AocInputStream;

public class Day8 extends AocDay {
    @Override
    public void part1() throws Exception {
        try (AocInputStream inputStream = new AocInputStream("day8a.txt")) {
            final SifImage sifImage = new SifImage(inputStream.getLine(), 25, 6);
            logInfo("Checksum: %d", sifImage.checksum());
        }
    }

    @Override
    public void part2() throws Exception {
        try (AocInputStream inputStream = new AocInputStream("day8a.txt")) {
            final SifImage sifImage = new SifImage(inputStream.getLine(), 25, 6);
            System.out.println(sifImage.render().toString());
        }
    }
}
