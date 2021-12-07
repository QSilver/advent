package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
public class Advent7 {
    public static void main(String[] args) {
        Stream<Integer> crabs = Util.fileStream("advent2021/advent7")
                                    .map(s -> s.split(","))
                                    .flatMap(Arrays::stream)
                                    .map(Integer::parseInt);

        double median = crabs.sorted()
                             .skip(Math.max(0, ((crabs.count() + 1) / 2) - 1))
                             .limit(1 + (1 + crabs.count()) % 2)
                             .mapToInt(Integer::intValue)
                             .average()
                             .getAsDouble();
        log.info("median: {}", median);

        int fuel_linear = crabs
                .map(integer -> Math.abs(integer - (int) median))
                .mapToInt(value -> value)
                .sum();
        log.info("P1: {}", fuel_linear);

        double mean = crabs
                .mapToInt(Integer::intValue)
                .average()
                .getAsDouble();
        log.info("mean: {}", mean);

        int fuel_gauss = crabs
                .map(integer -> {
                    int a = Math.abs(integer - (int) mean);
                    return a * (a + 1) / 2;
                })
                .sorted()
                .mapToInt(value -> value)
                .sum();
        log.info("P2: {}", fuel_gauss);
    }
}
