package advent2023;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent12 {
    // https://adventofcode.com/2023/day/12
    List<Pair<String, int[]>> springs = newArrayList();
    Map<Key, Long> cache = newHashMap();

    public Long runP1(String file) {
        List<String> collect = Util.fileStream(file).collect(Collectors.toList());

        collect.forEach(s -> {
            String[] split = s.split(" ");
            int[] list = Arrays.stream(split[1].split(",")).mapToInt(Integer::parseInt).toArray();
            springs.add(Pair.create(split[0] + ".", list));
        });

        return runSolution();
    }

    public Long runP2(String file) {
        List<String> collect = Util.fileStream(file).collect(Collectors.toList());

        collect.forEach(s -> {
            String[] split = s.split(" ");

            String lineRepeat = (split[0] + "?").repeat(5);
            String line = lineRepeat.substring(0, lineRepeat.length() - 1) + ".";

            String blocksRepeat = (split[1] + ",").repeat(5);
            String substring = blocksRepeat.substring(0, blocksRepeat.length() - 1);
            int[] list = Arrays.stream(substring.split(",")).mapToInt(Integer::parseInt).toArray();

            springs.add(Pair.create(line, list));
        });

        return runSolution();
    }

    private long runSolution() {
        return springs.stream()
                .mapToLong(pair -> count(pair.getFirst(), pair.getSecond(), 0, 0, 0))
                .sum();
    }

    long count(String line, int[] blocks, int lineIndex, int indexInCurrentBlock, int blockIndex) {
        if (lineIndex == line.length()) {
            // end of line, return 1 if we matched all damaged blocks, 0 otherwise
            return blockIndex == blocks.length ? 1 : 0;
        } else if (line.charAt(lineIndex) == '#') {
            // still inside a damaged block
            return count(line, blocks, lineIndex + 1, indexInCurrentBlock + 1, blockIndex);
        } else if (line.charAt(lineIndex) == '.' || blockIndex == blocks.length) {
            // end of damaged block or no more damaged blocks to match
            if (blockIndex < blocks.length && indexInCurrentBlock == blocks[blockIndex]) {
                // reached end of current damaged block, more blocks to match, start new block
                return count(line, blocks, lineIndex + 1, 0, blockIndex + 1);
            } else if (indexInCurrentBlock == 0) {
                // no new block to count, move on
                return count(line, blocks, lineIndex + 1, 0, blockIndex);
            } else {
                return 0;
            }
        } else {
            // ? = #
            Key key = new Key(line, blocks, lineIndex + 1, indexInCurrentBlock + 1, blockIndex);
            Long hash_count = cache.get(key);
            if (hash_count == null) {
                hash_count = count(line, blocks, lineIndex + 1, indexInCurrentBlock + 1, blockIndex);
                cache.put(key, hash_count);
            }

            // ? = .
            long dot_count = 0L;
            if (indexInCurrentBlock == blocks[blockIndex]) {
                // end of current block, move to next block
                dot_count = count(line, blocks, lineIndex + 1, 0, blockIndex + 1);
            } else if (indexInCurrentBlock == 0) {
                dot_count = count(line, blocks, lineIndex + 1, 0, blockIndex);
            }
            return hash_count + dot_count;
        }
    }

    record Key(String line, int[] counts, int pos, int current_count, int countPos) {
    }
}
