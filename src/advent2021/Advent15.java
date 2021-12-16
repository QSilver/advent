package advent2021;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import static java.lang.Math.min;

@Slf4j
public class Advent15 {
    private static final int[] DX = {-1, 1, 0, 0};
    private static final int[] DY = {0, 0, -1, 1};

    public static void main(String[] args) {
        Integer[][] map = Util.fileStream("advent2021/advent15")
                              .map(s -> Arrays.stream(s.split(""))
                                              .map(Integer::parseInt)
                                              .toArray(Integer[]::new))
                              .toArray(Integer[][]::new);

        int[][] newMap = multiplyInput(map, 5);

        int[][] dynamic = new int[newMap.length][newMap[0].length];
        dynamic[0][0] = newMap[0][0];
        for (int i = 1; i < newMap.length; i++) {
            dynamic[i][0] = newMap[i][0] + dynamic[i - 1][0];
        }
        for (int j = 1; j < newMap[0].length; j++) {
            dynamic[0][j] = newMap[0][j] + dynamic[0][j - 1];
        }

        for (int i = 1; i < newMap.length; i++) {
            for (int j = 1; j < newMap[0].length; j++) {
                dynamic[i][j] = min(dynamic[i - 1][j], dynamic[i][j - 1]) + newMap[i][j];
            }
        }

        int finalScore = dynamic[dynamic.length - 1][dynamic[0].length - 1];
        log.info("Lowest Total Risk: {}", finalScore - dynamic[0][0]);

        solve(map, 1);
        solve(map, 5);
    }

    private static void solve(Integer[][] map, int multiplier) {
        int[][] newMap = multiplyInput(map, multiplier);
        int shortestPath = getShortestPath(newMap);
        log.info("Lowest Total Risk: {}", shortestPath - newMap[0][0]);
    }

    private static int[][] multiplyInput(Integer[][] map, int m) {
        int[][] newMap = new int[map.length * m][map[0].length * m];
        for (int c = 0; c < m; c++) {
            for (int r = 0; r < m; r++) {
                for (int i1 = 0; i1 < map.length; i1++) {
                    for (int j1 = 0; j1 < map[0].length; j1++) {
                        int value = map[i1][j1] + c + r;
                        newMap[c * map.length + i1][r * map[0].length + j1] = value > 9 ? value % 10 + 1 : value;

                    }
                }
            }
        }
        return newMap;
    }

    private static int getShortestPath(int[][] newMap) {
        int[][] dist = setInitialDistances(newMap);
        PriorityQueue<Cell> queue = new PriorityQueue<>(Comparator.comparingInt(value -> value.distance));
        queue.add(new Cell(0, 0, dist[0][0]));

        while (!queue.isEmpty()) {
            Cell curr = queue.remove();
            for (int n = 0; n < 4; n++) {
                int i = curr.x + DX[n];
                int j = curr.y + DY[n];

                if (i >= 0 && i < newMap.length && j >= 0 && j < newMap[0].length) {
                    if (dist[i][j] > dist[curr.x][curr.y] + newMap[i][j]) {
                        if (dist[i][j] != Integer.MAX_VALUE) {
                            queue.remove(new Cell(i, j, dist[i][j]));
                        }

                        dist[i][j] = dist[curr.x][curr.y] + newMap[i][j];
                        queue.add(new Cell(i, j, dist[i][j]));
                    }
                }
            }
        }
        return dist[newMap.length - 1][newMap[0].length - 1];
    }

    private static int[][] setInitialDistances(int[][] newMap) {
        int[][] dist = new int[newMap.length][newMap[0].length];
        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist[0].length; j++) {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }
        dist[0][0] = newMap[0][0];
        return dist;
    }

    @AllArgsConstructor
    static class Cell {
        int x;
        int y;
        int distance;
    }
}