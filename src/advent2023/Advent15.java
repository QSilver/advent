package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.lang.Integer.parseInt;

@Slf4j
public class Advent15 {
    // https://adventofcode.com/2023/day/15

    public Long runP1(String file) {
        List<String> sequence = Arrays.stream(Util.fileStream(file).toList().getFirst().split(",")).toList();

        return sequence.stream()
                .mapToLong(Advent15::calculateHash)
                .sum();
    }

    public Long runP2(String file) {
        List<String> sequence = Arrays.stream(Util.fileStream(file).toList().getFirst().split(",")).toList();

        Map<Long, LinkedHashMap<String, Integer>> hashmap = newHashMap();

        sequence.forEach(s -> {
            String[] split = s.split("=");

            String label;
            boolean remove = false;
            if (split.length == 2) {
                label = split[0];
            } else {
                label = split[0].split("-")[0];
                remove = true;
            }

            long hash = calculateHash(label);
            LinkedHashMap<String, Integer> strings = hashmap.get(hash);
            if (remove) {
                if (strings != null) {
                    strings.remove(label);
                }
            } else {
                if (strings == null) {
                    hashmap.put(hash, newLinkedHashMap());
                }
                hashmap.get(hash).put(label, parseInt(split[1]));
            }

        });


        return hashmap.entrySet().stream().mapToLong(box -> {
            LinkedHashMap<String, Integer> value = box.getValue();

            long boxSum = 0;
            for (int slot = 0; slot < value.size(); slot++) {
                String key = value.sequencedKeySet().stream().toList().get(slot);
                Integer focal = value.get(key);
                boxSum += focal * (slot + 1) * (box.getKey() + 1);
            }
            return boxSum;
        }).sum();
    }

    private static long calculateHash(String seq) {
        AtomicLong hash = new AtomicLong();
        seq.chars().forEach(value -> {
            long l = hash.get();
            l += value;
            l *= 17;
            l %= 256;
            hash.set(l);
        });
        return hash.get();
    }
}
