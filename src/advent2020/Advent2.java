package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class Advent2 {
    public static void main(String[] args) {
        solve(checkCount());
        solve(checkPositions());
    }

    private static void solve(Predicate<String[]> function) {
        long nrOfPasswords = InputUtils.fileStream("advent2020/advent2")
                                 .map(s -> s.split(" "))
                                 .filter(function)
                                 .count();
        log.info("Nr of passwords: {}", nrOfPasswords);
    }

    private static Predicate<String[]> checkCount() {
        return strings -> {
            List<Integer> bounds = getIntegers(strings[0]);
            char letter = strings[1].split(":")[0].charAt(0);
            long count = strings[2].chars()
                                   .filter(ch -> ch == letter)
                                   .count();
            return bounds.get(0) <= count && count <= bounds.get(1);
        };
    }

    private static Predicate<String[]> checkPositions() {
        return strings -> {
            List<Integer> positions = getIntegers(strings[0]);
            char letter = strings[1].split(":")[0].charAt(0);
            return strings[2].charAt(positions.get(0) - 1) == letter ^ strings[2].charAt(positions.get(1) - 1) == letter;
        };
    }

    private static List<Integer> getIntegers(String string) {
        return Arrays.stream(string.split("-"))
                     .map(Integer::parseInt)
                     .collect(Collectors.toList());
    }
}
