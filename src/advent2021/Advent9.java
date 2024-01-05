package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent9 {
    static long basinSize;
    static List<Advent5.Point> lowPoints = newArrayList();

    public static void main(String[] args) {
        List<List<Integer>> map = InputUtils.fileStream("advent2021/advent9")
                                      .map(s -> Arrays.stream(s.split(""))
                                                      .map(operand -> Integer.parseInt(operand + "")))
                                      .map(intStream -> intStream.collect(Collectors.toList()))
                                      .collect(Collectors.toList());

        int riskLevel = getRiskLevel(map);
        log.info("Risk Level: {}", riskLevel);

        List<Long> basins = findBasins(map);
        log.info("P2: {}", basins.get(0) * basins.get(1) * basins.get(2));
    }

    private static List<Long> findBasins(List<List<Integer>> map) {
        List<Long> collect = lowPoints.stream()
                                      .map(point -> {
                                          boolean[][] visited = new boolean[map.size()][map.get(0)
                                                                                           .size()];
                                          basinSize = 0;
                                          floodFill(map, point.X, point.Y, visited);
                                          log.info("Basin Size: {}", basinSize);
                                          return basinSize;
                                      })
                                      .sorted()
                                      .collect(Collectors.toList());
        Collections.reverse(collect);
        return collect;
    }

    private static int getRiskLevel(List<List<Integer>> map) {
        int riskLevel = 0;
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(i)
                                   .size(); j++) {

                int pos = map.get(i)
                             .get(j);

                int greater = 0;
                int visited = 0;

                if (i > 0) {
                    visited++;
                    if (map.get(i - 1)
                           .get(j) > pos) {
                        greater++;
                    }
                }
                if (i < map.size() - 1) {
                    visited++;
                    if (map.get(i + 1)
                           .get(j) > pos) {
                        greater++;
                    }
                }
                if (j > 0) {
                    visited++;
                    if (map.get(i)
                           .get(j - 1) > pos) {
                        greater++;
                    }
                }
                if (j < map.get(i)
                           .size() - 1) {
                    visited++;
                    if (map.get(i)
                           .get(j + 1) > pos) {
                        greater++;
                    }
                }
                if (greater == visited) {
                    lowPoints.add(new Advent5.Point(i, j));
                    riskLevel += pos + 1;
                }
            }
        }
        return riskLevel;
    }

    public static void floodFill(List<List<Integer>> maze, int row, int col, boolean[][] visited) {
        if (row < 0 || col < 0 || row == maze.size() || col == maze.get(0)
                                                                   .size()
                || maze.get(row)
                       .get(col) == 9 || visited[row][col]) {
            return;
        }

        basinSize++;
        visited[row][col] = true;
        floodFill(maze, row - 1, col, visited);
        floodFill(maze, row, col - 1, visited);
        floodFill(maze, row + 1, col, visited);
        floodFill(maze, row, col + 1, visited);
    }
}
