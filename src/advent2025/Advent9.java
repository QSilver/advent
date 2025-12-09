package advent2025;

import lombok.extern.slf4j.Slf4j;
import util.Point2D;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.abs;
import static util.InputUtils.fileStream;
import static util.Util.forEachTwo;

@Slf4j
public class Advent9 {
    // https://adventofcode.com/2025/day/9

    public Long runP1(String file) {
        List<Point2D> points = fileStream(file)
                .map(Point2D::fromCoords)
                .toList();

        final AtomicLong max = new AtomicLong(0L);
        AtomicReference<Pair> maxPair = new AtomicReference<>(null);

        forEachTwo(points, (p1, p2) -> getAndUpdateMaxDistance(p1, p2, max).ifPresent(maxPair::set));

        return getArea(maxPair.get().p1, maxPair.get().p2);
    }

    private static Optional<Pair> getAndUpdateMaxDistance(Point2D p1, Point2D p2, AtomicLong max) {
        long dist = p1.manhattanDistanceTo(p2);
        if (dist > max.get()) {
            max.set(dist);
            return Optional.of(new Pair(p1, p2));
        }
        return Optional.empty();
    }

    record Pair(Point2D p1, Point2D p2) {

    }

    private static Long getArea(Point2D p1, Point2D p2) {
        return (abs(p1.row() - p2.row()) + 1) * (abs(p1.col() - p2.col()) + 1);
    }

    public Long runP2(String file) {
        // TODO - come up with a solution that doesn't rely on slapping the points into a graphics calculator and eyeballing the result

        return 0L;
    }
}
