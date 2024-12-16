package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util2D.Point2D;

import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static util.InputUtils.loadIntMatrix;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent10 {
    // https://adventofcode.com/2024/day/10

    int[][] map = new int[0][0];

    public Long runP1(String file) {
        return run(file, countEndpoints());
    }


    public Long runP2(String file) {
        return run(file, countPaths());
    }

    private long run(String file, Function<List<List<Point2D>>, Integer> countingFunction) {
        map = loadIntMatrix(file);
        return findStartingPoints(map).stream()
                .mapToInt(start -> tracePaths(start, countingFunction))
                .sum();
    }

    private static Function<List<List<Point2D>>, Integer> countEndpoints() {
        return paths -> (int) paths.stream().map(List::getLast).distinct().count();
    }

    private static Function<List<List<Point2D>>, Integer> countPaths() {
        return List::size;
    }

    private static List<Point2D> findStartingPoints(int[][] map) {
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

    private int tracePaths(Point2D start, Function<List<List<Point2D>>, Integer> countEndpoints) {
        List<List<Point2D>> paths = List.of(List.of(start));

        int count = 0;
        for (int height = 0; height <= 9; height++) {
            if (height == 9) {
                count += countEndpoints.apply(paths);
            }
            getNextPaths(paths, height);
        }
        return count;
    }

    private void getNextPaths(List<List<Point2D>> paths, int height) {
        List<List<Point2D>> iterationStep = newArrayList();
        for (List<Point2D> path : paths) {
            iterationStep.addAll(getPaths(path.getLast(), height, path));
        }

        paths.clear();
        paths.addAll(iterationStep);
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
            } catch (ArrayIndexOutOfBoundsException _) {
            }
        });

        return futurePaths;
    }
}
