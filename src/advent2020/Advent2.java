package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Advent2 {
    public static void main(String[] args) {
        solvePart2();
    }

    private static void solvePart1() {
        long nrOfPasswords = Util.fileStream("advent2020/advent2")
                                 .map(s -> s.split(" "))
                                 .filter(strings -> {
                                     List<Integer> bounds = Arrays.stream(strings[0].split("-"))
                                                                  .map(Integer::parseInt)
                                                                  .collect(Collectors.toList());
                                     char letter = strings[1].split(":")[0].charAt(0);
                                     long count = strings[2].chars()
                                                            .filter(ch -> ch == letter)
                                                            .count();
                                     return bounds.get(0) <= count && count <= bounds.get(1);
                                 })
                                 .count();
        log.info("Nr of passwords: {}", nrOfPasswords);
    }

    private static void solvePart2() {
        long nrOfPasswords = Util.fileStream("advent2020/advent2")
                                 .map(s -> s.split(" "))
                                 .filter(strings -> {
                                     List<Integer> positions = Arrays.stream(strings[0].split("-"))
                                                                     .map(Integer::parseInt)
                                                                     .collect(Collectors.toList());
                                     char letter = strings[1].split(":")[0].charAt(0);
                                     return strings[2].charAt(positions.get(0) - 1) == letter ^ strings[2].charAt(positions.get(1) - 1) == letter;
                                 })
                                 .count();
        log.info("Nr of passwords: {}", nrOfPasswords);
    }
}
