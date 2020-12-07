package advent2020;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent7 {
    static Map<String, List<BagPair>> bags = new HashMap<>();
    static Queue<BagPair> available = new LinkedList<>();

    public static void main(String[] args) {
        Util.fileStream("advent2020/advent7")
            .forEach(line -> {
                String[] split = line.replace("bags", "")
                                     .replace("bag", "")
                                     .split(" contain ");
                String outer = split[0].replace(" ", "");
                List<BagPair> bagPairs = Arrays.stream(split[1].split(", "))
                                               .map(Advent7::mapBag)
                                               .filter(Objects::nonNull)
                                               .collect(Collectors.toList());
                bags.put(outer, bagPairs);
            });

        available.add(new BagPair(1, "shinygold"));
        log.info("Bags {}", countOuterBags());

        available.add(new BagPair(1, "shinygold"));
        log.info("Bags {}", countInnerBags());
    }

    private static int countInnerBags() {
        AtomicInteger count = new AtomicInteger();
        while (!available.isEmpty()) {
            BagPair input = available.poll();
            List<BagPair> output = newArrayList();
            bags.get(input.type)
                .forEach(bagPair -> output.add(new BagPair(input.number * bagPair.number, bagPair.type)));
            output.forEach(bagPair -> count.getAndAdd(bagPair.number));
            available.addAll(output);
        }
        return count.get();
    }

    private static int countOuterBags() {
        Set<String> haveSeen = new HashSet<>();
        while (!available.isEmpty()) {
            String poll = available.poll().type;
            bags.forEach((key, value) -> {
                if (value.stream()
                         .anyMatch(bagPair -> bagPair.type.equals(poll))) {
                    available.add(new BagPair(1, key));
                    haveSeen.add(key);
                }
            });
        }
        return haveSeen.size();
    }

    private static BagPair mapBag(String bag) {
        String[] s = bag.split(" ");
        try {
            return new BagPair(Integer.parseInt(s[0]), s[1] + s[2]);
        } catch (Exception e) {
            return null;
        }
    }
}

@AllArgsConstructor
class BagPair {
    int number;
    String type;
}
