package advent2022;

import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import util.Util;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent5 {
    static List<Stack<Character>> stacks = newArrayList();

    public static void main(String[] args) {
        List<String> input = Util.fileStream("advent2022/advent5").collect(Collectors.toList());
        int splitter = 0;
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).contains("1")) {
                splitter = i;
                break;
            }
        }

        List<String> crates = input.subList(0, splitter);
        int numStacks = crates.get(0).length() / 4 + 1;
        for (int i = 0; i < numStacks; i++) {
            stacks.add(new Stack<>());
        }

        crates.forEach(s -> {
            for (int k = 0; k < numStacks; k++) {
                if (StringUtils.isNotBlank(s.charAt(4 * k + 1) + "")) {
                    stacks.get(k).push(s.charAt(4 * k + 1));
                }
            }
        });
        stacks.forEach(Collections::reverse);

        input.subList(splitter + 2, input.size()).forEach(s -> {
            String[] split = s.split(" ");
            int num = Integer.parseInt(split[1]);
            int from = Integer.parseInt(split[3]);
            int to = Integer.parseInt(split[5]);
            multiMoveItems(num, from, to);
        });

        String result = stacks.stream().map(Stack::pop).map(character -> character + "").collect(Collectors.joining());
        log.info("Result: {}", result);
    }

    private static void multiMoveItems(int num, int from, int to) {
        Stack<Character> temp = new Stack<>();
        for (int i = 0; i < num; i++) {
            Character pop = stacks.get(from - 1).pop();
            temp.push(pop);
        }
        for (int i = 0; i < num; i++) {
            Character pop = temp.pop();
            stacks.get(to - 1).push(pop);
        }
    }

    private static void moveItems(int num, int from, int to) {
        for (int i = 0; i < num; i++) {
            Character pop = stacks.get(from - 1).pop();
            stacks.get(to - 1).push(pop);
        }
    }
}
