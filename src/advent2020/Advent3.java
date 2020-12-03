package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent3 {
    private static final List<List<Boolean>> map = newArrayList();

    private static final List<Integer> steps = newArrayList(1, 3, 5, 7, 1);
    private static final List<Integer> rowSkip = newArrayList(1, 1, 1, 1, 2);

    public static void main(String[] args) {
        Util.fileStream("advent2020/advent3")
            .forEach(s -> {
                ArrayList<Boolean> row = newArrayList();
                Arrays.stream(s.split(""))
                      .forEach(b -> row.add(b.equals("#")));
                map.add(row);
            });

        int width = map.get(0)
                       .size();

//        long mult = 1;
//        for (int pass = 0; pass < 5; pass++) {
//            int cursor = 0;
//            int counter = 0;
//            for (int row = 0; row < map.size(); row += rowSkip.get(pass)) {
//                counter += map.get(row)
//                              .get(cursor % width) ? 1 : 0;
//                cursor += steps.get(pass);
//            }
//            log.info("Trees: {}", counter);
//            mult *= counter;
//        }
//        log.info("Final: {}", mult);

        long reduce = IntStream.range(0, 5)
                               .map(value -> {
                                   AtomicInteger cursor = new AtomicInteger(0);
                                   AtomicInteger currentRow = new AtomicInteger(0);
                                   Integer trees = map.stream()
                                                      .filter(row -> currentRow.getAndIncrement() % rowSkip.get(value) == 0)
                                                      .map(row -> row.get(cursor.getAndAdd(steps.get(value)) % width) ? 1 : 0)
                                                      .reduce((integer, integer2) -> integer += integer2)
                                                      .get();
                                   log.info("Trees: {}", trees);
                                   return trees;
                               })
                               .asLongStream()
                               .reduce(1, (left, right) -> left * right);
        log.info("Mult: {}", reduce);
    }
}
