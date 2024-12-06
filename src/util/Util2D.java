package util;

import lombok.With;
import util.Util.Direction;

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

    public static Character[][] loadCharMatrix(String file) {
        List<String> list = fileStream(file).toList();

        Character[][] matrix = new Character[list.size()][list.getFirst().length()];
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

    public static Surface calculateSurface(List<Point2D> points) {
        long perimeter = 0L;
        long shoelaceArea = 0L;

        // https://en.m.wikipedia.org/wiki/Shoelace_formula
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D current = points.get(i);
            Point2D next = points.get(i + 1);

            perimeter += current.manhattanDistanceTo(next);
            shoelaceArea += ((current.row() * next.col()) - (next.row() * current.col()));
        }

        Point2D last = points.getLast();
        Point2D first = points.getFirst();

        perimeter += last.manhattanDistanceTo(first);
        shoelaceArea += ((last.row() * first.col()) - (first.row() * last.col()));
        shoelaceArea = abs(shoelaceArea) / 2L;

        // https://en.m.wikipedia.org/wiki/Pick%27s_theorem
        long insidePoints = shoelaceArea + 1 - perimeter / 2;
        long area = insidePoints + perimeter;

        return new Surface(perimeter, area, insidePoints, shoelaceArea, newArrayList(points));
    }

    public record Surface(long perimeter, long area, long insidePoints, long shoelaceArea, List<Point2D> points) {
    }

    long getShortestPath(PointWithDistance from, Function<PointWithDistance, Boolean> endCondition,
                         Function<PointWithDistance, List<PointWithDistance>> neighbourFunction,
                         Comparator<PointWithDistance> sorting,
                         Function<PointWithDistance, Integer> distanceFunction) {
        PriorityQueue<PointWithDistance> toVisit = new PriorityQueue<>(sorting);
        Set<PointWithDistance> seen = newHashSet();

        toVisit.add(from);
        PointWithDistance end = null;
        while (end == null) {
            if (toVisit.isEmpty()) {
                return -1;
            }

            PointWithDistance current = toVisit.remove();
            end = endCondition.apply(current) ? current : null;

            List<PointWithDistance> neighbours = neighbourFunction.apply(current)
                    .stream().filter(node -> !seen.contains(node))
                    .map(node -> node.withDistance(current.distance + distanceFunction.apply(node)))
                    .toList();

            toVisit.addAll(neighbours);
            seen.addAll(neighbours);
        }

        return end.distance;
    }

    @With
    public record PointWithDistance(Point2D point2D, long distance) {
    }

    public record Point2D(long row, long col) {
        public Point2D neighbour(Direction direction) {
            switch (direction) {
                case UP -> {
                    return UP();
                }
                case RIGHT -> {
                    return RIGHT();
                }
                case DOWN -> {
                    return DOWN();
                }
                case LEFT -> {
                    return LEFT();
                }
            }
            throw new RuntimeException("Impossible Direction");
        }

        public List<Point2D> neighbours4() {
            return newArrayList(UP(), RIGHT(), DOWN(), LEFT());
        }

        public Point2D UP() {
            return new Point2D(row - 1, col);
        }

        public Point2D DOWN() {
            return new Point2D(row + 1, col);
        }

        public Point2D LEFT() {
            return new Point2D(row, col - 1);
        }

        public Point2D RIGHT() {
            return new Point2D(row, col + 1);
        }

        long manhattanDistanceTo(Point2D other) {
            return abs(row - other.row) + abs(col - other.col);
        }

        public Point2D withDelta(Point2D delta) {
            return new Point2D(row + delta.row, col + delta.col);
        }

        public Point2D copy() {
            return new Point2D(row, col);
        }
    }
}
