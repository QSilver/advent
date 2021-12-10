package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent10 {

    static Set<Character> open = newHashSet('(', '[', '{', '<');
    static Map<Character, Character> complement = newHashMap();

    public static void main(String[] args) {
        complement.put('(', ')');
        complement.put('[', ']');
        complement.put('{', '}');
        complement.put('<', '>');

        List<Character> corrupted = newArrayList();
        List<String> endings = newArrayList();
        Util.fileStream("advent2021/advent10")
            .forEach(line -> {
                boolean isCorrupted = false;
                Stack<Character> charStack = new Stack<>();
                for (char c : line.toCharArray()) {
                    if (open.contains(c)) {
                        charStack.push(c);
                    } else if (complement.get(charStack.peek()) == c) {
                        charStack.pop();
                    } else {
                        corrupted.add(c);
                        isCorrupted = true;
                        break;
                    }
                }
                if (!isCorrupted) {
                    StringBuilder ending = new StringBuilder();
                    charStack.forEach(c -> ending.append(complement.get(c)));
                    endings.add(ending.reverse()
                                      .toString());
                }
            });

        int points = calculateCorruptedScores(corrupted);
        log.info("P1: {}", points);

        List<Long> scores = calculateEndingScores(endings);
        log.info("Endings: {}", endings);
        log.info("P2: {}", scores.stream()
                                 .sorted()
                                 .collect(Collectors.toList())
                                 .get(scores.size() / 2));
    }

    private static int calculateCorruptedScores(List<Character> corrupted) {
        return corrupted.stream()
                        .map(c -> {
                            if (c == ')') {
                                return 3;
                            }
                            if (c == ']') {
                                return 57;
                            }
                            if (c == '}') {
                                return 1197;
                            }
                            if (c == '>') {
                                return 25137;
                            }
                            return 0;
                        })
                        .mapToInt(value -> value)
                        .sum();
    }

    private static List<Long> calculateEndingScores(List<String> endings) {
        return endings.stream()
                      .map(end -> {
                          AtomicLong tempScore = new AtomicLong();
                          end.chars()
                             .forEach(value -> {
                                 tempScore.updateAndGet(v -> v * 5);
                                 if (value == ')') {
                                     tempScore.getAndAdd(1);
                                 } else if (value == ']') {
                                     tempScore.getAndAdd(2);
                                 } else if (value == '}') {
                                     tempScore.getAndAdd(3);
                                 } else if (value == '>') {
                                     tempScore.getAndAdd(4);
                                 }
                             });
                          return tempScore.get();
                      })
                      .collect(Collectors.toList());
    }
}
