package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent6 {
    private static final int FINAL_DAY = 256;

    public static void main(String[] args) {
        List<Integer> collect = Util.fileStream("advent2021/advent6")
                                    .map(s -> s.split(","))
                                    .flatMap(Arrays::stream)
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());

        Map<Integer, Long> fish = newHashMap();
        collect.forEach(integer -> fish.merge(integer, 1L, Long::sum));

        IntStream.range(0, FINAL_DAY)
                 .forEach(day -> {
                     Map<Integer, Long> newFish = getNextDay(fish);
                     fish.clear();
                     fish.putAll(newFish);
                     logDay(fish, day);
                 });
    }

    private static void logDay(Map<Integer, Long> fish, int day) {
        log.info("After day {} - count: {}", day + 1, fish.values()
                                                          .stream()
                                                          .mapToLong(value -> value)
                                                          .sum());
    }

    private static Map<Integer, Long> getNextDay(Map<Integer, Long> fish) {
        Map<Integer, Long> newFish = newHashMap();
        fish.forEach((key, value) -> {
            if (key == 0) {
                newFish.merge(6, value, Long::sum);
                newFish.merge(8, value, Long::sum);
            } else {
                newFish.merge(key - 1, value, Long::sum);
            }
        });
        return newFish;
    }
}
