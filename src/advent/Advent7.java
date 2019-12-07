package advent;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent7 {
    private static ArrayList<Integer> memory = newArrayList();
    private static int[] inputBuffer = new int[10];
    private static int pointer = -1;
    private static int readHead = -1;

    public static void main(String[] args) {
        ArrayList<Integer> outputs = newArrayList();

        List<List<Integer>> phasePermutations = listPermutations(newArrayList(0, 1, 2, 3, 4));

        for (List<Integer> permutation : phasePermutations) {
            readHead = 0;

            inputBuffer[0] = permutation.get(0);
            inputBuffer[1] = 0;

            inputBuffer[2] = permutation.get(1);
            inputBuffer[3] = solve();

            inputBuffer[4] = permutation.get(2);
            inputBuffer[5] = solve();

            inputBuffer[6] = permutation.get(3);
            inputBuffer[7] = solve();

            inputBuffer[8] = permutation.get(4);
            inputBuffer[9] = solve();

            int solve = solve();
            outputs.add(solve);
        }

        OptionalInt max = outputs.stream()
                                 .mapToInt(value -> value)
                                 .max();
        log.info("Max output = {}", max.isPresent() ? max : 0);
    }

    private static int solve() {
        int output = -1;
        memory = newArrayList(splitLine(Util.fileStream("advent7")).stream()
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
                processOpCode3(inputBuffer[readHead++]);
                i += 2;
            } else if (opCode == 4) {
                output = processOpCode4(param1);
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

        return output;
    }

    private static void processOpCodes1and2(boolean param1, boolean param2, boolean param3, IntCommand intCommand) {
        int value1 = getValueOrReference(param1, 1);
        int value2 = getValueOrReference(param2, 2);
        int index = param3 ? pointer + 3 : memory.get(pointer + 3);
        int element = intCommand.apply(value1, value2);
        write(index, element);
        //log.info("{} : value1={}, value2={}, element={}, index={}", intCommand.getClass(), value1, value2, element, index);
    }

    private static void processOpCode3(int input) {
        int index = memory.get(pointer + 1);
        write(index, input);
        //log.info("OPCODE 3 : input={}, index={}", input, index);
    }

    private static int processOpCode4(boolean param1) {
        int element = getValueOrReference(param1, 1);
        //log.info("OPCODE 4 : output={}", element);
        System.out.println(element);
        return element;
    }

    private static int processOpCodes5and6(boolean param1, boolean param2, BoolCommand boolCommand) {
        int value1 = getValueOrReference(param1, 1);
        int value2 = getValueOrReference(param2, 2);
        if (boolCommand.apply(value1)) {
            //log.info("{} : value1={} value2={}", boolCommand.getClass(), value1, value2);
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
        //log.info("{} : value1={}, value2={}, element={}, index={}", compareCommand.getClass(), value1, value2, element, index);
    }

    private static void write(int index, int element) {
        memory.add(index, element);
        memory.remove(index + 1);
    }

    private static Integer getValueOrReference(boolean value, int offset) {
        return value ? memory.get(pointer + offset) : memory.get(memory.get(pointer + offset));
    }

    private static ArrayList<String> splitLine(Stream<String> stream) {
        Optional<String> first = stream.findFirst();
        if (first.isEmpty()) {
            return newArrayList();
        }
        return newArrayList(first.get()
                                 .split(","));
    }

    private static List<List<Integer>> listPermutations(List<Integer> list) {

        if (list.size() == 0) {
            List<List<Integer>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }

        List<List<Integer>> returnMe = new ArrayList<>();

        Integer firstElement = list.remove(0);

        List<List<Integer>> recursiveReturn = listPermutations(list);
        for (List<Integer> li : recursiveReturn) {

            for (int index = 0; index <= li.size(); index++) {
                List<Integer> temp = new ArrayList<>(li);
                temp.add(index, firstElement);
                returnMe.add(temp);
            }

        }
        return returnMe;
    }
}