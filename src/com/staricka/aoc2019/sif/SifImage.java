package com.staricka.aoc2019.sif;

import com.staricka.aoc2019.util.data.ExpandingGrid;
import com.staricka.aoc2019.util.data.GridValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SifImage {
    private final int width;
    private final int height;
    private final List<Layer> layers;

    private static final Pixel TRANSPARENT = new Pixel('2');

    public SifImage(final String encoded, final int width, final int height) {
        this.width = width;
        this.height = height;

        final int layerCount = encoded.length() / (width * height);
        layers = new ArrayList<>(layerCount);

        for (int layerNumber = 0; layerNumber < layerCount; layerNumber++) {
            layers.add(new Layer(encoded.substring(width * height * layerNumber, width * height * (layerNumber + 1))));
        }
    }

    public long checksum() {
        Layer leastZeros = layers.stream().min(Comparator.comparing(
                layer -> layer.pixels.getFilledTiles().stream().filter(pixel -> pixel.getValue().value == '0').count()))
                .get();
        long ones = leastZeros.pixels.getFilledTiles().stream().filter(pixel -> pixel.getValue().value == '1').count();
        long twos = leastZeros.pixels.getFilledTiles().stream().filter(pixel -> pixel.getValue().value == '2').count();
        return ones * twos;
    }

    public ExpandingGrid<Pixel> render() {
        ExpandingGrid<Pixel> rendered = new ExpandingGrid<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int javaIsSillyX = x;
                final int javaIsSillyY = y;
                final Pixel current = layers.stream().map(layer -> layer.pixels.get(javaIsSillyX, javaIsSillyY))
                        .reduce(TRANSPARENT, Pixel::renderOver);
                rendered.put(x, y, current);
            }
        }
        return rendered;
    }

    private class Layer {
        private final ExpandingGrid<Pixel> pixels;

        private Layer(final String encoded) {
            pixels = new ExpandingGrid<>();
            int x = 0;
            int y = 0;
            for (char pixel : encoded.toCharArray()) {
                pixels.put(x, y, new Pixel(pixel));
                x++;
                if (x >= width) {
                    y++;
                    x = 0;
                }
            }
        }
    }

    private static class Pixel implements GridValue {
        private final char value;

        private Pixel(final char value) {
            this.value = value;
        }

        @Override
        public char getPrintValue() {
            switch (value) {
                case '0':
                    return '#';
                case '1':
                    return ' ';
            }
            return value;
        }

        public Pixel renderOver(final Pixel underneath) {
            switch (this.value) {
                case '0':
                case '1':
                    return this;
                case '2':
                    return underneath;
            }
            return this;
        }
    }
}
