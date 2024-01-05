package util;

import lombok.With;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.abs;
import static java.util.stream.IntStream.range;

@Slf4j
public
class Util {
    public static Set<Integer> rangeToSet(int startInclusive, int endExclusive) {
        return range(startInclusive, endExclusive).boxed().collect(Collectors.toSet());
    }

    public static boolean doubleIsZero(double val, double precision) {
        return val >= -precision && val <= precision;
    }

    public static Surface calculateSurface(List<? extends Point> points) {
        long perimeter = 0L;
        long shoelaceArea = 0L;

        // https://en.m.wikipedia.org/wiki/Shoelace_formula
        for (int i = 0; i < points.size() - 1; i++) {
            Point current = points.get(i);
            Point next = points.get(i + 1);

            perimeter += current.manhattanDistanceTo(next);
            shoelaceArea += ((current.row() * next.col()) - (next.row() * current.col()));
        }

        Point last = points.getLast();
        Point first = points.getFirst();

        perimeter += last.manhattanDistanceTo(first);
        shoelaceArea += ((last.row() * first.col()) - (first.row() * last.col()));
        shoelaceArea = abs(shoelaceArea) / 2L;

        // https://en.m.wikipedia.org/wiki/Pick%27s_theorem
        long insidePoints = shoelaceArea + 1 - perimeter / 2;
        long area = insidePoints + perimeter;

        return new Surface(perimeter, area, insidePoints, shoelaceArea, newArrayList(points));
    }

    long getShortestPath(Point from, Function<Point, Boolean> endCondition,
                         Function<Point, List<Point>> neighbourFunction,
                         Comparator<Point> sorting,
                         Function<Point, Integer> distanceFunction) {
        PriorityQueue<Point> toVisit = new PriorityQueue<>(sorting);
        Set<Point> seen = newHashSet();

        toVisit.add(from);
        Point end = null;
        while (end == null) {
            if (toVisit.isEmpty()) {
                return -1;
            }

            Point current = toVisit.remove();
            end = endCondition.apply(current) ? current : null;

            List<Point> neighbours = neighbourFunction.apply(current)
                    .stream().filter(node -> !seen.contains(node))
                    .map(node -> node.withDistance(current.distance + distanceFunction.apply(node)))
                    .toList();

            toVisit.addAll(neighbours);
            seen.addAll(neighbours);
        }

        return end.distance;
    }

    public record Surface(long perimeter, long area, long insidePoints, long shoelaceArea, List<Point> points) {
    }

    @With
    public record Point(long row, long col, long distance) {
        long manhattanDistanceTo(Point other) {
            return abs(row - other.row) + abs(col - other.col);
        }
    }

    @With
    public record Point3D(long x, long y, long z) {
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
