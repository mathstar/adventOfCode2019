package com.staricka.aoc2019.intcode;

import com.staricka.aoc2019.util.AocDay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IntCodeProgram {
    private List<Long> memory;
    private int programCounter;
    private int relativeBase;
    private boolean running;
    private BufferedReader inputReader;
    private BufferedWriter outputWriter;
    private BufferedWriter backupWriter;

    public IntCodeProgram(final String program) {
        memory = Arrays.stream(program.split(",")).map(Long::parseLong).collect(Collectors.toList());
    }

    public IntCodeProgram(final String program, final InputStream inputStream, final OutputStream outputStream) {
        memory = Arrays.stream(program.split(",")).map(Long::parseLong).collect(Collectors.toList());
        inputReader = new BufferedReader(new InputStreamReader(inputStream));
        outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public IntCodeProgram(final String program, final Supplier<Integer> inputSupplier,
            final Consumer<Integer> outputConsumer) {
        memory = Arrays.stream(program.split(",")).map(Long::parseLong).collect(Collectors.toList());
        inputReader = new SupplierReaderFaker(inputSupplier);
        outputWriter = new ConsumerWriterFaker(outputConsumer);
    }

    public IntCodeProgram(final String program, final InputStream inputStream, final OutputStream outputStream,
            final Writer backupWriter) {
        memory = Arrays.stream(program.split(",")).map(Long::parseLong).collect(Collectors.toList());
        inputReader = new BufferedReader(new InputStreamReader(inputStream));
        outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        this.backupWriter = new BufferedWriter(backupWriter);
    }

    public IntCodeProgram(final String program, final Reader inputReader, final OutputStream outputStream) {
        memory = Arrays.stream(program.split(",")).map(Long::parseLong).collect(Collectors.toList());
        this.inputReader = new BufferedReader(inputReader);
        outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public IntCodeProgram(final String program, final Reader inputReader, final Writer outputWriter) {
        memory = Arrays.stream(program.split(",")).map(Long::parseLong).collect(Collectors.toList());
        this.inputReader = new BufferedReader(inputReader);
        this.outputWriter = new BufferedWriter(outputWriter);
    }

    public IntCodeProgram(final String program, final Reader inputReader, final Writer outputWriter,
            final Writer backupWriter) {
        memory = Arrays.stream(program.split(",")).map(Long::parseLong).collect(Collectors.toList());
        this.inputReader = new BufferedReader(inputReader);
        this.outputWriter = new BufferedWriter(outputWriter);
        this.backupWriter = new BufferedWriter(backupWriter);
    }

    public void run() throws Exception {
        programCounter = 0;
        relativeBase = 0;
        running = true;
        for (int i = memory.size(); i < 10000; i++) {
            memory.add(0L);
        }
        AocDay.logDebug(memoryString());
        while (running) {
            step();
            AocDay.logDebug(this::memoryString);
        }
        if (inputReader != null) {
            inputReader.close();
        }
        if (outputWriter != null) {
            outputWriter.close();
        }
    }

    public FutureTask<Void> runAsync() {
        return new FutureTask<>(() -> {
            run();
            return null;
        });
    }

    private void step() throws Exception {
        int numericOpCode = (int)getValue(programCounter);
        AocDay.logDebug("OpCode: %d, PC: %d, RB: %d", numericOpCode, programCounter, relativeBase);
        OpCode opCode = OpCode.lookupOpCode(numericOpCode);
        boolean incrementProgramCounter = true;
        switch (opCode) {
            case ADD: {
                setValue(getAddress(programCounter + 3, ParameterMode.lookupParameterCode(numericOpCode, 2)),
                        getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) + getValue(
                                programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1)));
                break;
            }
            case MULTIPLY: {
                setValue(getAddress(programCounter + 3, ParameterMode.lookupParameterCode(numericOpCode, 2)),
                        getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) * getValue(
                                programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1)));
                break;
            }
            case STORE: {
                setValue(getAddress(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)), readInput());
                break;
            }
            case RETRIEVE: {
                writeOutput(getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)));
                break;
            }
            case JIT: {
                if (getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) != 0) {
                    programCounter = (int)getValue(programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1));
                    incrementProgramCounter = false;
                }
                break;
            }
            case JIF: {
                if (getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) == 0) {
                    programCounter = (int)getValue(programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1));
                    incrementProgramCounter = false;
                }
                break;
            }
            case LT: {
                if (getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) < getValue(
                        programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1))) {
                    setValue(getAddress(programCounter + 3, ParameterMode.lookupParameterCode(numericOpCode, 2)), 1);
                } else {
                    setValue(getAddress(programCounter + 3, ParameterMode.lookupParameterCode(numericOpCode, 2)), 0);
                }
                break;
            }
            case EQ: {
                if (getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0)) == getValue(
                        programCounter + 2, ParameterMode.lookupParameterCode(numericOpCode, 1))) {
                    setValue(getAddress(programCounter + 3, ParameterMode.lookupParameterCode(numericOpCode, 2)), 1);
                } else {
                    setValue(getAddress(programCounter + 3, ParameterMode.lookupParameterCode(numericOpCode, 2)), 0);
                }
                break;
            }
            case RELBASE: {
                relativeBase += getValue(programCounter + 1, ParameterMode.lookupParameterCode(numericOpCode, 0));
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

    private long getValue(final int address, final ParameterMode parameterMode) {
        switch (parameterMode) {
            case POSITION:
                return getValue((int)getValue(address));
            case IMMEDIATE:
                return getValue(address);
            case RELATIVE:
                return getValue((int)(getValue(address) + relativeBase));
        }
        return -1;
    }

    private int getAddress(final int address, final ParameterMode parameterMode) {
        switch (parameterMode) {
            case POSITION:
                return (int)getValue(address);
            case RELATIVE:
                return (int)(getValue(address) + relativeBase);
        }
        return -1;
    }

    public long getValue(final long address) {
        return memory.get((int)address);
    }

    public void setValue(final long address, final long value) {
        memory.set((int)address, value);
    }

    private int readInput() throws Exception {
        return Integer.parseInt(inputReader.readLine());
    }

    private void writeOutput(final long output) throws Exception {
        try {
            outputWriter.write(output + "\n");
            outputWriter.flush();
        } catch (Exception e) {
            backupWriter.write(output + "\n");
            backupWriter.flush();
        }
    }

    public void printMemory() {
        AocDay.logInfo(memoryString());
    }

    private String memoryString() {
        return memory.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private enum OpCode {
        ADD(1, 3), MULTIPLY(2, 3), STORE(3, 1), RETRIEVE(4, 1), JIT(5, 2), JIF(6, 2), LT(7, 3), EQ(8, 3), RELBASE(9, 1), STOP(99, 0);
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
        POSITION('0'), IMMEDIATE('1'), RELATIVE('2');

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

    private static class SupplierReaderFaker extends BufferedReader {
        private final Supplier<Integer> supplier;

        public SupplierReaderFaker(final Supplier<Integer> supplier) {
            super(new StringReader(""));
            this.supplier = supplier;
        }

        @Override
        public String readLine() throws IOException {
            return String.format("%d", supplier.get());
        }
    }

    private static class ConsumerWriterFaker extends BufferedWriter {
        private final Consumer<Integer> consumer;

        public ConsumerWriterFaker(final Consumer<Integer> consumer) {
            super(new StringWriter());
            this.consumer = consumer;
        }

        @Override
        public void write(String output) throws IOException {
            consumer.accept(Integer.parseInt(output.trim()));
        }
    }
}
