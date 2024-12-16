package advent2023;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.stream;
import static util.InputUtils.readDoubleNewlineBlocks;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent8 {
    // https://adventofcode.com/2023/day/8
    static String instructions;
    static Map<String, Node> nodeMap;

    public Long runP1(String file) {
        parseInput(file);
        return getSteps(notIsZZZ(), "AAA");
    }

    public Long runP2(String file) {
        parseInput(file);

        List<Long> lengthsOfTrip = nodeMap.keySet().stream()
                .filter(label -> label.endsWith("A"))
                .map(node -> getSteps(notEndsWithZ(), node))
                .toList();

        return lcm(lengthsOfTrip);
    }

    private static long getSteps(Function<String, Boolean> endCondition, String current) {
        long steps = 0;
        while (endCondition.apply(current)) {
            int pointer = (int) (steps % instructions.length());
            char direction = instructions.charAt(pointer);
            if (direction == 'R') {
                current = nodeMap.get(current).right;
            } else {
                current = nodeMap.get(current).left;
            }
            steps++;
        }
        return steps;
    }

    private Function<String, Boolean> notIsZZZ() {
        return node -> !node.equals("ZZZ");
    }

    private Function<String, Boolean> notEndsWithZ() {
        return node -> !node.endsWith("Z");
    }

    private void parseInput(String file) {
        String[] strings = readDoubleNewlineBlocks(file);
        instructions = strings[0];
        nodeMap = getNodeMap(strings[1].split("\n"));
    }

    private Map<String, Node> getNodeMap(String[] nodeLines) {
        Map<String, Node> nodeMap = newHashMap();
        stream(nodeLines).forEach(line -> {
            String[] split = line.stringRemove(",", "(", ")", " =").split(" ");
            nodeMap.put(split[0], new Node(split[0], split[1], split[2]));
        });
        return nodeMap;
    }

    private long lcm(List<Long> input) {
        long result = input.getFirst();
        for (int i = 1; i < input.size(); i++) {
            result = lcm(result, input.get(i));
        }
        return result;
    }

    private long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    private long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    record Node(String label, String left, String right) {
    }
}
