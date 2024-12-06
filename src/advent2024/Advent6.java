package advent2024;

import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util.Direction;
import util.Util2D.Point2D;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static util.Util2D.loadCharMatrix;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent6 {
    // https://adventofcode.com/2024/day/6

    public Integer runP1(String file) {
        Character[][] map = loadCharMatrix(file);
        Point2D guard = findGuard(map);

        return tracePath(map, guard).visited.size();
    }

    public Integer runP2(String file) {
        Character[][] map = loadCharMatrix(file);
        Point2D guard = findGuard(map);

        return (int) generatePossibleObstacles(map).stream()
                .map(copy -> tracePath(copy, new Point2D(guard.row(), guard.col())))
                .filter(pathResult -> pathResult.isLoop)
                .count();
    }

    private static PathResult tracePath(Character[][] map, Point2D guard) {
        Set<Point2D> visited = newHashSet();
        visited.add(guard);

        Direction direction = Direction.UP;

        int step = 0;
        while (true) {
            if (++step > 25000) { // assume we are in a loop
                return new PathResult(visited, true);
            }

            try {
                if (map.neighbour(guard.neighbour(direction)) != '#') {
                    guard = guard.neighbour(direction);
                } else {
                    direction = direction.clockwise();
                }

                visited.add(new Point2D(guard.row(), guard.col()));
            } catch (ArrayIndexOutOfBoundsException _) {
                break;
            }
        }

        return new PathResult(visited, false);
    }

    private static List<Character[][]> generatePossibleObstacles(Character[][] matrix) {
        List<Character[][]> copies = newArrayList();
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                // only place on empty spots
                if (matrix[row][col] != '.') {
                    continue;
                }

                Character[][] copy = new Character[matrix.length][];
                for (int i = 0; i < matrix.length; i++) {
                    copy[i] = matrix[i].clone();
                }
                copy[row][col] = '#';
                copies.add(copy);
            }
        }
        return copies;
    }

    @NonNull
    private static Point2D findGuard(Character[][] matrix) {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] == '^') {
                    return new Point2D(row, col);
                }
            }
        }
        log.error("Invalid Input");
        return new Point2D(-1, -1);
    }

    private static void display(char[][] matrix, Set<Point2D> visited) {
        StringBuilder display = new StringBuilder();
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (visited.contains(new Point2D(row, col))) {
                    display.append("X");
                } else {
                    display.append(matrix[row][col]);
                }
            }
            display.append("\n");
        }

        System.out.println(display);
    }

    private record PathResult(Set<Point2D> visited, boolean isLoop) {
    }
}
