package advent2024;

import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util2D.Direction;
import util.Util2D.Point2D;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static util.InputUtils.loadCharMatrix;

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
                .map(copy -> tracePath(copy, guard.copy()))
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
                if (map.atPos(guard.neighbour(direction)) != '#') {
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

    private static List<Character[][]> generatePossibleObstacles(Character[][] map) {
        List<Character[][]> copies = newArrayList();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                // only place on empty spots
                if (map[row][col] != '.') {
                    continue;
                }

                Character[][] copy = new Character[map.length][];
                for (int i = 0; i < map.length; i++) {
                    copy[i] = map[i].clone();
                }
                copy[row][col] = '#';
                copies.add(copy);
            }
        }
        return copies;
    }

    @NonNull
    private static Point2D findGuard(Character[][] map) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] == '^') {
                    return new Point2D(row, col);
                }
            }
        }
        log.error("Invalid Input");
        return new Point2D(-1, -1);
    }

    private record PathResult(Set<Point2D> visited, boolean isLoop) {
    }
}
