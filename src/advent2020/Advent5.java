package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;

@Slf4j
public class Advent5 {
    public static void main(String[] args) {
        IntSummaryStatistics seats = InputUtils.fileStream("advent2020/advent5")
                                         .map(line -> line.chars()
                                                          .map(c -> (c / 7) % 2)
                                                          .mapToObj(String::valueOf)
                                                          .collect(Collectors.joining()))
                                         .map(binaryLine -> Integer.parseInt(binaryLine, 2))
                                         .collect(Collectors.summarizingInt(Integer::intValue));
        log.info("Max seat: {}", seats.getMax());
        log.info("{}", ((seats.getMax() + seats.getMin()) * (seats.getCount() + 1) / 2) - seats.getSum());
    }

}