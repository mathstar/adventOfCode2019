package com.staricka.aoc2019.intcode;

import com.staricka.aoc2019.util.AocDay;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IntCodeProgram {
    private List<Integer> memory;
    private int programCounter;
    private boolean running;

    public IntCodeProgram(final String input) {
        memory = Arrays.stream(input.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    public void run() {
        programCounter = 0;
        running = true;
        AocDay.logDebug(memoryString());
        while (running) {
            step();
            AocDay.logDebug(memoryString());
        }
    }

    private void step() {
        OpCode opCode = OpCode.lookupOpCode(getValue(programCounter));
        switch (opCode) {
            case ADD: {
                setValue(getValue(programCounter + 3),
                        getValue(getValue(programCounter + 1)) + getValue(getValue(programCounter + 2)));
                break;
            }
            case MULTIPLY: {
                setValue(getValue(programCounter + 3),
                        getValue(getValue(programCounter + 1)) * getValue(getValue(programCounter + 2)));
                break;
            }
            case STOP: {
                running = false;
                break;
            }
        }
        programCounter += opCode.parameterCount + 1;
    }

    public int getValue(final int address) {
        return memory.get(address);
    }

    public void setValue(final int address, final int value) {
        memory.set(address, value);
    }

    public void printMemory() {
        AocDay.logInfo(memoryString());
    }

    private String memoryString() {
        return memory.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private enum OpCode {
        ADD(1, 3), MULTIPLY(2, 3), STOP(99, 0);
        private int opCode;
        private int parameterCount;

        private static Map<Integer, OpCode> lookupMap =
                Arrays.stream(OpCode.values()).collect(Collectors.toMap(o -> o.opCode, Function.identity()));

        OpCode(final int opCode, final int parameterCount) {
            this.opCode = opCode;
            this.parameterCount = parameterCount;
        }

        private static OpCode lookupOpCode(final int opCode) {
            return lookupMap.get(opCode);
        }
    }

}
