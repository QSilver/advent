package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util2D.Point2D;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent21 {
    // https://adventofcode.com/2023/day/21

    Set<Point2D> points = newHashSet();
    int max;

    int[] counts = new int[131 * 2 + 65 + 1];

    public Long runP1(String file, int maxStep) {
        Point2D starting = parseInput(file);
        points.add(starting);
        return (long) flood(newHashSet(starting), 0, maxStep).size();
    }

    public Long runP2(String file) {
        Point2D starting = parseInput(file);
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

    Set<Point2D> flood(Set<Point2D> current, int step, int maxStep) {
        if (step == maxStep) {
            return current;
        }

        Set<Point2D> visited = newHashSet();

        current.forEach(point -> {
            Set<Point2D> neighbours = getNeighbours(point);

            neighbours.forEach(n -> {
                if (points.contains(n)) {
                    visited.add(n);
                }
            });
        });

        counts[step + 1] = visited.size();
        return flood(visited, step + 1, maxStep);
    }

    Set<Point2D> getNeighbours(Point2D point) {
        Point2D up = new Point2D(point.row() - 1, point.col());
        Point2D down = new Point2D(point.row() + 1, point.col());
        Point2D left = new Point2D(point.row(), point.col() - 1);
        Point2D right = new Point2D(point.row(), point.col() + 1);
        return newHashSet(up, down, left, right);
    }

    private Point2D parseInput(String file) {
        List<String> lines = fileStream(file).toList();

        Point2D starting = null;
        for (int row = 0; row < lines.size(); row++) {
            for (int col = 0; col < lines.get(row).length(); col++) {
                if (lines.get(row).charAt(col) == '.') {
                    points.add(new Point2D(row, col));
                } else if (lines.get(row).charAt(col) == 'S') {
                    points.add(new Point2D(row, col));
                    starting = new Point2D(row, col);
                }
            }
        }

        max = lines.size();
        return starting;
    }
}
