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
                                               .map(bag -> {
                                                   String[] s = bag.split(" ");
                                                   try {
                                                       return new BagPair(Integer.parseInt(s[0]), s[1] + s[2]);
                                                   } catch (Exception e) {
                                                       return null;
                                                   }
                                               })
                                               .collect(Collectors.toList());
                bags.put(outer, bagPairs);
            });

        available.add(new BagPair(1, "shinygold"));
        int size = countOuterBags();
        log.info("Bags {}", size);

        available.add(new BagPair(1, "shinygold"));
        AtomicInteger count = new AtomicInteger();
        while (!available.isEmpty()) {
            List<BagPair> innerBags = digForBag(available.poll());
            innerBags.forEach(bagPair -> count.addAndGet(bagPair.number));
            available.addAll(innerBags);
        }
        log.info("Bags {}", count.get());
    }

    private static List<BagPair> digForBag(BagPair input) {
        List<BagPair> output = newArrayList();
        bags.get(input.type)
            .stream()
            .filter(Objects::nonNull)
            .forEach(bagPair -> output.add(new BagPair(input.number * bagPair.number, bagPair.type)));
        return output;
    }

    private static int countOuterBags() {
        Set<String> haveSeen = new HashSet<>();
        while (!available.isEmpty()) {
            String poll = available.poll().type;
            bags.forEach((key, value) -> {
                if (value.stream()
                         .filter(Objects::nonNull)
                         .anyMatch(bagPair -> bagPair.type.equals(poll))) {
                    available.add(new BagPair(1, key));
                    haveSeen.add(key);
                }
            });
        }
        return haveSeen.size();
    }
}

@AllArgsConstructor
class BagPair {
    int number;
    String type;
}
