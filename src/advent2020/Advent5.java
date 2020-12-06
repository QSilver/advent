package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Advent5 {
    public static void main(String[] args) {
        List<Integer> seats = Util.fileStream("advent2020/advent5")
                                  .map(s -> s.chars()
                                             .map(c -> (c / 7) % 2)
                                             .mapToObj(String::valueOf)
                                             .collect(Collectors.joining()))
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
