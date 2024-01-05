package util;

import lombok.With;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.lang.String.valueOf;
import static util.InputUtils.fileStream;

public class Util2D {
    public static List<Point2D> get2DPoints(String file, char point) {
        List<String> list = fileStream(file).toList();

        List<Point2D> points = newArrayList();
        for (int row = 0; row < list.size(); row++) {
            String s = list.get(row);
            for (int col = 0; col < s.length(); col++) {
                if (s.charAt(col) == point) {
                    points.add(new Point2D(row, col));
                }
            }
        }
        return points;
    }

    public static int[][] loadIntMatrix(String file) {
        List<String> list = fileStream(file).toList();

        int[][] matrix = new int[list.size()][list.getFirst().length()];
        for (int row = 0; row < list.size(); row++) {
            String s = list.get(row);
            for (int col = 0; col < s.length(); col++) {
                matrix[row][col] = parseInt(valueOf(s.charAt(col)));
            }
        }
        return matrix;
    }

    public static char[][] loadCharMatrix(String file) {
        List<String> list = fileStream(file).toList();

        char[][] matrix = new char[list.size()][list.getFirst().length()];
        for (int row = 0; row < list.size(); row++) {
            String s = list.get(row);
            for (int col = 0; col < s.length(); col++) {
                matrix[row][col] = s.charAt(col);
            }
        }
        return matrix;
    }

    public static int[][] initIntMatrix(int rows, int cols, int value) {
        int[][] matrix = new int[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrix[row][col] = value;
            }
        }
        return matrix;
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

    public record Surface(long perimeter, long area, long insidePoints, long shoelaceArea, List<Point> points) {
    }

    long getShortestPath(Util2D.Point from, Function<Point, Boolean> endCondition,
                         Function<Util2D.Point, List<Util2D.Point>> neighbourFunction,
                         Comparator<Point> sorting,
                         Function<Util2D.Point, Integer> distanceFunction) {
        PriorityQueue<Point> toVisit = new PriorityQueue<>(sorting);
        Set<Point> seen = newHashSet();

        toVisit.add(from);
        Util2D.Point end = null;
        while (end == null) {
            if (toVisit.isEmpty()) {
                return -1;
            }

            Util2D.Point current = toVisit.remove();
            end = endCondition.apply(current) ? current : null;

            List<Util2D.Point> neighbours = neighbourFunction.apply(current)
                    .stream().filter(node -> !seen.contains(node))
                    .map(node -> node.withDistance(current.distance + distanceFunction.apply(node)))
                    .toList();

            toVisit.addAll(neighbours);
            seen.addAll(neighbours);
        }

        return end.distance;
    }

    @With
    public record Point(long row, long col, long distance) {
        long manhattanDistanceTo(Point other) {
            return abs(row - other.row) + abs(col - other.col);
        }
    }

    public record Point2D(int row, int col) {

    }
}
