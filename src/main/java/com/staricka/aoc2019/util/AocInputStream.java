package com.staricka.aoc2019.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AocInputStream implements AutoCloseable, Closeable {
    final BufferedReader reader;

    public AocInputStream(final String resourceName) {
        final InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new RuntimeException("Resource not found: " + resourceName);
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public Stream<String> lines() {
        return reader.lines();
    }

    public String getAll() {
        return reader.lines().collect(Collectors.joining("\n"));
    }

    public String getLine() throws Exception {
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
