package advent2021;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.parseInt;

@Slf4j
public class Advent20 {
    static String enhancementAlgo;

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        List<String> inputs = InputUtils.fileStream("advent2021/advent20")
                                  .collect(Collectors.toList());
        enhancementAlgo = inputs.remove(0);
        inputs.remove(0);
        Set<Advent5.Point> points = getPoints(inputs);

        log.info("P1: {}", solve(points, 2));
        log.info("P2: {}", solve(points, 50));
        log.info("{} ms", System.currentTimeMillis() - start);
    }

    private static int solve(Set<Advent5.Point> points, int iterations) {
        int trim = 0;
        int iteration = 0;
        while (iteration < iterations) {
            trim += 1;
            iteration += 1;
            points = enhance(points, trim, iteration);
        }
        return points.size();
    }

    private static Set<Advent5.Point> enhance(Set<Advent5.Point> points, int trim, int iteration) {
        Bound bound = getBound(points);
        Set<Advent5.Point> newSet = newHashSet();
        for (int x = bound.min_x - 4; x <= bound.max_x + 4; x++) {
            for (int y = bound.min_y - 4; y <= bound.max_y + 4; y++) {
                if (enhancementAlgo.charAt(get3x3index(points, x, y)) == '#') {
                    newSet.add(new Advent5.Point(x, y));
                }
            }
        }

        if (iteration % 2 == 0) {
            newSet.removeIf(point -> point.X < -trim || point.Y < -trim || point.X > 100 + trim || point.Y > 100 + trim);
        }

        // display(newSet, trim);
        return newSet;
    }

    private static int get3x3index(Set<Advent5.Point> points, int x, int y) {
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
        return parseInt(index.toString(), 2);
    }

    private static Bound getBound(Set<Advent5.Point> points) {
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
        return new Bound(min_x, max_x, min_y, max_y);
    }

    private static void display(Set<Advent5.Point> toDisplay, Bound bound) {
        for (int x = bound.min_x - 2; x <= bound.max_x + 2; x++) {
            StringBuilder sb = new StringBuilder();
            for (int y = bound.min_y - 2; y <= bound.max_y + 2; y++) {
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

    @AllArgsConstructor
    static class Bound {
        int min_x;
        int max_x;
        int min_y;
        int max_y;
    }
}
