package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util.Point;
import util.Util.Surface;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.parseInt;
import static util.Util.calculateSurface;
import static util.Util.fileStream;

@Slf4j
public class Advent18 {
    // https://adventofcode.com/2023/day/18

    public Long runP1(String file) {

        List<Point> points = newArrayList();
        points.add(new Point(0, 0));

        fileStream(file).forEach(line -> {
            String[] split = line.split(" ");

            Direction direction = Direction.valueOf(split[0]);
            int digSize = parseInt(split[1]);

            buildPointList(points, direction, digSize);
        });

        Surface surface = calculateSurface(points);
        return surface.area();
    }

    public Long runP2(String file) {
        List<String> list = fileStream(file).toList();

        List<Point> points = newArrayList();
        points.add(new Point(0, 0));

        list.forEach(line -> {
            String[] split = line.split(" ");

            String hex = split[2];
            int lastDigit = parseInt(hex.substring(hex.length() - 2, hex.length() - 1));

            Direction direction = Direction.values()[lastDigit];
            int digSize = parseInt(hex.substring(2, hex.length() - 2), 16);

            buildPointList(points, direction, digSize);
        });

        Surface surface = calculateSurface(points);
        return surface.area();
    }

    private static void buildPointList(List<Point> points, Direction direction, int digSize) {
        Point last = points.getLast();
        switch (direction) {
            case U -> points.add(new Point(last.row() - digSize, last.col()));
            case D -> points.add(new Point(last.row() + digSize, last.col()));
            case L -> points.add(new Point(last.row(), last.col() - digSize));
            case R -> points.add(new Point(last.row(), last.col() + digSize));
        }
    }

    enum Direction {
        R, D, L, U
    }
}