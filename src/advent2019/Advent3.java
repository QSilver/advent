package advent2019;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.ToString;
import util.InputUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class Advent3 {
    public static void main(String[] args) {
        System.out.println(solve());
    }

    private static int solve() {
        List<String> wires = InputUtils.fileStream("advent2019/advent3")
                                 .collect(Collectors.toList());

        List<Point> wire1 = buildWire(newArrayList(wires.get(0)
                                                        .split(",")).stream()
                                                                    .map(Advent3::step)
                                                                    .collect(Collectors.toList()));
        List<Point> wire2 = buildWire(newArrayList(wires.get(1)
                                                        .split(",")).stream()
                                                                    .map(Advent3::step)
                                                                    .collect(Collectors.toList()));

        Set<Point> intersections = newHashSet();
        for (int i = 0; i < wire2.size() - 1; i++) {
            for (int j = 0; j < wire1.size() - 1; j++) {
                Point a = wire2.get(i);
                Point b = wire2.get(i + 1);
                Point c = wire1.get(j);
                Point d = wire1.get(j + 1);
                Point intersect = intersect(a, b, c, d);
                intersections.add(intersect);
            }
        }

        OptionalInt min = intersections.stream()
                                       .map(point -> point.steps)
                                       .filter(value -> value != 0)
                                       .mapToInt(value -> value)
                                       .min();
        return min.isPresent() ? min.getAsInt() : 0;
    }

    private static Point intersect(Point a, Point b, Point c, Point d) {
        // Line AB represented as a1x + b1y = c1
        double a1 = b.y - a.y;
        double b1 = a.x - b.x;
        double c1 = a1 * (a.x) + b1 * (a.y);

        // Line CD represented as a2x + b2y = c2
        double a2 = d.y - c.y;
        double b2 = c.x - d.x;
        double c2 = a2 * (c.x) + b2 * (c.y);

        double determinant = a1 * b2 - a2 * b1;

        if (Math.abs(determinant) > 0.0001 && validateScalars(a, b, c, d)) {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            return new Point((int) x, (int) y, calculateSteps(a, b, c, d, (int) x, (int) y));
        }
        return new Point(0, 0, 0);
    }

    private static int calculateSteps(Point a, Point b, Point c, Point d, int x, int y) {
        Point line1Point1 = a.steps < b.steps ? a : b;
        Point line2Point1 = c.steps < d.steps ? c : d;

        int line1XSteps = Math.abs(line1Point1.x - x);
        int line1YSteps = Math.abs(line1Point1.y - y);
        int line2XSteps = Math.abs(line2Point1.x - x);
        int line2YSteps = Math.abs(line2Point1.y - y);

        return line1XSteps + line1YSteps + line2XSteps + line2YSteps + line1Point1.steps + line2Point1.steps;
    }

    private static boolean validateScalars(Point a, Point b, Point c, Point d) {
        double i = (d.x - c.x) * (a.y - b.y) - (a.x - b.x) * (d.y - c.y) * 1.0;
        double ta = ((c.y - d.y) * (a.x - c.x) + (d.x - c.x) * (a.y - c.y)) / i;
        double tb = ((a.y - b.y) * (a.x - c.x) + (b.x - a.x) * (a.y - c.y)) / i;
        return 0 <= ta && ta <= 1 && 0 <= tb && tb <= 1;
    }

    private static List<Point> buildWire(List<Point> wire1Deltas) {
        List<Point> wire1 = new ArrayList<>();
        Point current = new Point(0, 0, 0);
        wire1.add(current);
        for (Point delta : wire1Deltas) {
            current = moveToPoint(wire1, current, delta);
        }
        return wire1;
    }

    private static Point moveToPoint(List<Point> wire, Point current, Point delta) {
        Point newPoint = new Point(current.x + delta.x, current.y + delta.y, current.steps + delta.steps);
        wire.add(newPoint);
        return newPoint;
    }

    private static Point step(String move) {
        int value = Integer.parseInt(move.substring(1));
        if (move.charAt(0) == 'R') {
            return new Point(value, 0, value);
        } else if (move.charAt(0) == 'L') {
            return new Point(-1 * value, 0, value);
        } else if (move.charAt(0) == 'U') {
            return new Point(0, value, value);
        } else if (move.charAt(0) == 'D') {
            return new Point(0, -1 * value, value);
        }
        return new Point(0, 0, 0);
    }
}

@ToString
@AllArgsConstructor
class Point {
    int x;
    int y;
    int steps;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }
}
