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
        Map<String, Monkey> monkeys = Util.fileStream("advent2022/advent21")
                .map(Monkey::new)
                .collect(Collectors.toMap(monkey -> monkey.id, monkey -> monkey));
        initializeConnections(monkeys);

        Monkey root = monkeys.get(ROOT);
        log.info("Root: {}", root.getValue());

        setHumanAncestors(monkeys);
        calculateHumanBranch(root);

        Monkey human = monkeys.get(HUMAN);
        log.info("Human: {}", human.value);
    }

    private static void initializeConnections(Map<String, Monkey> monkeys) {
        monkeys.values().forEach(monkey -> {
            monkey.left = monkeys.get(monkey.leftId);
            monkey.right = monkeys.get(monkey.rightId);
            if (monkey.left != null) {
                monkey.left.prev = monkey;
            }
            if (monkey.right != null) {
                monkey.right.prev = monkey;
            }
        });
    }

    private static void setHumanAncestors(Map<String, Monkey> monkeys) {
        Monkey current = monkeys.get(HUMAN);
        while (!current.id.equals(ROOT)) {
            current.hasHuman = true;
            current = current.prev;
        }
        Monkey root = monkeys.get(ROOT);
        root.hasHuman = true;
        root.getValue();
    }

    private static void calculateHumanBranch(Monkey root) {
        if (root.left.hasHuman) {
            root.left.value = root.right.value;
            root.left.applyInverse();
        } else if (root.right.hasHuman) {
            root.right.value = root.left.value;
            root.right.applyInverse();
        }
    }

    static class Monkey {
        String id;
        long value = -1;
        String leftId;
        Monkey left;
        String rightId;
        Monkey right;

        String operation;
        boolean hasHuman;
        Monkey prev;

        @Override
        public String toString() {
            return "[" + id + " v=" + value + " l=" + left + ", o=" + operation + " r=" + right + " h=" + hasHuman + "]";
        }

        long getValue() {
            // if uninitialized, calculate
            if (this.value == -1) {
                this.value = applyOperation(left, right);
            }
            return this.value;
        }

        public Monkey(String line) {
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

        private long applyOperation(Monkey left, Monkey right) {
            long leftVal = left.getValue();
            long rightVal = right.getValue();

            long value = -1;
            switch (operation) {
                case "+" -> value = leftVal + rightVal;
                case "-" -> value = leftVal - rightVal;
                case "*" -> value = leftVal * rightVal;
                case "/" -> value = leftVal / rightVal;
            }
            return value;
        }

        private void applyInverse() {
            if (this.id.equals(HUMAN)) {
                return;
            }

            if (left.hasHuman) {
                // left = unknown
                switch (operation) {
                    case "+" -> left.value = this.value - right.value; // V=X+R -> X=V-R
                    case "-" -> left.value = this.value + right.value; // V=X-R -> X=V+R
                    case "*" -> left.value = this.value / right.value; // V=X*R -> X=V/R
                    case "/" -> left.value = this.value * right.value; // V=X/R -> X=V*R
                }
                left.applyInverse();
            } else if (right.hasHuman) {
                // right = unknown
                switch (operation) {
                    case "+" -> right.value = this.value - left.value; // V=L+X -> X=V-L
                    case "-" -> right.value = left.value - this.value; // V=L-X -> X=L-V
                    case "*" -> right.value = this.value / left.value; // V=L*X -> X=V/L
                    case "/" -> right.value = left.value / this.value; // V=L/X -> X=L/V
                }
                right.applyInverse();
            }
        }
    }
}