package util;

import lombok.NonNull;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Long.parseLong;
import static java.lang.Math.abs;

public record Point2D(long row, long col) {
    public static Point2D fromCoords(String coords) {
        String[] split = coords.split(",");
        return new Point2D(parseLong(split[0]), parseLong(split[1]));
    }

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

    public List<Point2D> getNAround(int n) {
        List<Point2D> around = newArrayList();
        for (long deltaRow = -n; deltaRow <= n; deltaRow++) {
            for (long deltaCol = -n; deltaCol <= n; deltaCol++) {
                around.add(this.withDelta(new Point2D(deltaRow, deltaCol)));
            }
        }
        return around;
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

    public long manhattanDistanceTo(Point2D other) {
        return abs(row - other.row) + abs(col - other.col);
    }

    public Point2D withDelta(Point2D delta) {
        return new Point2D(row + delta.row, col + delta.col);
    }

    public Point2D copy() {
        return new Point2D(row, col);
    }

    @Override
    public @NonNull String toString() {
        return String.format("%s,%s", row, col);
    }
}