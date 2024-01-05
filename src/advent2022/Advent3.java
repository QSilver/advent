package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.stream.Collectors;

// 12701 - too high
@Slf4j
public class Advent3 {
    public static void main(String[] args) {
        List<String> input = InputUtils.fileStream("advent2022/advent3").collect(Collectors.toList());

        int sum = part1(input);
        log.info("Item Sum: {}", sum);

        int sum2 = part2(input);
        log.info("Badge Sum: {}", sum2);
    }

    private static int part2(List<String> input) {
        int score = 0;
        for (int g = 0; g < input.size(); g += 3) {
            int[] firstFreq = countFrequencies(input.get(g));
            int[] secondFreq = countFrequencies(input.get(g + 1));
            int[] thirdFreq = countFrequencies(input.get(g + 2));


            for (int i = 0; i < firstFreq.length; i++) {
                if (firstFreq[i] > 0 && secondFreq[i] > 0 && thirdFreq[i] > 0) {
                    score += getPriority(i);
                }
            }
        }
        return score;
    }

    private static int part1(List<String> input) {
        return input.stream().mapToInt(s -> {
            String first = s.substring(0, s.length() / 2);
            String second = s.substring(s.length() / 2);

            int[] firstFreq = countFrequencies(first);
            int[] secondFreq = countFrequencies(second);

            int score = 0;
            for (int i = 0; i < firstFreq.length; i++) {
                if (firstFreq[i] > 0 && secondFreq[i] > 0) {
                    score += getPriority(i);
                }
            }

            return score;
        }).sum();
    }

    private static int[] countFrequencies(String input) {
        int[] firstFreq = new int[58];
        for (int i = 0; i < input.length(); i++) {
            firstFreq[input.charAt(i) - 65]++;
        }
        return firstFreq;
    }

    private static int getPriority(int i) {
        if (i <= 26) {
            return 27 + i;
        } else {
            return i - 31;
        }
    }
}
