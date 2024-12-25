package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.stream;
import static util.InputUtils.readDoubleNewlineBlocks;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent25 {
    // https://adventofcode.com/2024/day/25

    Map<Character[][], int[]> locks = newHashMap();
    Map<Character[][], int[]> keys = newHashMap();

    public Long runP1(String file) {
        stream(readDoubleNewlineBlocks(file)).forEach(this::parseBlock);
        return countMatching();
    }

    private long countMatching() {
        AtomicLong count = new AtomicLong();
        locks.keySet().forEach(lock ->
                keys.keySet().forEach(key -> {
                    if (fit(locks.get(lock), keys.get(key))) {
                        count.getAndIncrement();
                    }
                }));
        return count.get();
    }

    private void parseBlock(String block) {
        List<String> list = stream(block.split("\n")).toList();

        Character[][] matrix = new Character[list.size()][list.getFirst().length()];
        for (int row = 0; row < list.size(); row++) {
            String s = list.get(row);
            for (int col = 0; col < s.length(); col++) {
                matrix[row][col] = s.charAt(col);
            }
        }

        int[] count = count(matrix);
        if (stream(matrix[0]).anyMatch(character -> character == '#')) {
            locks.put(matrix, count);
        } else {
            keys.put(matrix, count);
        }
    }

    private static boolean fit(int[] lock, int[] key) {
        for (int i = 0; i < lock.length; i++) {
            if (lock[i] + key[i] > 7) {
                return false;
            }
        }
        return true;
    }

    private static int[] count(Character[][] matrix) {
        int[] counts = new int[matrix[0].length];
        for (int col = 0; col < matrix[0].length; col++) {
            for (Character[] characters : matrix) {
                if (characters[col] == '#') {
                    counts[col]++;
                }
            }
        }
        return counts;
    }
}
