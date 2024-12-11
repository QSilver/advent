package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.Map;
import java.util.stream.IntStream;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static util.InputUtils.readSplitLineNumbers;

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
        Map<Long, Long> stoneCounts = readSplitLineNumbers(file)
                .collect(groupingBy(identity(), counting()));

        IntStream.range(0, blinks).forEach(_ -> {
            Map<Long, Long> next = newHashMap();
            stoneCounts.keySet().forEach(stone -> blinkStone(stone, stoneCounts, next));
            stoneCounts.clear();
            stoneCounts.putAll(next);
        });

        return stoneCounts.values().stream()
                .collect(summingLong(value -> value));
    }

    private static void blinkStone(Long value, Map<Long, Long> numberCounts, Map<Long, Long> next) {
        String stringValue = String.valueOf(value);
        Long count = numberCounts.get(value);

        if (value == 0) {
            next.merge(1L, count, Long::sum);
        } else if (stringValue.length() % 2 == 0) {
            int midpoint = stringValue.length() / 2;
            next.merge(Long.parseLong(stringValue.substring(0, midpoint)), count, Long::sum);
            next.merge(Long.parseLong(stringValue.substring(midpoint)), count, Long::sum);
        } else {
            next.merge(value * 2024, count, Long::sum);
        }
    }
}
