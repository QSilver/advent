package advent2024;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.List;
import java.util.stream.LongStream;

import static com.google.common.collect.Lists.newArrayList;
import static util.InputUtils.readDoubleNewlineBlocks;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent19 {
    // https://adventofcode.com/2024/day/19

    List<String> towels = newArrayList();
    LoadingCache<String, Long> combinationCache = CacheBuilder.newBuilder().build(CacheLoader.from(this::calculateDesign));

    public Long runP1(String file) {
        return getTowelCombinations(file).count();
    }

    public Long runP2(String file) {
        return getTowelCombinations(file).sum();
    }

    private LongStream getTowelCombinations(String file) {
        String[] strings = readDoubleNewlineBlocks(file);
        towels = strings[0].split(", ").stream().toList();

        return strings[1].split("\n").stream()
                .mapToLong(this::calculateDesign)
                .filter(combinations -> combinations > 0);
    }

    private Long calculateDesign(String design) {
        if (design.isEmpty()) {
            return 1L;
        }
        return towels.stream()
                .mapToLong(towel -> {
                    if (design.startsWith(towel)) {
                        return combinationCache.getUnchecked(design.substring(towel.length()));
                    }
                    return 0;
                })
                .sum();
    }

}
