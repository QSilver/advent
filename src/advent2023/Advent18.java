package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util2D.Point;

import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.parseInt;
import static util.InputUtils.fileStream;
import static util.Util2D.calculateSurface;

@Slf4j
public class Advent18 {
    // https://adventofcode.com/2023/day/18

    public Long runP1(String file) {
        return extractPointsAndCalculateArea(file, Advent18::p1Input);
    }

    public Long runP2(String file) {
        return extractPointsAndCalculateArea(file, Advent18::p2Input);
    }

    private long extractPointsAndCalculateArea(String file, Function<String[], Result> coordinateExtractor) {
        List<Point> points = newArrayList(new Point(0, 0, 0));

        fileStream(file).forEach(line -> {
            Result result = coordinateExtractor.apply(line.split(" "));
            Point nextPoint = getNextPoint(points.getLast(), result.direction(), result.digSize);
            points.add(nextPoint);
        });

        return calculateSurface(points).area();
    }

    private static Result p1Input(String[] split) {
        Direction direction = Direction.valueOf(split[0]);
        int digSize = parseInt(split[1]);
        return new Result(direction, digSize);
    }

    private static Result p2Input(String[] split) {
        String hex = split[2];
        int lastDigit = parseInt(hex.substring(hex.length() - 2, hex.length() - 1));

        Direction direction = Direction.values()[lastDigit];
        int digSize = parseInt(hex.substring(2, hex.length() - 2), 16);

        return new Result(direction, digSize);
    }

    private static Point getNextPoint(Point last, Direction direction, int digSize) {
        switch (direction) {
            case U -> {
                return new Point(last.row() - digSize, last.col(), 1);
            }
            case D -> {
                return new Point(last.row() + digSize, last.col(), 1);
            }
            case L -> {
                return new Point(last.row(), last.col() - digSize, 1);
            }
            case R -> {
                return new Point(last.row(), last.col() + digSize, 1);
            }
        }
        return null;
    }

    private record Result(Direction direction, int digSize) {
    }

    enum Direction {
        R, D, L, U
    }
}