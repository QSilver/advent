package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent20 {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        List<String> inputs = Util.fileStream("advent2021/advent20")
                                  .collect(Collectors.toList());

        String enhancementAlgo = inputs.remove(0);
        inputs.remove(0);

        Set<Advent5.Point> points = getPoints(inputs);

        int bound = 1;
        int iter = 1;
        Set<Advent5.Point> enhance = enhance(enhancementAlgo, points, bound, iter);
        while (iter < 50) {
            bound += 1;
            iter += 1;
            enhance = enhance(enhancementAlgo, enhance, bound, iter);
        }

        log.info("P1: {}", enhance.size());
        log.info("{}ms", System.currentTimeMillis() - start);
    }

    private static Set<Advent5.Point> enhance(String enhancementAlgo, Set<Advent5.Point> points, int bound, int iter) {
        Set<Advent5.Point> newSet = newHashSet();
        int min_x = points.stream()
                          .map(point -> point.X)
                          .mapToInt(value -> value)
                          .min()
                          .orElse(-1);
        int max_x = points.stream()
                          .map(point -> point.X)
                          .mapToInt(value -> value)
                          .max()
                          .orElse(-1);
        int min_y = points.stream()
                          .map(point -> point.Y)
                          .mapToInt(value -> value)
                          .min()
                          .orElse(-1);
        int max_y = points.stream()
                          .map(point -> point.Y)
                          .mapToInt(value -> value)
                          .max()
                          .orElse(-1);

        for (int x = min_x - 4; x <= max_x + 4; x++) {
            for (int y = min_y - 4; y <= max_y + 4; y++) {
                StringBuilder index = new StringBuilder();
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (points.contains(new Advent5.Point(x + i, y + j))) {
                            index.append("1");
                        } else {
                            index.append("0");
                        }
                    }
                }
                int i = Integer.parseInt(index.toString(), 2);
                if (enhancementAlgo.charAt(i) == '#') {
                    newSet.add(new Advent5.Point(x, y));
                }
            }
        }

        // trim
        if (iter % 2 == 0) {
            newSet.removeIf(point -> point.X < -bound || point.Y < -bound || point.X > 100 + bound || point.Y > 100 + bound);
        }

        // display(newSet, min_x, max_x, min_y, max_y);
        return newSet;
    }

    private static void display(Set<Advent5.Point> toDisplay, int min_x, int max_x, int min_y, int max_y) {
        for (int x = min_x - 2; x <= max_x + 2; x++) {
            StringBuilder sb = new StringBuilder();
            for (int y = min_y - 2; y <= max_y + 2; y++) {
                sb.append(toDisplay.contains(new Advent5.Point(x, y)) ? "██" : "  ");
            }
            log.info("{}", sb);
        }
    }

    private static Set<Advent5.Point> getPoints(List<String> inputs) {
        Set<Advent5.Point> points = newHashSet();

        for (int y = 0; y < inputs.size(); y++) {
            for (int x = 0; x < inputs.get(y)
                                      .length(); x++) {
                if (inputs.get(y)
                          .charAt(x) == '#') {
                    points.add(new Advent5.Point(y, x));
                }
            }
        }
        return points;
    }
}
