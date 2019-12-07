package advent;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

public class Advent5 {
    private static final int INPUT = 1;

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Integer> strings = newArrayList(splitLine(Util.fileStream("advent5")).stream()
                                                                                       .map(Integer::parseInt)
                                                                                       .collect(Collectors.toList()));

        for (int i = 0; i < strings.size() && strings.get(i) != 99; ) {
            int opCode = strings.get(i) % 100;

            int params = strings.get(i) / 100;
            boolean param1 = params % 100 % 10 != 0;
            boolean param2 = params / 10 % 10 != 0;
            boolean param3 = params / 100 != 0;

            if (opCode == 1) {
                processOpCodes1and2(strings, i, param1, param2, param3, new Add());
                i += 4;
            } else if (opCode == 2) {
                processOpCodes1and2(strings, i, param1, param2, param3, new Multiply());
                i += 4;
            } else if (opCode == 3) {
                processOpCode3(strings, i);
                i += 2;
            } else if (opCode == 4) {
                processOpCode4(strings, i);
                i += 2;
            }
        }
    }

    private static void processOpCodes1and2(ArrayList<Integer> strings, int i, boolean param1, boolean param2, boolean param3, Command command) {
        int value1 = param1 ? strings.get(i + 1) : strings.get(strings.get(i + 1));
        int value2 = param2 ? strings.get(i + 2) : strings.get(strings.get(i + 2));
        int index = param3 ? i + 3 : strings.get(i + 3);
        int element = command.apply(value1, value2);
        strings.add(index, element);
        strings.remove(index + 1);
    }

    private static void processOpCode3(ArrayList<Integer> strings, int i) {
        int index = strings.get(i + 1);
        strings.add(index, INPUT);
        strings.remove(index + 1);
    }

    private static void processOpCode4(ArrayList<Integer> strings, int i) {
        System.out.println("OPCODE 4: " + strings.get(strings.get(i + 1)));
    }

    private static ArrayList<String> splitLine(Stream<String> stream) {
        Optional<String> first = stream.findFirst();
        if (first.isEmpty()) {
            return newArrayList();
        }
        return newArrayList(first.get()
                                 .split(","));
    }
}

class Multiply implements Command {
    @Override
    public int apply(int val1, int val2) {
        return val1 * val2;
    }
}

class Add implements Command {
    @Override
    public int apply(int val1, int val2) {
        return val1 + val2;
    }
}

interface Command {
    int apply(int val1, int val2);
}