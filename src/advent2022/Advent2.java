package advent2022;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 12701 - too high
@Slf4j
public class Advent2 {
    static final Map<String, Integer> scoreMap = ImmutableMap.<String, Integer>builder()
            .put("A X", 1 + 3)
            .put("A Y", 2 + 6)
            .put("A Z", 3 + 0)
            .put("B X", 1 + 0)
            .put("B Y", 2 + 3)
            .put("B Z", 3 + 6)
            .put("C X", 1 + 6)
            .put("C Y", 2 + 0)
            .put("C Z", 3 + 3)
            .build();

    static final Map<String, Integer> modifiedScoreMap = ImmutableMap.<String, Integer>builder()
            .put("A X", 3 + 0)
            .put("A Y", 1 + 3)
            .put("A Z", 2 + 6)
            .put("B X", 1 + 0)
            .put("B Y", 2 + 3)
            .put("B Z", 3 + 6)
            .put("C X", 2 + 0)
            .put("C Y", 3 + 3)
            .put("C Z", 1 + 6)
            .build();

    public static void main(String[] args) {
        List<String> input = InputUtils.fileStream("advent2022/advent2").collect(Collectors.toList());
        int sum = input.stream().map(scoreMap::get).mapToInt(value -> value).sum();
        int modifiedSum = input.stream().map(modifiedScoreMap::get).mapToInt(value -> value).sum();
        log.info("Sum: {}", sum);
        log.info("ModifiedSum: {}", modifiedSum);
    }
}
