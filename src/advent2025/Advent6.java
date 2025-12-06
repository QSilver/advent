package advent2025;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent6 {
    // https://adventofcode.com/2025/day/6

    public Long runP1(String file) {
        return getConvertedInput(file).stream()
                .map(line -> {
                    char sign = line.getLast().charAt(0);
                    long accumulator = sign == '+' ? 0 : 1;

                    for (int i = 0; i < line.size() - 1; i++) {
                        long number = Long.parseLong(line.get(i));
                        if (sign == '+') {
                            accumulator += number;
                        } else if (sign == '*') {
                            accumulator = accumulator * number;
                        }
                    }
                    return accumulator;
                })
                .mapToLong(Long::longValue)
                .sum();
    }

    public Long runP2(String file) {
        List<String> list = fileStream(file).toList();

        List<Integer> signPositions = newArrayList();
        for (int i = 0; i < list.getLast().length(); i++) {
            if (list.getLast().charAt(i) == '+' || list.getLast().charAt(i) == '*') {
                signPositions.add(i);
            }
        }

        int max = list.stream().mapToInt(String::length).max().orElse(0) + 1;

        long globalAccumulator = 0L;
        for (int index = 0; index < signPositions.size(); index++) {
            int signPos = signPositions.get(index);

            char sign = list.getLast().charAt(signPos);
            long accumulator = sign == '+' ? 0 : 1;

            int endIndex = (index + 1 < signPositions.size() ? signPositions.get(index + 1) : max) - 1;
            log.info("Processing from {} to {}", signPos, endIndex);

            for (int i = signPos; i < endIndex; i++) {
                List<Character> number = newArrayList();

                for (int j = 0; j < list.size() - 1; j++) {
                    if (i < list.get(j).length()) {
                        number.add(list.get(j).charAt(i));
                    }
                }

                String string = number.stream()
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                        .toString().trim();
                long num = Long.parseLong(string);
                log.info("Number: {}", num);

                if (sign == '+') {
                    accumulator += num;
                } else if (sign == '*') {
                    accumulator = accumulator * num;
                }
            }

            log.info("Accumulator for sign {}: {}", sign, accumulator);
            globalAccumulator += accumulator;
        }

        return globalAccumulator;
    }

    private static List<List<String>> getConvertedInput(String file) {
        List<List<String>> input = fileStream(file)
                .map(line -> line.split(" "))
                .map(strings -> Arrays.stream(strings)
                        .filter(s -> !s.isBlank())
                        .toList())
                .toList();

        List<List<String>> converted = newArrayList();

        for (List<String> strings : input) {
            for (int j = 0; j < strings.size(); j++) {
                String val = strings.get(j);
                if (j >= converted.size()) {
                    converted.add(newArrayList());
                }
                converted.get(j).add(val);
            }
        }
        return converted;
    }
}
