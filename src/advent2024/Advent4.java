package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util2D.Point2D;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static util.Util2D.loadCharMatrix;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent4 {
    // https://adventofcode.com/2024/day/4

    public Integer runP1(String file) {
        List<List<Point2D>> deltas = newArrayList();

        Point2D c = new Point2D(0, 0);
        deltas.add(newArrayList(c.UP(), c.UP().UP(), c.UP().UP().UP())); // UP
        deltas.add(newArrayList(c.DOWN(), c.DOWN().DOWN(), c.DOWN().DOWN().DOWN())); // DOWN
        deltas.add(newArrayList(c.LEFT(), c.LEFT().LEFT(), c.LEFT().LEFT().LEFT())); // LEFT
        deltas.add(newArrayList(c.RIGHT(), c.RIGHT().RIGHT(), c.RIGHT().RIGHT().RIGHT())); // RIGHT
        deltas.add(newArrayList(c.UP().LEFT(), c.UP().LEFT().UP().LEFT(), c.UP().LEFT().UP().LEFT().UP().LEFT())); // UP-LEFT
        deltas.add(newArrayList(c.DOWN().RIGHT(), c.DOWN().RIGHT().DOWN().RIGHT(), c.DOWN().RIGHT().DOWN().RIGHT().DOWN().RIGHT())); // DOWN-RIGHT
        deltas.add(newArrayList(c.DOWN().LEFT(), c.DOWN().LEFT().DOWN().LEFT(), c.DOWN().LEFT().DOWN().LEFT().DOWN().LEFT())); // DOWN-LEFT
        deltas.add(newArrayList(c.UP().RIGHT(), c.UP().RIGHT().UP().RIGHT(), c.UP().RIGHT().UP().RIGHT().UP().RIGHT())); // UP-RIGHT

        return run(file, identify(deltas, "XMAS"));
    }

    public Integer runP2(String file) {
        List<List<Point2D>> deltas = newArrayList();

        Point2D c = new Point2D(0, 0);
        deltas.add(newArrayList(c.UP().LEFT(), c.DOWN().RIGHT(), c.DOWN().LEFT(), c.UP().RIGHT())); // UP-LEFT to DOWN-RIGHT and DOWN-LEFT to UP-RIGHT
        deltas.add(newArrayList(c.UP().LEFT(), c.DOWN().RIGHT(), c.UP().RIGHT(), c.DOWN().LEFT())); // UP-LEFT to DOWN-RIGHT and UP-RIGHT to DOWN-LEFT
        deltas.add(newArrayList(c.DOWN().RIGHT(), c.UP().LEFT(), c.DOWN().LEFT(), c.UP().RIGHT())); // DOWN-RIGHT to UP-LEFT and DOWN-LEFT to UP-RIGHT
        deltas.add(newArrayList(c.DOWN().RIGHT(), c.UP().LEFT(), c.UP().RIGHT(), c.DOWN().LEFT())); // DOWN-RIGHT to UP-LEFT and DOWN-LEFT to UP-RIGHT

        // we always check center point first so this is a way oof checking
        // M.S
        // .A.
        // M.S
        // and the 3 other possible variations of the pattern

        return run(file, identify(deltas, "AMSMS"));
    }

    public Integer run(String file, Function<Coord, Integer> identificationAlgorithm) {
        Character[][] chars = loadCharMatrix(file);

        int count = 0;
        for (int row = 0; row < chars.length; row++) {
            for (int col = 0; col < chars[row].length; col++) {
                count += identificationAlgorithm.apply(new Coord(chars, new Point2D(row, col)));
            }
        }

        return count;
    }

    private record Coord(Character[][] matrix, Point2D current) {

    }

    private Function<Coord, Integer> identify(List<List<Point2D>> deltas, String pattern) {
        return (coord) -> {
            AtomicInteger count = new AtomicInteger();
            if (coord.matrix.atPos(coord.current) == pattern.charAt(0)) {
                deltas.forEach(direction -> matchRestOfPattern(pattern, coord, direction, count));
            }
            return count.get();
        };
    }

    private static void matchRestOfPattern(String matcher, Coord coord, List<Point2D> direction, AtomicInteger count) {
        try {
            for (int c = 0; c < direction.size(); c++) {
                if (coord.matrix.atPos(coord.current.withDelta(direction.get(c))) != matcher.charAt(c + 1)) {
                    throw new Exception();
                }
            }
            count.getAndIncrement();
        } catch (Exception _) {

        }
    }
}
