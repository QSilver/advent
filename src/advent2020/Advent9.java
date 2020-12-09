package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class Advent9 {

    private static final int CONST = 25;
    static List<Long> active = new LinkedList<>();
    static Set<Long> sums = new HashSet<>();

    static long[][] arraySums = new long[1001][1001];

    public static void main(String[] args) {
        List<Long> collect = Util.fileStream("advent2020/advent9")
                                 .map(Long::parseLong)
                                 .collect(Collectors.toList());

        for (int i = 0; i < CONST; i++) {
            active.add(collect.get(i));
        }

        calcSums();

        long breakNumber = 0L;
        for (int i = CONST; i < collect.size(); i++) {
            Long num = collect.get(i);
            if (!sums.contains(num)) {
                log.info("Break {}", num);
                breakNumber = num;
                break;
            }

            active.set(i % CONST, num);
            calcSums();
        }

        List<Long> subList = getSubList(collect, breakNumber);
        long max = subList.stream()
                          .mapToLong(Long::longValue)
                          .max()
                          .orElse(-1L);
        long min = subList.stream()
                          .mapToLong(Long::longValue)
                          .min()
                          .orElse(-1L);
        log.info("min+max {}", max + min);
    }

    private static List<Long> getSubList(List<Long> collect, long breakNumber) {
        for (int i = 0; i < collect.size() - 1; i++) {
            for (int j = i + 1; j < collect.size(); j++) {
                long l = collect.get(j - 1) + arraySums[i][j - 1];
                if (l == breakNumber) {
                    log.info("Found it {} {}", i, j - 1);
                    return collect.subList(i, j);
                }
                arraySums[i][j] = l;
            }
        }
        return new ArrayList<>();
    }

    private static void calcSums() {
        sums.clear();
        for (int i = 0; i < CONST - 1; i++) {
            for (int j = i; j < CONST; j++) {
                sums.add(active.get(i) + active.get(j));
            }
        }
    }
}
