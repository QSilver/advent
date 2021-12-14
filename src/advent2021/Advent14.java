package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent14 {
    private static final int STEPS = 40;

    public static void main(String[] args) {
        List<String> lines = Util.fileStream("advent2021/advent14")
                                 .collect(Collectors.toList());
        String base = lines.remove(0);
        lines.remove(0);

        Map<String, String> reactions = lines.stream()
                                             .map(s -> s.split(" -> "))
                                             .collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));

        Map<String, Long> pairs = formBasePairs(base);
        for (int step = 1; step <= STEPS; step++) {
            log.info("Step: {}", step);
            Map<String, Long> newPairs = newHashMap();
            pairs.forEach((pair, count) -> {
                if (reactions.containsKey(pair)) {
                    String[] split = pair.split("");
                    newPairs.merge(split[0] + reactions.get(pair), count, Long::sum);
                    newPairs.merge(reactions.get(pair) + split[1], count, Long::sum);
                }
            });
            pairs.clear();
            pairs.putAll(newPairs);
        }

        long result = countPairs(pairs);
        log.info("P1: {}", result);
    }

    private static long countPairs(Map<String, Long> pairs) {
        Map<Character, Long> charMap = newHashMap();
        pairs.forEach((pair, count) -> {
            String[] split = pair.split("");
            charMap.merge(split[0].charAt(0), count, Long::sum);
            charMap.merge(split[1].charAt(0), count, Long::sum);
        });

        long max = (long) Math.ceil(charMap.values()
                                           .stream()
                                           .max(Long::compareTo)
                                           .orElse(-1L) / 2.0);
        long min = (long) Math.ceil(charMap.values()
                                           .stream()
                                           .min(Long::compareTo)
                                           .orElse(-1L) / 2.0);
        return max - min;
    }

    private static Map<String, Long> formBasePairs(String base) {
        Map<String, Long> pairs = newHashMap();
        for (int c = 1; c < base.length(); c++) {
            pairs.merge(String.valueOf(base.charAt(c - 1)) +
                    base.charAt(c), 1L, Long::sum);
        }
        return pairs;
    }
}
