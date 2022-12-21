package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Advent21 {

    public static void main(String[] args) {
        Map<String, Monkey> monkeys = Util.fileStream("advent2022/advent21")
                .map(Monkey::new)
                .collect(Collectors.toMap(monkey -> monkey.id, monkey -> monkey));

        monkeys.values().forEach(monkey -> {
            monkey.left = monkeys.get(monkey.leftId);
            monkey.right = monkeys.get(monkey.rightId);
        });

        log.info("Root: {}", monkeys.get("root").getValue());
    }

    static class Monkey {
        String id;
        long value = -1;
        String leftId;
        Monkey left;
        String rightId;
        Monkey right;

        String operation;

        long getValue() {
            if (this.value != -1) {
                return value;
            } else {
                return applyOperation(left, right);
            }
        }

        public Monkey(String line) {
            String[] split = line.split(": ");
            this.id = split[0];
            String[] strings = split[1].split(" ");

            if (strings.length == 1) {
                this.value = Long.parseLong(strings[0]);
            } else {
                this.leftId = strings[0];
                this.operation = strings[1];
                this.rightId = strings[2];
            }
        }

        private long applyOperation(Monkey left, Monkey right) {
            long leftVal = left.getValue();
            long rightVal = right.getValue();

            switch (operation) {
                case "*" -> {
                    return leftVal * rightVal;
                }
                case "/" -> {
                    return leftVal / rightVal;
                }
                case "+" -> {
                    return leftVal + rightVal;
                }
                case "-" -> {
                    return leftVal - rightVal;
                }
            }
            log.error("Unknown Operation");
            return -1;
        }
    }
}