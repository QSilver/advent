package advent2025;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Point2D;

import java.util.List;
import java.util.Set;

import static java.lang.Math.abs;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent9 {
    // https://adventofcode.com/2025/day/9

    public Long runP1(String file) {
        return fileStream(file)
                .map(Point2D::fromCoords)
                .generateSubsets(2)
                .stream()
                .map(Advent9::getArea)
                .max(Long::compareTo)
                .orElse(0L);
    }

    private static long getArea(Set<Point2D> subset) {
        List<Point2D> list1 = subset.stream().toList();
        Point2D p1 = list1.get(0);
        Point2D p2 = list1.get(1);
        return (abs(p1.row() - p2.row()) + 1) * (abs(p1.col() - p2.col()) + 1);
    }

    public Long runP2(String file) {
        // TODO - come up with a solution that doesn't rely on slapping the points into a graphics calculator and eyeballing the result

        return 0L;
    }
}
