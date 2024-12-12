package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static util.Util2D.*;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent12 {
    // https://adventofcode.com/2024/day/12

    public Long runP1(String file) {
        return getSurfaces(file).stream()
                .map(surface -> surface.area() * surface.perimeter())
                .mapToLong(value -> value)
                .sum();
    }

    public Long runP2(String file) {
        return getSurfaces(file).stream()
                .map(surface -> surface.area() * surface.insidePoints())
                .mapToLong(value -> value)
                .sum();
    }

    Character[][] map;
    List<PointWithLabel> points;

    private List<Surface> getSurfaces(String file) {
        map = loadCharMatrix(file);
        points = get2DPointsIgnore(file, '.');

        List<Surface> surfaces = newArrayList();
        while (!points.isEmpty()) {
            surfaces.add(getSurfaceFromPoint(points.getFirst()));
        }
        return surfaces;
    }

    private Surface getSurfaceFromPoint(PointWithLabel starting) {
        Set<PointWithLabel> currentSurface = newHashSet();

        Stack<PointWithLabel> toProcess = new Stack<>();
        toProcess.add(starting);
        currentSurface.add(starting);

        while (!toProcess.isEmpty()) {
            PointWithLabel current = toProcess.pop();

            Set<PointWithLabel> validNeighbours = current.point2D().neighbours4().stream()
                    .map(point2D -> getLabel(point2D, map))
                    .filter(Objects::nonNull)
                    .filter(neighbour -> neighbour.label() == current.label())
                    .filter(pointWithLabel -> !currentSurface.contains(pointWithLabel))
                    .collect(Collectors.toSet());

            currentSurface.addAll(validNeighbours);
            toProcess.addAll(validNeighbours);
        }

        points.removeAll(currentSurface);
        Set<Point2D> set = currentSurface.stream().map(PointWithLabel::point2D).collect(Collectors.toSet());

        // counting sides of a polygon is the same as counting corners
        long surfaceCorners = currentSurface.stream()
                .map(PointWithLabel::point2D)
                .mapToInt(point -> countCornersForPoint(point, set))
                .sum();

        return new Surface(calculatePerimeter(set), set.size(), surfaceCorners, 0, set.stream().toList());
    }

    private static int countCornersForPoint(Point2D point, Set<Point2D> set) {
        int corners = 0;
        // count outer corners
        if (!set.contains(point.UP()) && !set.contains(point.LEFT())) {
            corners++;
        }
        if (!set.contains(point.UP()) && !set.contains(point.RIGHT())) {
            corners++;
        }
        if (!set.contains(point.DOWN()) && !set.contains(point.LEFT())) {
            corners++;
        }
        if (!set.contains(point.DOWN()) && !set.contains(point.RIGHT())) {
            corners++;
        }
        // count inner corners
        if (set.contains(point.UP()) && set.contains(point.LEFT()) && !set.contains(point.UP().LEFT())) {
            corners++;
        }
        if (set.contains(point.UP()) && set.contains(point.RIGHT()) && !set.contains(point.UP().RIGHT())) {
            corners++;
        }
        if (set.contains(point.DOWN()) && set.contains(point.LEFT()) && !set.contains(point.DOWN().LEFT())) {
            corners++;
        }
        if (set.contains(point.DOWN()) && set.contains(point.RIGHT()) && !set.contains(point.DOWN().RIGHT())) {
            corners++;
        }
        return corners;
    }

    // assume each point adds 4 to the perimeter
    private Long calculatePerimeter(Set<Point2D> points) {
        long starting = points.size() * 4L;

        for (Point2D point : points) {
            // for each neighbour of each point subtract the intersection
            for (Point2D neighbour : point.neighbours4()) {
                if (points.contains(neighbour)) {
                    starting -= 1;
                }
            }
        }

        return starting;
    }

    private static PointWithLabel getLabel(Point2D point2D, Character[][] map) {
        try {
            return new PointWithLabel(point2D, map[(int) point2D.row()][(int) point2D.col()]);
        } catch (ArrayIndexOutOfBoundsException _) {
            return null;
        }
    }
}
