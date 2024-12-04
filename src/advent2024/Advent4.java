package advent2024;

import lombok.extern.slf4j.Slf4j;
import util.Util2D.Point2D;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static util.Util2D.loadCharMatrix;

@Slf4j
public class Advent4 {
    // https://adventofcode.com/2024/day/4

    public Integer runP1(String file) {
        List<List<Point2D>> deltas = newArrayList();

        deltas.add(newArrayList(new Point2D(-1, +0), new Point2D(-2, +0), new Point2D(-3, +0))); // UP
        deltas.add(newArrayList(new Point2D(+1, +0), new Point2D(+2, +0), new Point2D(+3, +0))); // DOWN
        deltas.add(newArrayList(new Point2D(+0, -1), new Point2D(+0, -2), new Point2D(+0, -3))); // LEFT
        deltas.add(newArrayList(new Point2D(+0, +1), new Point2D(+0, +2), new Point2D(+0, +3))); // RIGHT
        deltas.add(newArrayList(new Point2D(-1, -1), new Point2D(-2, -2), new Point2D(-3, -3))); // UP-LEFT
        deltas.add(newArrayList(new Point2D(+1, +1), new Point2D(+2, +2), new Point2D(+3, +3))); // DOWN-RIGHT
        deltas.add(newArrayList(new Point2D(+1, -1), new Point2D(+2, -2), new Point2D(+3, -3))); // DOWN-LEFT
        deltas.add(newArrayList(new Point2D(-1, +1), new Point2D(-2, +2), new Point2D(-3, +3))); // UP-RIGHT

        return run(file, identify(deltas, "XMAS"));
    }

    public Integer runP2(String file) {
        List<List<Point2D>> deltas = newArrayList();

        deltas.add(newArrayList(new Point2D(-1, -1), new Point2D(+1, +1), new Point2D(-1, +1), new Point2D(+1, -1))); // UP-LEFT to DOWN-RIGHT and DOWN-LEFT to UP-RIGHT
        deltas.add(newArrayList(new Point2D(-1, -1), new Point2D(+1, +1), new Point2D(+1, -1), new Point2D(-1, +1))); // UP-LEFT to DOWN-RIGHT and UP-RIGHT to DOWN-LEFT
        deltas.add(newArrayList(new Point2D(+1, +1), new Point2D(-1, -1), new Point2D(-1, +1), new Point2D(+1, -1))); // DOWN-RIGHT to UP-LEFT and DOWN-LEFT to UP-RIGHT
        deltas.add(newArrayList(new Point2D(+1, +1), new Point2D(-1, -1), new Point2D(+1, -1), new Point2D(-1, +1))); // DOWN-RIGHT to UP-LEFT and DOWN-LEFT to UP-RIGHT

        // we always check center point first so this is a way oof checking
        // M.S
        // .A.
        // M.S
        // and the 3 other possible variations of the pattern

        return run(file, identify(deltas, "AMSMS"));
    }

    public Integer run(String file, Function<Coord, Integer> identificationAlgorithm) {
        char[][] chars = loadCharMatrix(file);

        int count = 0;
        for (int row = 0; row < chars.length; row++) {
            for (int col = 0; col < chars[row].length; col++) {
                count += identificationAlgorithm.apply(new Coord(chars, new Point2D(row, col)));
            }
        }

        return count;
    }

    private record Coord(char[][] matrix, Point2D current) {

    }

    private Function<Coord, Integer> identify(List<List<Point2D>> deltas, String pattern) {
        return (coord) -> {
            AtomicInteger count = new AtomicInteger();
            if (coord.matrix[(int) coord.current.row()][(int) coord.current.col()] == pattern.charAt(0)) {
                deltas.forEach(direction -> matchRestOfPattern(pattern, coord, direction, count));
            }
            return count.get();
        };
    }

    private static void matchRestOfPattern(String matcher, Coord coord, List<Point2D> direction, AtomicInteger count) {
        try {
            for (int c = 0; c < direction.size(); c++) {
                if (coord.matrix[(int) coord.current.row() + (int) direction.get(c).row()][(int) coord.current.col() + (int) direction.get(c).col()] != matcher.charAt(c + 1)) {
                    throw new Exception();
                }
            }
            count.getAndIncrement();
        } catch (Exception _) {

        }
    }
}
