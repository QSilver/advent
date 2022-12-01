package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent1 {
    public static void main(String[] args) {
        List<List<Integer>> elves = newArrayList();
        elves.add(newArrayList());

        Util.fileStream("advent2022/advent1").forEach(s -> {
            if (s.isBlank()) {
                elves.add(newArrayList());
            } else {
                elves.get(elves.size() - 1).add(Integer.valueOf(s));
            }
        });

        List<Integer> sorted = elves.stream()
                .sorted((o1, o2) -> Integer.compare(getSum(o2), getSum(o1)))
                .map(Advent1::getSum)
                .collect(Collectors.toList());
        log.info("Max Calories: {}", sorted.get(0));
        log.info("2nd Calories: {}", sorted.get(1));
        log.info("3rd Calories: {}", sorted.get(2));
        log.info("Sum: {}", sorted.get(0) + sorted.get(1) + sorted.get(2));
    }

    private static int getSum(List<Integer> list) {
        return list.stream().mapToInt(i -> i).sum();
    }
}
