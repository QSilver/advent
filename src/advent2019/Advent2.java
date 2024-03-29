package advent2019;

import com.google.common.collect.Lists;
import util.InputUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Advent2 {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (solve(i, j) == 19690720) {
                    System.out.println(100 * i + j);
                }
            }
        }
    }

    private static int solve(int value1, int value2) {
        ArrayList<Integer> strings = InputUtils.splitLine(InputUtils.fileStream("advent2019/advent2"))
                                         .stream()
                                         .map(Integer::parseInt)
                                         .collect(Collectors.toCollection(Lists::newArrayList));
        replace(strings, value1, value2);

        for (int i = 0; i < strings.size() && strings.get(i) != 99; i += 4) {
            if (strings.get(i) == 1) {
                processOpCode1(strings, i);
            } else if (strings.get(i) == 2) {
                processOpCode2(strings, i);
            }
        }

        return strings.get(0);
    }

    private static void replace(ArrayList<Integer> input, int value1, int value2) {
        input.add(1, value1);
        input.remove(2);
        input.add(2, value2);
        input.remove(3);
    }

    private static void processOpCode1(ArrayList<Integer> strings, int i) {
        int element = strings.get(strings.get(i + 1)) + strings.get(strings.get(i + 2));
        int index = strings.get(i + 3);
        strings.add(index, element);
        strings.remove(index + 1);
    }

    private static void processOpCode2(ArrayList<Integer> strings, int i) {
        int element = strings.get(strings.get(i + 1)) * strings.get(strings.get(i + 2));
        int index = strings.get(i + 3);
        strings.add(index, element);
        strings.remove(index + 1);
    }
}
