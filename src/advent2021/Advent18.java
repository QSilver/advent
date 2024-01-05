package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent18 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        List<String> inputs = InputUtils.fileStream("advent2021/advent18")
                                  .collect(Collectors.toList());
        List<List<Snailfish>> numbers = inputs.stream()
                                              .map(Advent18::getSnailfish)
                                              .collect(Collectors.toList());
        solveP2(numbers);
        log.info("{} ms", System.currentTimeMillis() - start);
    }

    private static void solveP1(List<List<Snailfish>> numbers) {
        List<Snailfish> runningTotal = numbers.remove(0);
        numbers.forEach(number -> {
            add(runningTotal, number);
            cascade(runningTotal);
        });

        long magnitude = magnitude(runningTotal);
        log.info("P1: {}", magnitude);
    }

    private static void solveP2(List<List<Snailfish>> numbers) {
        List<Long> magnitudeList = newArrayList();

        for (int i = 0; i < numbers.size(); i++) {
            for (int j = 0; j < numbers.size(); j++) {
                if (i != j) {
                    List<Snailfish> number1Copy = numbers.get(i)
                                                         .stream()
                                                         .map(Snailfish::copy)
                                                         .collect(Collectors.toList());
                    List<Snailfish> number2Copy = numbers.get(j)
                                                         .stream()
                                                         .map(Snailfish::copy)
                                                         .collect(Collectors.toList());

                    add(number1Copy, number2Copy);
                    cascade(number1Copy);
                    long magnitude = magnitude(number1Copy);
                    magnitudeList.add(magnitude);
                }
            }
        }

        long max = magnitudeList.stream()
                                .mapToLong(value -> value)
                                .max()
                                .orElse(-1);
        log.info("P2: {}", max);
    }

    private static List<Snailfish> getSnailfish(String input) {
        List<Snailfish> list = newArrayList();

        int stackDepth = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '[') {
                stackDepth++;
            } else if (c == ']') {
                stackDepth--;
            } else if (Character.isDigit(c)) {
                list.add(new Snailfish(Integer.parseInt(String.valueOf(c)), stackDepth));
            }
        }
        return list;
    }

    private static long magnitude(List<Snailfish> number) {
        while (number.size() > 1) {
            int maxDepth = number.stream()
                                 .map(snailfish -> snailfish.depth)
                                 .mapToInt(value -> value)
                                 .max()
                                 .orElse(-1);

            for (int d = maxDepth; d > 0; d--) {
                for (int i = 0; i < number.size() - 1; i++) {
                    if (number.get(i).depth == maxDepth && number.get(i).depth == number.get(i + 1).depth) {
                        number.get(i).value = 3 * number.get(i).value + 2 * number.get(i + 1).value;
                        number.get(i).depth--;
                        number.remove(i + 1);
                    }
                }
            }
        }
        return number.get(0).value;
    }

    private static void cascade(List<Snailfish> number) {
        while (true) {
            boolean explode = false;
            while (explode(number)) {
                explode = true;
            }
            boolean split = split(number);
            if (!split && !explode) {
                return;
            }
        }
    }

    private static boolean explode(List<Snailfish> number) {
        for (int i = 0; i < number.size() - 1; i++) {
            if (number.get(i).depth == number.get(i + 1).depth && number.get(i).depth > 4) {
                if (i > 0) {
                    number.get(i - 1).value += number.get(i).value;
                }
                if (i < number.size() - 2) {
                    number.get(i + 2).value += number.get(i + 1).value;
                }
                number.remove(i + 1);
                number.get(i).value = 0;
                number.get(i).depth--;
                return true;
            }
        }
        return false;
    }

    private static boolean split(List<Snailfish> number) {
        for (int i = 0; i < number.size(); i++) {
            long oldV = number.get(i).value;
            int oldD = number.get(i).depth;
            if (oldV > 9) {
                number.get(i).depth++;
                number.get(i).value = (long) Math.floor(oldV / 2.0);
                number.add(i + 1, new Snailfish((long) Math.ceil(oldV / 2.0), oldD + 1));
                return true;
            }
        }
        return false;
    }

    private static void add(List<Snailfish> number1, List<Snailfish> number2) {
        number1.forEach(snailfish -> snailfish.depth++);
        number2.forEach(snailfish -> snailfish.depth++);
        number1.addAll(number2);
    }

    static class Snailfish {
        long value;
        int depth;

        public Snailfish(long value, int depth) {
            this.value = value;
            this.depth = depth;
        }

        @Override
        public String toString() {
            return value + "/" + depth;
        }

        public Snailfish copy() {
            return new Snailfish(value, depth);
        }
    }
}
