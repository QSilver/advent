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
        List<Point2D> pointList = get2DPoints(file, '@');
        return pointList.stream()
                .filter(lessThan4Neighbours(pointList))
                .count();
    }

    public Long runP2(String file) {
        List<Point2D> pointList = get2DPoints(file, '@');

        long count = 0;
        List<Point2D> toRemove;
        do {
            toRemove = pointList.stream()
                    .filter(lessThan4Neighbours(pointList))
                    .toList();
            count += toRemove.size();
        } while (pointList.removeAll(toRemove));

        return count;
    }

    private static Predicate<Point2D> lessThan4Neighbours(List<Point2D> pointList) {
        return point -> point.neighbours8().stream()
                .filter(pointList::contains)
                .count() < 4;
    }
}
