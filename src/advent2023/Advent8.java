package advent2023;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.collect.Maps.newHashMap;
import static util.Util.fileStream;

@Slf4j
public class Advent8 {
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
        List<String> list = fileStream(file).toList();
        instructions = list.getFirst();
        nodeMap = getNodeMap(list);
    }

    private Map<String, Node> getNodeMap(List<String> list) {
        Map<String, Node> nodeMap = newHashMap();
        list.subList(2, list.size()).forEach(line -> {
            line = line.replace(",", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace(" =", "");
            String[] split = line.split(" ");
            String label = split[0];
            String left = split[1];
            String right = split[2];
            nodeMap.put(label, new Node(right, left, label));
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

    record Node(String right, String left, String label) {
    }
}
