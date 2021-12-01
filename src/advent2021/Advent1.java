package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Advent1 {
    public static void main(String[] args) {
        List<Integer> depth = Util.fileStream("advent2021/advent1")
                                  .map(Integer::valueOf)
                                  .collect(Collectors.toList());

        solve(depth, 1);
        solve(depth, 3);
    }

    private static void solve(List<Integer> depth, int window) {
        long count = IntStream.range(window, depth.size())
                              .mapToObj(i -> depth.get(i) > depth.get(i - window))
                              .filter(aBoolean -> aBoolean)
                              .count();

        log.info("Count: {}", count);
    }
}
