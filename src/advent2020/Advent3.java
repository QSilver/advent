package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent3 {
    private static final List<List<Boolean>> forest = newArrayList();

    private static final List<Integer> steps = newArrayList(1, 3, 5, 7, 1);
    private static final List<Integer> rowSkip = newArrayList(1, 1, 1, 1, 2);

    public static void main(String[] args) {
        InputUtils.fileStream("advent2020/advent3")
            .forEach(s -> {
                ArrayList<Boolean> row = newArrayList();
                Arrays.stream(s.split(""))
                      .forEach(b -> row.add(b.equals("#")));
                forest.add(row);
            });

        long reduce = IntStream.range(0, steps.size())
                               .map(step -> countTrees(steps.get(step), rowSkip.get(step)))
                               .asLongStream()
                               .reduce(1, (left, right) -> left * right);
        log.info("Mult: {}", reduce);
    }

    private static Integer countTrees(int right, int down) {
        AtomicInteger cursor = new AtomicInteger(0);
        AtomicInteger currentRow = new AtomicInteger(0);
        Integer trees = forest.stream()
                              .filter(row -> currentRow.getAndIncrement() % down == 0) // every n-th row
                              .map(row -> row.get(cursor.getAndAdd(right) % row.size()) ? 1 : 0) // is tree
                              .reduce((integer, integer2) -> integer += integer2) // add trees
                              .orElse(0);
        log.info("Right {}; Down {}; Trees {}", right, down, trees);
        return trees;
    }
}
