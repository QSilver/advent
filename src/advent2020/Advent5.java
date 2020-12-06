package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Advent5 {
    public static void main(String[] args) {
        List<Integer> seats = Util.fileStream("advent2020/advent5")
                                  .map(s -> s.replace("F", "0")
                                             .replace("B", "1")
                                             .replace("L", "0")
                                             .replace("R", "1"))
                                  .map(strings -> Integer.parseInt(strings, 2))
                                  .sorted()
                                  .collect(Collectors.toList());
        Integer max = seats.get(seats.size() - 1);
        Integer min = seats.get(0);
        log.info("Max seat: {}", max);
        int sum = seats.stream()
                       .mapToInt(Integer::intValue)
                       .sum();
        log.info("{}", ((min + max) * (seats.size() + 1) / 2) - sum);
    }
}
