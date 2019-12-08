package advent;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent5 {
    private static final int INPUT = 5;
    private static ArrayList<Integer> memory = newArrayList();
    private static int pointer = -1;

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        memory = newArrayList(Util.splitLine(Util.fileStream("advent5")).stream()
                                                                   .map(Integer::parseInt)
                                                                   .collect(Collectors.toList()));

        for (int i = 0; i < memory.size() && memory.get(i) != 99; ) {
            pointer = i;
            int opCode = memory.get(pointer) % 100;
            int params = memory.get(pointer) / 100;

            boolean param1 = params % 100 % 10 != 0;
            boolean param2 = params / 10 % 10 != 0;
            boolean param3 = params / 100 != 0;

            if (opCode == 1) {
                processOpCodes1and2(param1, param2, param3, new Add());
                i += 4;
            } else if (opCode == 2) {
                processOpCodes1and2(param1, param2, param3, new Multiply());
                i += 4;
            } else if (opCode == 3) {
                processOpCode3();
                i += 2;
            } else if (opCode == 4) {
                processOpCode4(param1);
                i += 2;
            } else if (opCode == 5) {
                i = processOpCodes5and6(param1, param2, new NonZero());
            } else if (opCode == 6) {
                i = processOpCodes5and6(param1, param2, new Zero());
            } else if (opCode == 7) {
                processOpCodes7and8(param1, param2, param3, new LessThan());
                i += 4;
            } else if (opCode == 8) {
                processOpCodes7and8(param1, param2, param3, new Equals());
                i += 4;
            }
        }
    }

    private static void processOpCodes1and2(boolean param1, boolean param2, boolean param3, IntCommand intCommand) {
        int value1 = getValueOrReference(param1, 1);
        int value2 = getValueOrReference(param2, 2);
        int index = param3 ? pointer + 3 : memory.get(pointer + 3);
        int element = intCommand.apply(value1, value2);
        write(index, element);
        log.info("{} : value1={}, value2={}, element={}, index={}", intCommand.getClass(), value1, value2, element, index);
    }

    private static void processOpCode3() {
        int index = memory.get(pointer + 1);
        write(index, INPUT);
        log.info("OPCODE 3 : input={}, index={}", INPUT, index);
    }

    private static void processOpCode4(boolean param1) {
        int element = getValueOrReference(param1, 1);
        log.info("OPCODE 4 : output={}", element);
        System.out.println(element);
    }

    private static int processOpCodes5and6(boolean param1, boolean param2, BoolCommand boolCommand) {
        int value1 = getValueOrReference(param1, 1);
        int value2 = getValueOrReference(param2, 2);
        if (boolCommand.apply(value1)) {
            log.info("{} : value1={} value2={}", boolCommand.getClass(), value1, value2);
            return value2;
        }
        return pointer + 3;
    }

    private static void processOpCodes7and8(boolean param1, boolean param2, boolean param3, CompareCommand compareCommand) {
        int value1 = getValueOrReference(param1, 1);
        int value2 = getValueOrReference(param2, 2);
        int index = param3 ? pointer + 3 : memory.get(pointer + 3);
        int element = compareCommand.apply(value1, value2) ? 1 : 0;
        write(index, element);
        log.info("{} : value1={}, value2={}, element={}, index={}", compareCommand.getClass(), value1, value2, element, index);
    }

    private static void write(int index, int element) {
        memory.add(index, element);
        memory.remove(index + 1);
    }

    private static Integer getValueOrReference(boolean value, int offset) {
        return value ? memory.get(pointer + offset) : memory.get(memory.get(pointer + offset));
    }
}

class LessThan implements CompareCommand {
    @Override
    public boolean apply(int val1, int val2) {
        return val1 < val2;
    }
}

class Equals implements CompareCommand {
    @Override
    public boolean apply(int val1, int val2) {
        return val1 == val2;
    }
}

class Zero implements BoolCommand {
    @Override
    public boolean apply(int val1) {
        return val1 == 0;
    }
}

class NonZero implements BoolCommand {
    @Override
    public boolean apply(int val1) {
        return val1 != 0;
    }
}

class Multiply implements IntCommand {
    @Override
    public int apply(int val1, int val2) {
        return val1 * val2;
    }
}

class Add implements IntCommand {
    @Override
    public int apply(int val1, int val2) {
        return val1 + val2;
    }
}

interface IntCommand {
    int apply(int val1, int val2);
}

interface BoolCommand {
    boolean apply(int val1);
}

interface CompareCommand {
    boolean apply(int val1, int val2);
}