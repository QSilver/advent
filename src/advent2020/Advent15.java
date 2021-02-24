package advent2020;

import com.google.common.collect.HashBiMap;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class Advent15 {
    private static final int END = 30000000;
    static HashBiMap<Integer, Integer> numbers = HashBiMap.create();

    public static void main(String[] args) {
        List<Integer> input = Arrays.stream(Util.fileStream("advent2020/advent15")
                                                .map(s -> s.split(","))
                                                .findFirst()
                                                .get())
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());

        AtomicInteger counter = new AtomicInteger(1);
        input.forEach(integer -> numbers.put(integer, counter.getAndIncrement()));

        int prev = 0;

        for (int i = input.size() + 1; i < END + 1; i++) {
            int newVal = numbers.get(prev) == null ? 0 : i - numbers.get(prev);
            numbers.put(prev, i);
            prev = newVal;
            if (i % (END / 100) == 0) {
                log.info("Progress: {}%", i / (END * 0.01));
            }
        }

        log.info("Number in pos {}: {}", END, numbers.inverse()
                                                     .get(END));
    }
}
