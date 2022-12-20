package advent2022;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Math.floorMod;


@Slf4j
public class Advent20 {
    static BiMap<Integer, Integer> positions = HashBiMap.create();

    public static void main(String[] args) {
        List<Integer> list = Util.fileStream("advent2022/advent20")
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        AtomicInteger index = new AtomicInteger();
        list.forEach(value -> positions.put(index.getAndIncrement(), value));

        List<Integer> display = positions.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        log.info("{}", display);

        for (Integer number : list) {
            int oldPos = positions.inverse().get(number);
            int newPos = floorMod(oldPos + number, list.size() - 1);

            if (number >= 0) {
                if (newPos >= oldPos) {
                    decrementAll(oldPos, newPos);
                } else {
                    incrementAll(oldPos, newPos);
                }
                positions.forcePut(newPos, number);
            } else {
                if (newPos <= oldPos) {
                    incrementAll(oldPos, newPos);
                } else {
                    decrementAll(oldPos, newPos);
                }
                positions.forcePut(newPos, number);
            }

            display = positions.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
            log.info("{} jumped {}", number, display);
        }

        int starting = positions.inverse().get(0);
        Integer v1000 = positions.get((starting + 1000) % list.size());
        Integer v2000 = positions.get((starting + 2000) % list.size());
        Integer v3000 = positions.get((starting + 3000) % list.size());
        log.info("Pos 1000: {}", v1000);
        log.info("Pos 2000: {}", v2000);
        log.info("Pos 3000: {}", v3000);
        log.info("Sum: {}", v1000 + v2000 + v3000);
    }

    private static void decrementAll(int oldPos, int newPos) {
        for (int jump = oldPos + 1; jump <= newPos; jump++) {
            positions.forcePut(jump - 1, positions.get(jump));
        }
    }

    private static void incrementAll(int oldPos, int newPos) {
        for (int jump = oldPos - 1; jump >= newPos; jump--) {
            positions.forcePut(jump + 1, positions.get(jump));
        }
    }
}