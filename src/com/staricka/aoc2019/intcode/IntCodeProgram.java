package com.staricka.aoc2019.intcode;

import com.staricka.aoc2019.util.AocDay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IntCodeProgram {
    private List<Integer> memory;
    private int programCounter;
    private boolean running;
    private BufferedReader inputReader;
    private BufferedWriter outputWriter;

    public IntCodeProgram(final String program) throws Exception {
        memory = Arrays.stream(program.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    public IntCodeProgram(final String program, final InputStream inputStream, final OutputStream outputStream)
            throws Exception {
        memory = Arrays.stream(program.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        inputReader = new BufferedReader(new InputStreamReader(inputStream));
        outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public IntCodeProgram(final String program, final Reader inputReader, final OutputStream outputStream)
            throws Exception {
        memory = Arrays.stream(program.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        this.inputReader = new BufferedReader(inputReader);
        outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public void run() throws Exception {
        programCounter = 0;
        running = true;
        AocDay.logDebug(memoryString());
        while (running) {
            step();
            AocDay.logDebug(memoryString());
        }
        if (inputReader != null) {
            inputReader.close();
        }
        if (outputWriter != null) {
            outputWriter.close();
        }
    }

    private void step() throws Exception {
        int numericOpCode = getValue(programCounter);
        AocDay.logDebug("OpCode: %d, PC: %d", numericOpCode, programCounter);
        OpCode opCode = OpCode.lookupOpCode(numericOpCode);
        boolean incrementProgramCounter = true;
        switch (opCode) {
            case ADD: {
                setValue(getValue(programCounter + 3),
                        getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) + getValue(
                                programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1)));
                break;
            }
            case MULTIPLY: {
                setValue(getValue(programCounter + 3),
                        getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) * getValue(
                                programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1)));
                break;
            }
            case STORE: {
                setValue(getValue(programCounter + 1), readInput());
                break;
            }
            case RETRIEVE: {
                writeOutput(getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)));
                break;
            }
            case JIT: {
                if (getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) != 0) {
                    programCounter = getValue(programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1));
                    incrementProgramCounter = false;
                }
                break;
            }
            case JIF: {
                if (getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) == 0) {
                    programCounter = getValue(programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1));
                    incrementProgramCounter = false;
                }
                break;
            }
            case LT: {
                if (getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) < getValue(
                        programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1))) {
                    setValue(getValue(programCounter + 3), 1);
                } else {
                    setValue(getValue(programCounter + 3), 0);
                }
                break;
            }
            case EQ: {
                if (getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) == getValue(
                        programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1))) {
                    setValue(getValue(programCounter + 3), 1);
                } else {
                    setValue(getValue(programCounter + 3), 0);
                }
                break;
            }
            case STOP: {
                running = false;
                break;
            }
        } if (incrementProgramCounter) {
            programCounter += opCode.parameterCount + 1;
        }
    }

    private int getValue(final int address, final ParameterMode parameterMode) {
        switch (parameterMode) {
            case POSITION:
                return getValue(getValue(address));
            case IMMEDIATE:
                return getValue(address);
        }
        return -1;
    }

    public int getValue(final int address) {
        return memory.get(address);
    }

    public void setValue(final int address, final int value) {
        memory.set(address, value);
    }

    private int readInput() throws Exception {
        return Integer.parseInt(inputReader.readLine());
    }

    private void writeOutput(final int output) throws Exception {
        outputWriter.write(output + "\n");
    }

    public void printMemory() {
        AocDay.logInfo(memoryString());
    }

    private String memoryString() {
        return memory.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private enum OpCode {
        ADD(1, 3), MULTIPLY(2, 3), STORE(3, 1), RETRIEVE(4, 1), JIT(5, 2), JIF(6, 2), LT(7, 3), EQ(8, 3), STOP(99, 0);
        private int opCode;
        private int parameterCount;

        private static Map<Integer, OpCode> lookupMap =
                Arrays.stream(OpCode.values()).collect(Collectors.toMap(o -> o.opCode, Function.identity()));

        OpCode(final int opCode, final int parameterCount) {
            this.opCode = opCode;
            this.parameterCount = parameterCount;
        }

        private static OpCode lookupOpCode(final int opCode) {
            return lookupMap.get(opCode % 100);
        }
    }

    private enum ParameterMode {
        POSITION('0'), IMMEDIATE('1');

        private static Map<Character, ParameterMode> lookupMap =
                Arrays.stream(ParameterMode.values()).collect(Collectors.toMap(p -> p.code, Function.identity()));

        private char code;

        ParameterMode(char code) {
            this.code = code;
        }

        private static ParameterMode lookupParameterCode(final int opCode, final int parameterIndex) {
            char[] digits = String.valueOf(opCode).toCharArray();
            int index = digits.length - 3 - parameterIndex;
            //AocDay.logDebug("oc: %d, pi: %d, i: %d, dig: %s", opCode, parameterIndex, index, Arrays.toString(digits));
            if (index < 0) {
                return POSITION;
            }
            return lookupMap.get(digits[index]);
        }
    }

}
