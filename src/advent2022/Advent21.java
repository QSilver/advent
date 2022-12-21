package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Advent21 {

    public static final String HUMAN = "humn";
    public static final String ROOT = "root";

    public static void main(String[] args) {
        part1();
        part2();
    }

    private static void part2() {
        Map<String, MonkeyPart2> monkeysPart2 = Util.fileStream("advent2022/advent21")
                .map(MonkeyPart2::new)
                .collect(Collectors.toMap(monkey -> monkey.id, monkey -> monkey));

        monkeysPart2.values().forEach(monkey -> {
            monkey.left = monkeysPart2.get(monkey.leftId);
            monkey.right = monkeysPart2.get(monkey.rightId);
            if (monkey.left != null) {
                monkey.left.prev = monkey;
            }
            if (monkey.right != null) {
                monkey.right.prev = monkey;
            }
        });

        MonkeyPart2 current = monkeysPart2.get(HUMAN);
        while (!current.id.equals(ROOT)) {
            current.hasHuman = true;
            current = current.prev;
        }
        MonkeyPart2 root = monkeysPart2.get(ROOT);
        root.hasHuman = true;
        root.getValue();

        if (root.left.hasHuman) {
            root.left.value = root.right.value;
            root.left.applyInverse();
        } else if (root.right.hasHuman) {
            root.right.value = root.left.value;
            root.right.applyInverse();
        }

        MonkeyPart2 human = monkeysPart2.get(HUMAN);
        log.info("Human: {}", human.value);
    }

    private static void part1() {
        Map<String, Monkey> monkeys = Util.fileStream("advent2022/advent21")
                .map(Monkey::new)
                .collect(Collectors.toMap(monkey -> monkey.id, monkey -> monkey));

        monkeys.values().forEach(monkey -> {
            monkey.left = monkeys.get(monkey.leftId);
            monkey.right = monkeys.get(monkey.rightId);
        });

        log.info("Root: {}", monkeys.get(ROOT).getValue());
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

    static class MonkeyPart2 {
        String id;
        long value = -1;
        String leftId;
        MonkeyPart2 left;
        String rightId;
        MonkeyPart2 right;

        String operation;
        boolean hasHuman;
        MonkeyPart2 prev;

        @Override
        public String toString() {
            return "[" + id + " v=" + value + " l=" + left + ", o=" + operation + " r=" + right + " h=" + hasHuman + "]";
        }

        long getValue() {
            if (this.value == -1) {
                this.value = applyOperation(left, right);
            }
            return this.value;
        }

        public MonkeyPart2(String line) {
            String[] split = line.split(": ");
            this.id = split[0];
            String[] strings = split[1].split(" ");

            if (this.id.equals(HUMAN)) {
                this.hasHuman = true;
                this.value = -2;
            }

            if (strings.length == 1) {
                this.value = Long.parseLong(strings[0]);
            } else {
                this.leftId = strings[0];
                this.operation = strings[1];
                this.rightId = strings[2];
            }
        }

        private long applyOperation(MonkeyPart2 left, MonkeyPart2 right) {
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

        private void applyInverse() {
            if (this.id.equals(HUMAN)) {
                return;
            }

            if (left.hasHuman) {
                switch (operation) {
                    case "+" -> left.value = this.value - right.value;
                    case "-" -> left.value = this.value + right.value;
                    case "*" -> left.value = this.value / right.value;
                    case "/" -> left.value = this.value * right.value;
                }
                left.applyInverse();
            } else if (right.hasHuman) {
                switch (operation) {
                    case "+" -> right.value = this.value - left.value;
                    case "-" -> right.value = left.value - this.value;
                    case "*" -> right.value = this.value / left.value;
                    case "/" -> right.value = left.value / this.value;
                }
                right.applyInverse();
            }
        }
    }
}