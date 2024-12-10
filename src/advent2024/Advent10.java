package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util2D.Point2D;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.collect.Lists.newArrayList;
import static util.Util2D.loadIntMatrix;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent10 {
    // https://adventofcode.com/2024/day/10

    int[][] map = new int[0][0];

    public Long runP1(String file) {
        map = loadIntMatrix(file);
        List<Point2D> starts = findStart(map);

        AtomicLong globalCount = new AtomicLong();
        starts.forEach(start -> {
            List<List<Point2D>> paths = newArrayList();
            paths.add(newArrayList(start));

            int count = 0;
            for (int height = 0; height <= 9; height++) {
                if (height == 9) {
                    count += (int) paths.stream().map(List::getLast).distinct().count();
                }

                List<List<Point2D>> iter = newArrayList();
                for (List<Point2D> path : paths) {
                    iter.addAll(getPaths(path.getLast(), height, path));
                }
                paths.clear();
                paths.addAll(iter);
            }

            globalCount.addAndGet(count);
        });

        return globalCount.get();
    }

    private static List<Point2D> findStart(int[][] map) {
        List<Point2D> points = newArrayList();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] == 0) {
                    points.add(new Point2D(row, col));
                }
            }
        }
        return points;
    }

    private List<List<Point2D>> getPaths(Point2D current, int currentHeight, List<Point2D> currentPath) {
        List<List<Point2D>> futurePaths = newArrayList();

        current.neighbours4().forEach(neighbour -> {
            try {
                if (map[(int) neighbour.row()][(int) neighbour.col()] == currentHeight + 1) {
                    List<Point2D> pathCopy = newArrayList(currentPath);
                    pathCopy.add(neighbour);
                    futurePaths.add(pathCopy);
                }
            } catch (Exception _) {

            }
        });

        return futurePaths;
    }

    public Long runP2(String file) {
        map = loadIntMatrix(file);
        List<Point2D> starts = findStart(map);

        AtomicLong globalCount = new AtomicLong();
        starts.forEach(start -> {
            List<List<Point2D>> paths = newArrayList();
            paths.add(newArrayList(start));

            int count = 0;
            for (int height = 0; height <= 9; height++) {
                if (height == 9) {
                    count += paths.size();
                }

                List<List<Point2D>> iter = newArrayList();
                for (List<Point2D> path : paths) {
                    iter.addAll(getPaths(path.getLast(), height, path));
                }
                paths.clear();
                paths.addAll(iter);
            }

            globalCount.addAndGet(count);
        });

        return globalCount.get();
    }
}
