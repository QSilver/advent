package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent10 {

    static Map<Character, Character> complement = Map.of('(', ')', '[', ']', '{', '}', '<', '>');
    static Map<Character, Integer> corruptedScores = Map.of(')', 3, ']', 57, '}', 1197, '>', 25137);
    static Map<Character, Integer> endingScores = Map.of(')', 1, ']', 2, '}', 3, '>', 4);

    public static void main(String[] args) {
        AtomicInteger points = new AtomicInteger();
        List<Long> scores = newArrayList();

        InputUtils.fileStream("advent2021/advent10")
            .forEach(line -> {
                boolean isCorrupted = false;
                Stack<Character> charStack = new Stack<>();
                for (char c : line.toCharArray()) {
                    if (complement.containsKey(c)) {
                        charStack.push(c);
                    } else if (complement.get(charStack.peek()) == c) {
                        charStack.pop();
                    } else {
                        points.addAndGet(corruptedScores.get(c));
                        isCorrupted = true;
                        break;
                    }
                }
                if (!isCorrupted) {
                    long tempScore = 0;
                    while (!charStack.empty()) {
                        tempScore = tempScore * 5 + endingScores.get(complement.get(charStack.pop()));
                    }
                    scores.add(tempScore);
                }
            });

        log.info("P1: {}", points);
        log.info("P2: {}", scores.stream()
                                 .sorted()
                                 .collect(Collectors.toList())
                                 .get(scores.size() / 2));
    }
}
