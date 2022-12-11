package advent2022;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Queues.newArrayDeque;

@Slf4j
public class Advent11 {
    static List<Monkey> monkeys = newArrayList();

    public static void main(String[] args) {
        List<String> input = Util.fileStream("advent2022/advent11").collect(Collectors.toList());

        for (int i = 0; i < input.size(); i += 7) {
            int id = Integer.parseInt(input.get(i).split(" ")[1].split(":")[0]);
            Queue<Long> startingItems = newArrayDeque(Arrays.stream(input.get(i + 1).split(": ")[1]
                    .split(", ")).map(Long::parseLong).collect(Collectors.toList()));

            String[] op = input.get(i + 2).split("= ")[1].split(" ");
            Function<Long, Long> operation = old -> {
                long value;
                try {
                    value = Integer.parseInt(op[2]);
                } catch (NumberFormatException e) {
                    value = old;
                }

                if (op[1].equals("+")) {
                    return old + value;
                } else if (op[1].equals("*")) {
                    return old * value;
                } else {
                    return old;
                }
            };

            int divisibleTest = Integer.parseInt(input.get(i + 3).split("by ")[1]);
            int trueId = Integer.parseInt(input.get(i + 4).split("monkey ")[1]);
            int falseId = Integer.parseInt(input.get(i + 5).split("monkey ")[1]);

            monkeys.add(new Monkey(id, startingItems, operation, divisibleTest, trueId, falseId));
        }

        log.info("Here");
    }

    @AllArgsConstructor
    static class Monkey {
        int id;
        Queue<Long> items;
        Function<Long, Long> operation;
        int divisibleBy;
        int trueId;
        int falseId;
    }
}
