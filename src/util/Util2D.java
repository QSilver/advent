package util;

import lombok.With;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Math.abs;

public class Util2D {
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


    public static List<Node> getAllPaths(Node from, Function<Node, Boolean> endCondition, Function<Node, List<Node>> neighbourFunction, Comparator<Node> sorting) {
        List<Node> paths = newArrayList();

        PriorityQueue<Node> toVisit = new PriorityQueue<>(sorting);
        Map<Node, Long> seen = newHashMap();

        toVisit.add(from);
        while (!toVisit.isEmpty()) {
            Node current = toVisit.remove();

            if (endCondition.apply(current)) {
                paths.add(current);
            }

            List<Node> apply = neighbourFunction.apply(current);
            List<Node> neighbours = apply
                    .stream().filter(node -> seen.getOrDefault(node, Long.MAX_VALUE) > current.distance)
                    .toList();

            toVisit.addAll(neighbours);
            neighbours.forEach(node -> seen.put(node, node.distance));
        }

        return paths;
    }

    @With
    public record Node(Point2D point, Direction direction, long distance, Node previous) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (point.row() != node.point.row()) return false;
            if (point.col() != node.point.col()) return false;
            return direction == node.direction;
        }

        @Override
        public int hashCode() {
            int result = (int) point.row();
            result = (int) (151 * result + point.col());
            result = 151 * result + direction.hashCode();
            return result;
        }
    }

    @With
    public record PointWithLabel(Point2D point2D, char label) {
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

        public List<Point2D> neighbours8() {
            return newArrayList(UP(), UP().RIGHT(), RIGHT(), RIGHT().DOWN(), DOWN(), DOWN().LEFT(), LEFT(), LEFT().UP());
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

    public enum Direction {
        UP, RIGHT, DOWN, LEFT;

        public Direction clockwise() {
            Direction[] values = Direction.values();
            int next = (this.ordinal() + 1) % values.length;
            return values[next];
        }

        public Direction counterclockwise() {
            Direction[] values = Direction.values();
            int next = (this.ordinal() - 1) % values.length;
            return values[next];
        }
    }
}
