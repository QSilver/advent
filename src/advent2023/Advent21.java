package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util.Point;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static util.InputUtils.lines;

@Slf4j
public class Advent21 {
    // https://adventofcode.com/2023/day/21

    Set<Point> points = newHashSet();
    int max;

    int[] counts = new int[131 * 2 + 65 + 1];

    public Long runP1(String file, int maxStep) {
        Point starting = parseInput(file);
        points.add(starting);
        return (long) flood(newHashSet(starting), 0, maxStep).size();
    }

    public Long runP2(String file) {
        Point starting = parseInput(file);
        points.add(starting);

        flood(newHashSet(starting), 0, 131 * 2 + 65);

        // map size = 131
        // we can reach the edge of the map in 131/2 = 65 steps
        //
        // because the row and col containing S are empty and have no rocks
        // we will always reach the 4 adjacent maps and their centers at the same time
        //
        // starting from step 65, every 131 steps, the number of points increases (velocity)
        // every 131 points that increase increases by a constant number (acceleration)
        //
        // at step

        int start = counts[65];
        int startingSpeed = counts[131 + 65] - start;
        int acceleration = counts[131 * 2 + 65] - counts[131 + 65] - startingSpeed;
        int x = 26501365 / 131;

        long distance = start;
        long speed = startingSpeed;
        for (int i = 0; i < x; i++) {
            distance += speed;
            speed += acceleration;
        }
        return distance;
    }

    Set<Point> flood(Set<Point> current, int step, int maxStep) {
        if (step == maxStep) {
            return current;
        }

        Set<Point> visited = newHashSet();

        current.forEach(point -> {
            Set<Point> neighbours = getNeighbours(point);

            neighbours.forEach(n -> {
                if (points.contains(n)) {
                    visited.add(n);
                }
            });
        });

        counts[step + 1] = visited.size();
        return flood(visited, step + 1, maxStep);
    }

    Set<Point> getNeighbours(Point point) {
        Point up = new Point(point.row() - 1, point.col(), 0);
        Point down = new Point(point.row() + 1, point.col(), 0);
        Point left = new Point(point.row(), point.col() - 1, 0);
        Point right = new Point(point.row(), point.col() + 1, 0);
        return newHashSet(up, down, left, right);
    }

    private Point parseInput(String file) {
        List<String> lines = lines(file);

        Point starting = null;
        for (int row = 0; row < lines.size(); row++) {
            for (int col = 0; col < lines.get(row).length(); col++) {
                if (lines.get(row).charAt(col) == '.') {
                    points.add(new Point(row, col, 0));
                } else if (lines.get(row).charAt(col) == 'S') {
                    points.add(new Point(row, col, 0));
                    starting = new Point(row, col, 0);
                }
            }
        }

        max = lines.size();
        return starting;
    }
}
