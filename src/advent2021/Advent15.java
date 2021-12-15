package advent2021;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.PriorityQueue;

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
        PriorityQueue<Cell> queue = new PriorityQueue<>(newMap.length * newMap[0].length);
        queue.add(new Cell(0, 0, dist[0][0]));

        while (!queue.isEmpty()) {
            Cell curr = queue.poll();
            for (int i = 0; i < 4; i++) {
                int rows = curr.x + DX[i];
                int cols = curr.y + DY[i];

                if (rows >= 0 && rows < newMap.length && cols >= 0 && cols < newMap[0].length) {
                    if (dist[rows][cols] > dist[curr.x][curr.y] + newMap[rows][cols]) {
                        if (dist[rows][cols] != Integer.MAX_VALUE) {
                            Cell adj = new Cell(rows, cols, dist[rows][cols]);
                            queue.remove(adj);
                        }

                        dist[rows][cols] = dist[curr.x][curr.y] + newMap[rows][cols];
                        queue.add(new Cell(rows, cols, dist[rows][cols]));
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
    static class Cell implements Comparable<Cell> {
        int x;
        int y;
        int distance;

        @Override
        public int compareTo(Cell other) {
            return Integer.compare(this.distance, other.distance);
        }
    }
}