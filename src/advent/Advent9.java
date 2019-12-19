package advent;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent9 {
    public static void main(String[] args) {
        ArrayList<Long> program = Util.splitLine(Util.fileStream("advent9"))
                                      .stream()
                                      .map(Long::parseLong)
                                      .collect(Collectors.toCollection(Lists::newArrayList));

        Computer computer = new Computer(program);
        computer.addInput(2);
        computer.solve();
        log.warn("BOOST MODE: {}", computer.getOutput());
    }
}

@Slf4j
class Computer {
    private ArrayList<Long> memory;
    private int pointer = -1;
    private int relativeBase = 0;
    private Queue<Long> inputBuffer = new LinkedList<>();
    private Queue<Long> outputBuffer = new LinkedList<>();
    private int saveState = 0;

    @Getter
    private boolean isRunning = true;

    public Computer(ArrayList<Long> memory) {
        this.memory = newArrayList(memory);
    }

    public void addInput(long input) {
        inputBuffer.add(input);
    }

    public long getOutput() {
        return outputBuffer.peek() != null ? outputBuffer.poll() : 0L;
    }

    public boolean hasOutput() {
        return outputBuffer.peek() != null;
    }

    void solve() {
        for (int i = saveState; i < memory.size() && memory.get(i) != 99; ) {
            pointer = i;
            int opCode = (int) (memory.get(pointer) % 100);
            int params = (int) (memory.get(pointer) / 100);

            ParameterType param1 = ParameterType.values()[params % 100 % 10];
            ParameterType param2 = ParameterType.values()[params / 10 % 10];
            ParameterType param3 = ParameterType.values()[params / 100];

            if (opCode == 1) {
                processOpCode1(param1, param2, param3);
                i += 4;
            } else if (opCode == 2) {
                processOpCode2(param1, param2, param3);
                i += 4;
            } else if (opCode == 3) {
                Long poll = inputBuffer.poll();
                if (poll == null) {
                    saveState = i;
                    return;
                }
                processOpCode3(param1, poll);
                i += 2;
            } else if (opCode == 4) {
                outputBuffer.add(processOpCode4(param1));
                i += 2;
            } else if (opCode == 5) {
                i = processOpCode5(param1, param2);
            } else if (opCode == 6) {
                i = processOpCode6(param1, param2);
            } else if (opCode == 7) {
                processOpCode7(param1, param2, param3);
                i += 4;
            } else if (opCode == 8) {
                processOpCode8(param1, param2, param3);
                i += 4;
            } else if (opCode == 9) {
                processOpCode9(param1);
                i += 2;
            }
        }
        isRunning = false;
//        log.info("======================================================");
    }

    private void processOpCode1(ParameterType param1, ParameterType param2, ParameterType param3) {
        long value1 = getValueOrReference(param1, 1);
        long value2 = getValueOrReference(param2, 2);
        int index = getIndex(param3, pointer + 3);
        long element = value1 + value2;
        write(index, element);
//        log.info("OPCODE 1 : value1={}, value2={}, element={}, index={}", value1, value2, element, index);
    }

    private void processOpCode2(ParameterType param1, ParameterType param2, ParameterType param3) {
        long value1 = getValueOrReference(param1, 1);
        long value2 = getValueOrReference(param2, 2);
        int index = getIndex(param3, pointer + 3);
        long element = value1 * value2;
        write(index, element);
//        log.info("OPCODE 2 : value1={}, value2={}, element={}, index={}", value1, value2, element, index);
    }

    private void processOpCode3(ParameterType param1, long input) {
        int index = getIndex(param1, pointer + 1);
        write(index, input);
//        log.info("OPCODE 3 : input={}, index={}", input, index);
    }

    private long processOpCode4(ParameterType param1) {
//        log.info("OPCODE 4 : output={}", getValueOrReference(param1, 1));
        return getValueOrReference(param1, 1);
    }

    private int processOpCode5(ParameterType param1, ParameterType param2) {
        long value1 = getValueOrReference(param1, 1);
        long value2 = getValueOrReference(param2, 2);
//        log.info("OPCODE 5 : value1={} value2={}", value1, value2);
        if (value1 != 0) {
            return (int) value2;
        }
        return pointer + 3;
    }

    private int processOpCode6(ParameterType param1, ParameterType param2) {
        long value1 = getValueOrReference(param1, 1);
        long value2 = getValueOrReference(param2, 2);
//        log.info("OPCODE 6 : value1={} value2={}", value1, value2);
        if (value1 == 0) {
            return (int) value2;
        }
        return pointer + 3;
    }

    private void processOpCode7(ParameterType param1, ParameterType param2, ParameterType param3) {
        long value1 = getValueOrReference(param1, 1);
        long value2 = getValueOrReference(param2, 2);
        int index = getIndex(param3, pointer + 3);
        int element = value1 < value2 ? 1 : 0;
        write(index, element);
//        log.info("OPCODE 7: value1={}, value2={}, element={}, index={}", value1, value2, element, index);
    }

    private void processOpCode8(ParameterType param1, ParameterType param2, ParameterType param3) {
        long value1 = getValueOrReference(param1, 1);
        long value2 = getValueOrReference(param2, 2);
        int index = getIndex(param3, pointer + 3);
        int element = value1 == value2 ? 1 : 0;
        write(index, element);
//        log.info("OPCODE 8: value1={}, value2={}, element={}, index={}", value1, value2, element, index);
    }

    private void processOpCode9(ParameterType param1) {
        long value1 = getValueOrReference(param1, 1);
        relativeBase += value1;
//        log.info("OPCODE 9 : value1={}, relativeBase={}", value1, relativeBase);
    }

    private long getValueOrReference(ParameterType parameterType, int offset) {
        switch (parameterType) {
            case POSITION:
                return read((int) read(pointer + offset));
            case IMMEDIATE:
                return read(pointer + offset);
            case RELATIVE:
                return read(relativeBase + (int) read(pointer + offset));
            default:
                throw new UnsupportedOperationException();
        }
    }

    private int getIndex(ParameterType param3, int i) {
        switch (param3) {
            case POSITION:
                return memory.get(i)
                             .intValue();
            case IMMEDIATE:
                return i;
            case RELATIVE:
                return relativeBase + memory.get(i)
                                            .intValue();
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void write(int index, long element) {
        expandMemoryIfNeeded(index);
        memory.add(index, element);
        memory.remove(index + 1);
    }

    private long read(int index) {
        expandMemoryIfNeeded(index);
        return memory.get(index);
    }

    private void expandMemoryIfNeeded(int index) {
        if (index >= memory.size()) {
            for (long i = memory.size(); i <= index; i++) {
                memory.add(0L);
            }
        }
    }
}

enum ParameterType {
    POSITION, IMMEDIATE, RELATIVE
}