package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Math.max;

@Slf4j
public class Advent24 {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        List<String> input = Util.fileStream("advent2021/advent24")
                                 .toList();

        Map<Integer, Integer> pairs = newHashMap();
        Map<Integer, Integer> inverse = newHashMap();
        List<Integer> bounded = newArrayList();
        Stack<Integer> decision = new Stack<>();
        int[] popIndex = new int[14];
        int[] pushIndex = new int[14];

        for (int line = 0; line < input.size(); line++) {
            final String s = input.get(line);
            switch (line % 18) {
                case 4 -> {
                    if (!s.contains("26")) {
                        decision.push(line / 18);
                    } else {
                        Integer pop = decision.pop();
                        pairs.put(pop, line / 18);
                        inverse.put(line / 18, pop);
                        bounded.add(pop);
                    }
                }
                case 5 -> popIndex[line / 18] = Integer.parseInt(s.split(" ")[2]);
                case 15 -> pushIndex[line / 18] = Integer.parseInt(s.split(" ")[2]);
            }
        }

        int[] var = new int[14];
        int[] upper = new int[14];
        int[] lower = new int[14];

        AtomicInteger index = new AtomicInteger();
        bounded = bounded.stream()
                         .sorted()
                         .map(integer -> {
                             upper[index.get()] = 9 - max(0, pushIndex[integer] + popIndex[pairs.get(integer)]);
                             lower[index.get()] = max(0, -(pushIndex[integer] + popIndex[pairs.get(integer)])) + 1;
                             index.getAndIncrement();
                             return integer;
                         })
                         .collect(Collectors.toList());

        var[bounded.get(0)] = upper[0];
        var[bounded.get(1)] = upper[1];
        var[bounded.get(2)] = upper[2];
        var[bounded.get(3)] = upper[3];
        var[bounded.get(4)] = upper[4];
        var[bounded.get(5)] = upper[5];
        var[bounded.get(6)] = upper[6];
        inverse.forEach((key, value) -> var[key] = var[value] + pushIndex[value] + popIndex[pairs.get(value)]);
        StringBuilder maxVersion = new StringBuilder();
        for (int digit = 0; digit < 14; digit++) {
            maxVersion.append(var[digit]);
        }
        long max = Long.parseLong(maxVersion.toString());
        log.info("Max Version {}", max);

        var[bounded.get(0)] = lower[0];
        var[bounded.get(1)] = lower[1];
        var[bounded.get(2)] = lower[2];
        var[bounded.get(3)] = lower[3];
        var[bounded.get(4)] = lower[4];
        var[bounded.get(5)] = lower[5];
        var[bounded.get(6)] = lower[6];
        inverse.forEach((key, value) -> var[key] = var[value] + pushIndex[value] + popIndex[pairs.get(value)]);
        StringBuilder version = new StringBuilder();
        for (int digit = 0; digit < 14; digit++) {
            version.append(var[digit]);
        }
        long min = Long.parseLong(version.toString());
        log.info("Min Version {}", min);
        log.info("{} ms", System.currentTimeMillis() - start);


    }
}