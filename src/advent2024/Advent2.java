package advent2024;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent2 {
    // https://adventofcode.com/2024/day/2

    public Integer runP1(String file) {
        return fileStream(file)
                .map(Advent2::buildSequence)
                .map(Advent2::isSequenceSafe)
                .mapToInt(value -> value ? 1 : 0)
                .sum();
    }

    public Integer runP2(String file) {
        return fileStream(file)
                .map(Advent2::buildSequence)
                .map(Advent2::buildAllSubsequences)
                .map(Advent2::isAnySubsequenceSafe)
                .mapToInt(value -> value ? 1 : 0)
                .sum();
    }

    private static int[] buildSequence(String line) {
        return Arrays.stream(line.split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private static boolean isSequenceSafe(int[] s) {
        Boolean isIncreasing = null;
        for (int i = 0; i < s.length - 1; i++) {
            if (s[i] > s[i + 1] && s[i] - s[i + 1] <= 3) {
                if (FALSE.equals(isIncreasing)) {
                    return false;
                }
                isIncreasing = TRUE;
            } else if (s[i] < s[i + 1] && s[i + 1] - s[i] <= 3) {
                if (TRUE.equals(isIncreasing)) {
                    return false;
                }
                isIncreasing = FALSE;
            } else {
                return false;
            }
        }
        return true;
    }

    private static boolean isAnySubsequenceSafe(List<int[]> subsequences) {
        return subsequences.stream().map(Advent2::isSequenceSafe).anyMatch(e -> e);
    }

    private static List<int[]> buildAllSubsequences(int[] sequence) {
        List<int[]> subsequences = newArrayList();

        List<Integer> list = Arrays.stream(sequence).boxed().toList();
        for (int i = 0; i < list.size(); i++) {
            subsequences.add(buildSubsequence(list, i));
        }
        return subsequences;
    }

    private static int[] buildSubsequence(List<Integer> s, int i) {
        List<Integer> temp = newArrayList();

        temp.addAll(s.subList(0, i));
        temp.addAll(s.subList(i + 1, s.size()));

        return temp.stream().mapToInt(Integer::intValue).toArray();
    }

}
