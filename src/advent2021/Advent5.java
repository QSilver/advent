package advent2021;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent5 {
    public static Map<Point, Integer> board = newHashMap();

    public static void main(String[] args) {
        List<Line> collect = Util.fileStream("advent2021/advent5")
                                 .map(s -> {
                                     String[] split = s.split(" -> ");
                                     return new Line(new Point(split[0]), new Point(split[1]));
                                 })
                                 .collect(Collectors.toList());


        collect.forEach(Advent5::markLine);

        long count = board.values()
                          .stream()
                          .filter(integer -> integer > 1)
                          .count();
        log.info("P1 Overlap: {}", count);
    }

    private static void markLine(Line line) {
        Pair<Integer, Integer> increment = getIncrementPair(line);

        int i = line.A.X;
        int j = line.A.Y;
        while (i != line.B.X || j != line.B.Y) {
            board.merge(new Point(i, j), 1, Integer::sum);
            i += increment.getFirst();
            j += increment.getSecond();
        }
        board.merge(new Point(i, j), 1, Integer::sum);
    }

    private static Pair<Integer, Integer> getIncrementPair(Line line) {
        int i_increment = 0;
        int j_increment = 0;

        if (line.A.Y > line.B.Y) {
            j_increment = -1;
        } else if (line.A.Y < line.B.Y) {
            j_increment = 1;
        }
        if (line.A.X > line.B.X) {
            i_increment = -1;
        } else if (line.A.X < line.B.X) {
            i_increment = 1;
        }

        return Pair.create(i_increment, j_increment);
    }

    @EqualsAndHashCode
    static class Point {
        int X;
        int Y;

        public Point(int x, int y) {
            X = x;
            Y = y;
        }

        public Point(String input) {
            String[] split = input.split(",");
            X = Integer.parseInt(split[0]);
            Y = Integer.parseInt(split[1]);
        }

        @Override
        public String toString() {
            return X + "," + Y;
        }
    }

    @AllArgsConstructor
    static class Line {
        Point A;
        Point B;
    }
}
