package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.abs;

@Slf4j
public class Advent18 {
    // https://adventofcode.com/2023/day/18

    public Long runP1(String file) {
        List<String> list = Util.fileStream(file).toList();

        List<Point> points = newArrayList();
        points.add(new Point(0, 0));

        AtomicInteger perimeter = new AtomicInteger();
        list.forEach(line -> {
            String[] split = line.split(" ");

            int digSize = Integer.parseInt(split[1]);

            Point last = points.getLast();
            switch (Direction.valueOf(split[0])) {
                case U -> points.add(new Point(last.row - digSize, last.col));
                case D -> points.add(new Point(last.row + digSize, last.col));
                case L -> points.add(new Point(last.row, last.col - digSize));
                case R -> points.add(new Point(last.row, last.col + digSize));
            }
            perimeter.addAndGet(digSize);
        });

        long area = 0L;
        for (int i = 0; i < points.size() - 1; i++) {
            area += (((long) points.get(i).row * points.get(i + 1).col) - ((long) points.get(i + 1).row * points.get(i).col));
        }
        area += (((long) points.getLast().row * points.getFirst().col) - ((long) points.getFirst().row * points.getLast().col));
        area = abs(area) / 2L;

        long inside = area + 1 - perimeter.get() / 2;
        return inside + perimeter.get();
    }

    public Long runP2(String file) {
        List<String> list = Util.fileStream(file).toList();

        List<Point> points = newArrayList();
        points.add(new Point(0, 0));

        AtomicLong perimeter = new AtomicLong();
        list.forEach(line -> {
            String[] split = line.split(" ");

            String hex = split[2];
            int digSize = Integer.parseInt(hex.substring(2, hex.length() - 2), 16);
            int lastDigit = Integer.parseInt(hex.substring(hex.length() - 2, hex.length() - 1));

            Direction value = Direction.values()[lastDigit];

            Point last = points.getLast();
            switch (value) {
                case U -> points.add(new Point(last.row - digSize, last.col));
                case D -> points.add(new Point(last.row + digSize, last.col));
                case L -> points.add(new Point(last.row, last.col - digSize));
                case R -> points.add(new Point(last.row, last.col + digSize));
            }
            perimeter.addAndGet(digSize);
        });

        long area = 0L;
        for (int i = 0; i < points.size() - 1; i++) {
            area += (((long) points.get(i).row * points.get(i + 1).col) - ((long) points.get(i + 1).row * points.get(i).col));
        }
        area += (((long) points.getLast().row * points.getFirst().col) - ((long) points.getFirst().row * points.getLast().col));
        area = abs(area) / 2L;

        long inside = area + 1 - perimeter.get() / 2;
        return inside + perimeter.get();
    }

    record Point(int row, int col) {

    }

    enum Direction {
        R, D, L, U
    }
}