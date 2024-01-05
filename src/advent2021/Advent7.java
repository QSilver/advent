package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Advent7 {
    public static void main(String[] args) {
        List<Integer> crabs = InputUtils.fileStream("advent2021/advent7")
                                  .map(s -> s.split(","))
                                  .flatMap(Arrays::stream)
                                  .map(Integer::parseInt)
                                  .collect(Collectors.toList());

        double median = crabs.stream()
                             .sorted()
                             .skip(Math.max(0, (((long) crabs.size() + 1) >> 1) - 1))
                             .limit(1 + (1 + (long) crabs.size() & 1))
                             .mapToInt(Integer::intValue)
                             .average()
                             .getAsDouble();
        log.info("median: {}", median);

        int fuel_linear = crabs.stream()
                               .map(integer -> Math.abs(integer - (int) median))
                               .mapToInt(value -> value)
                               .sum();
        log.info("P1: {}", fuel_linear);

        double mean = crabs.stream()
                           .mapToInt(Integer::intValue)
                           .average()
                           .getAsDouble();
        log.info("mean: {}", mean);

        int fuel_gauss = crabs.stream()
                              .map(integer -> {
                                  int a = Math.abs(integer - (int) mean);
                                  return a * (a + 1) >> 1;
                              })
                              .sorted()
                              .mapToInt(value -> value)
                              .sum();
        log.info("P2: {}", fuel_gauss);
    }
}
