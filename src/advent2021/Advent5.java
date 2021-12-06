package advent2021;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.Util;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent5 {
    public static Map<Point, Integer> board = newHashMap();

    public static void main(String[] args) {
        Util.fileStream("advent2021/advent5")
            .map(Line::new)
            .forEach(Advent5::markLine);

        long count = board.values()
                          .stream()
                          .filter(integer -> integer > 1)
                          .count();
        log.info("Overlap: {}", count);
    }

    private static void markLine(Line line) {
        Pair<Integer, Integer> movePair = getMovePair(line);

        int i = line.A.X;
        int j = line.A.Y;
        while (i != line.B.X || j != line.B.Y) {
            board.merge(new Point(i, j), 1, Integer::sum);
            i += movePair.getFirst();
            j += movePair.getSecond();
        }
        board.merge(new Point(i, j), 1, Integer::sum);
    }

    private static Pair<Integer, Integer> getMovePair(Line line) {
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
    }

    @AllArgsConstructor
    static class Line {
        Point A;
        Point B;

        public Line(String input) {
            String[] split = input.split(" -> ");
            A = new Point(split[0]);
            B = new Point(split[1]);
        }
    }
}
