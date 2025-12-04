package advent2025;

import lombok.extern.slf4j.Slf4j;
import util.Point2D;

import java.util.List;
import java.util.function.Predicate;

import static util.InputUtils.get2DPoints;

@Slf4j
public class Advent4 {
    // https://adventofcode.com/2025/day/4

    public Long runP1(String file) {
        List<Point2D> points = get2DPoints(file, '@');
        return points.stream()
                .filter(lessThan4Neighbours(points))
                .count();

    }

    public Long runP2(String file) {
        long count = 0;

        List<Point2D> point2DS = get2DPoints(file, '@');
        List<Point2D> toRemove;
        do {
            toRemove = point2DS.stream()
                    .filter(lessThan4Neighbours(point2DS))
                    .toList();
            count += toRemove.size();
            point2DS.removeAll(toRemove);
        } while (!toRemove.isEmpty());

        return count;
    }

    private static Predicate<Point2D> lessThan4Neighbours(List<Point2D> point2DS) {
        return point -> point.neighbours8().stream()
                .filter(point2DS::contains)
                .count() < 4;
    }
}
