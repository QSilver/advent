package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Queues.newArrayDeque;

// 1052801021 - too low
@Slf4j
public class Advent11 {
    static List<Monkey> monkeys = newArrayList();
    static BigInteger LCM = BigInteger.ONE;
    static List<Integer> divisibleBy = newArrayList();

    public static void main(String[] args) {
        List<String> input = Util.fileStream("advent2022/advent11").collect(Collectors.toList());

        for (int i = 0; i < input.size(); i += 7) {
            int id = Integer.parseInt(input.get(i).split(" ")[1].split(":")[0]);
            Queue<BigInteger> startingItems = newArrayDeque(Arrays.stream(input.get(i + 1).split(": ")[1]
                    .split(", ")).map(BigInteger::new).collect(Collectors.toList()));

            String[] op = input.get(i + 2).split("= ")[1].split(" ");
            Function<BigInteger, BigInteger> operation = old -> {
                BigInteger value;
                try {
                    value = new BigInteger(op[2]);
                } catch (NumberFormatException e) {
                    value = old;
                }

                if (op[1].equals("+")) {
                    return old.add(value);
                } else if (op[1].equals("*")) {
                    return old.multiply(value);
                } else {
                    return old;
                }
            };

            int divisibleTest = Integer.parseInt(input.get(i + 3).split("by ")[1]);
            divisibleBy.add(divisibleTest);
            int trueId = Integer.parseInt(input.get(i + 4).split("monkey ")[1]);
            int falseId = Integer.parseInt(input.get(i + 5).split("monkey ")[1]);

            monkeys.add(new Monkey(id, startingItems, operation, divisibleTest, trueId, falseId));
        }

        LCM = BigInteger.valueOf(divisibleBy.stream().mapToInt(value -> value).reduce((left, right) -> left * right).getAsInt());

        for (int round = 1; round <= 10000; round++) {
            monkeys.forEach(Monkey::takeTurn);
        }

        List<Integer> inspections = monkeys.stream().map(monkey -> monkey.inspections).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        log.info("Monkey Business: {}", BigInteger.valueOf(inspections.get(0)).multiply(BigInteger.valueOf(inspections.get(1))));
    }

    static class Monkey {
        int id;
        Queue<BigInteger> items;
        Function<BigInteger, BigInteger> operation;
        int divisibleBy;
        int trueId;
        int falseId;

        public Monkey(int id, Queue<BigInteger> items, Function<BigInteger, BigInteger> operation, int divisibleBy, int trueId, int falseId) {
            this.id = id;
            this.items = items;
            this.operation = operation;
            this.divisibleBy = divisibleBy;
            this.trueId = trueId;
            this.falseId = falseId;
        }

        int inspections;

        void takeTurn() {
            while (this.items.size() > 0) {
                BigInteger inspect = operation.apply(items.poll()); // .divide(BigInteger.valueOf(3))
                inspections++;
                if (inspect.mod(BigInteger.valueOf(divisibleBy)).equals(BigInteger.ZERO)) {
                    throwToMonkey(trueId, inspect.mod(LCM));
                } else {
                    throwToMonkey(falseId, inspect.mod(LCM));
                }
            }
        }

        void throwToMonkey(int id, BigInteger item) {
            monkeys.get(id).items.add(item);
        }

        @Override
        public String toString() {
            return id + "-" + items + "-" + inspections;
        }
    }
}
