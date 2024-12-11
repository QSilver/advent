package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.joining;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent11 {
    // https://adventofcode.com/2024/day/11

    public Long runP1(String file) {
        return run(file, 25);
    }

    public Long runP2(String file) {
        return run(file, 75);
    }

    private static long run(String file, int blinks) {
        Map<Long, Long> numberCounts = Arrays.stream(fileStream(file)
                        .collect(joining())
                        .split(" "))
                .mapToLong(Long::parseLong).boxed()
                .collect(Collectors.groupingBy(identity(), counting()));

        for (int blink = 1; blink <= blinks; blink++) {
            Map<Long, Long> next = newHashMap();

            numberCounts.keySet().forEach(value -> {
                String stringValue = String.valueOf(value);

                if (value == 0) {
                    next.merge(1L, numberCounts.get(value), Long::sum);
                } else if (stringValue.length() % 2 == 0) {
                    int midpoint = stringValue.length() / 2;
                    next.merge(Long.parseLong(stringValue.substring(0, midpoint)), numberCounts.get(value), Long::sum);
                    next.merge(Long.parseLong(stringValue.substring(midpoint)), numberCounts.get(value), Long::sum);
                } else {
                    next.merge(value * 2024, numberCounts.get(value), Long::sum);
                }
            });

            numberCounts.clear();
            numberCounts.putAll(next);
        }

        return numberCounts.values().stream()
                .mapToLong(value -> value)
                .sum();
    }
}
