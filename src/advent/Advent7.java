package advent;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent7 {
    public static void main(String[] args) {
        ArrayList<Integer> program = newArrayList(splitLine(Util.fileStream("advent7")).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList()));

        ArrayList<Integer> outputs = newArrayList();

        List<List<Integer>> phasePermutations = listPermutations(newArrayList(0, 1, 2, 3, 4));
        //List<List<Integer>> phasePermutations = listPermutations(newArrayList(5, 6, 7, 8, 9));

        Amplifier amplifier1 = new Amplifier(program, "amplifier1");
        Amplifier amplifier2 = new Amplifier(program, "amplifier2");
        Amplifier amplifier3 = new Amplifier(program, "amplifier3");
        Amplifier amplifier4 = new Amplifier(program, "amplifier4");
        Amplifier amplifier5 = new Amplifier(program, "amplifier5");

        //for (List<Integer> permutation : phasePermutations) {
        List<Integer> permutation = newArrayList(9, 8, 7, 6, 5);

        amplifier1.addInput(permutation.get(0));
        amplifier2.addInput(permutation.get(1));
        amplifier3.addInput(permutation.get(2));
        amplifier4.addInput(permutation.get(3));
        amplifier5.addInput(permutation.get(4));

        int amp1out_amp2in;
        int amp2out_amp3in;
        int amp3out_amp4in;
        int amp4out_amp5in;
        int amp5out_amp1in = 0;

        while (amplifier1.isRunning() && amplifier2.isRunning() && amplifier3.isRunning() && amplifier4.isRunning() && amplifier5.isRunning()) {
            amp1out_amp2in = runAmplifier(amplifier1, amp5out_amp1in);
            amp2out_amp3in = runAmplifier(amplifier2, amp1out_amp2in);
            amp3out_amp4in = runAmplifier(amplifier3, amp2out_amp3in);
            amp4out_amp5in = runAmplifier(amplifier4, amp3out_amp4in);
            amp5out_amp1in = runAmplifier(amplifier5, amp4out_amp5in);
        }

        outputs.add(amp5out_amp1in);
        //}

        OptionalInt max = outputs.stream()
                                 .mapToInt(value -> value)
                                 .max();
        //log.info("Max output = {}", max.isPresent() ? max : 0);
        System.out.println(max.isPresent() ? max.getAsInt() : 0);
    }

    private static int runAmplifier(Amplifier amplifier, int input) {
        amplifier.addInput(input);
        amplifier.solve();
        return amplifier.getOutput();
    }

    private static ArrayList<String> splitLine(Stream<String> stream) {
        return stream.findFirst().map(s -> newArrayList(s.split(","))).orElseGet(Lists::newArrayList);
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

@Slf4j
class Amplifier {
    private String name;
    private ArrayList<Integer> memory;
    private ArrayList<Integer> inputBuffer = newArrayList();
    private int pointer = -1;
    private int readHead = -1;
    private boolean halted = false;

    private int save = 0;
    private Integer output;

    Amplifier(ArrayList<Integer> program, String name) {
        this.memory = program;
        this.name = name;
    }

    int getOutput() {
        return output;
    }

    boolean isRunning() {
        return !halted;
    }

    void addInput(int input) {
        inputBuffer.add(input);
    }

    void solve() {
        System.out.println(name + " starting from IP " + save);
        for (int i = save; i < memory.size(); ) {
            pointer = i;

            int opCode = memory.get(pointer) % 100;
            if (opCode == 99) {
                halted = true;
                return;
            }

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
                if (readHead + 1 >= inputBuffer.size()) {
                    System.out.println(name + " waiting for input");
                    save = pointer;
                    return;
                }
                processOpCode3();
                i += 2;
            } else if (opCode == 4) {
                output = processOpCode4(param1);
                System.out.println(name + " output " + output);
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

    private void processOpCodes1and2(boolean param1, boolean param2, boolean param3, IntCommand intCommand) {
        int value1 = getValueOrReference(param1, 1);
        int value2 = getValueOrReference(param2, 2);
        int index = param3 ? pointer + 3 : memory.get(pointer + 3);
        int element = intCommand.apply(value1, value2);
        write(index, element);
//        log.info("{} : value1={}, value2={}, element={}, index={}", intCommand.getClass(), value1, value2, element, index);
    }

    private void processOpCode3() {
        ++readHead;
        int input = inputBuffer.get(readHead);
        int index = memory.get(pointer + 1);
        write(index, input);
//        log.info("OPCODE 3 : input={}, index={}", input, index);
    }

    private int processOpCode4(boolean param1) {
        int element = getValueOrReference(param1, 1);
//        log.info("OPCODE 4 : output={}", element);
        return element;
    }

    private int processOpCodes5and6(boolean param1, boolean param2, BoolCommand boolCommand) {
        int value1 = getValueOrReference(param1, 1);
        int value2 = getValueOrReference(param2, 2);
        if (boolCommand.apply(value1)) {
//            log.info("{} : value1={} value2={}", boolCommand.getClass(), value1, value2);
            return value2;
        }
        return pointer + 3;
    }

    private void processOpCodes7and8(boolean param1, boolean param2, boolean param3, CompareCommand compareCommand) {
        int value1 = getValueOrReference(param1, 1);
        int value2 = getValueOrReference(param2, 2);
        int index = param3 ? pointer + 3 : memory.get(pointer + 3);
        int element = compareCommand.apply(value1, value2) ? 1 : 0;
        write(index, element);
//        log.info("{} : value1={}, value2={}, element={}, index={}", compareCommand.getClass(), value1, value2, element, index);
    }

    private void write(int index, int element) {
        memory.add(index, element);
        memory.remove(index + 1);
    }

    private Integer getValueOrReference(boolean value, int offset) {
        return value ? memory.get(pointer + offset) : memory.get(memory.get(pointer + offset));
    }
}