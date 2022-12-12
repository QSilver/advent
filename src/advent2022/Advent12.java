package advent2022;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static com.google.common.collect.Lists.newArrayList;

// 1052801021 - too low
@Slf4j
public class Advent12 {
    private static final int[] DX = {-1, 1, 0, 0};
    private static final int[] DY = {0, 0, -1, 1};


    public static void main(String[] args) {
        String[][] map = Util.fileStream("advent2022/advent12")
                .map(s -> Arrays.stream(s.split(""))
                        .toArray(String[]::new))
                .toArray(String[][]::new);

        List<Cell> startList = newArrayList();
        Cell start = new Cell(0, 0, 0);
        Cell end = new Cell(0, 0, Integer.MAX_VALUE);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j].equals("S")) {
                    start = new Cell(i, j, 0);
                    map[i][j] = "a";
                }
                if (map[i][j].equals("a")) {
                    startList.add(new Cell(i, j, 0));
                }
                if (map[i][j].equals("E")) {
                    end = new Cell(i, j, Integer.MAX_VALUE);
                    map[i][j] = "z";
                }
            }
        }

        log.info("Shortest Path: {}", getShortestPath(map, start, end));
        Cell finalEnd = end;
        int minHikingPath = startList.stream().map(cell -> getShortestPath(map, cell, finalEnd)).mapToInt(value -> value).min().getAsInt();
        log.info("Min Hiking Path: {}", minHikingPath);
    }

    private static int getShortestPath(String[][] newMap, Cell start, Cell end) {
        int[][] dist = setInitialDistances(newMap);
        dist[start.x][start.y] = 0;
        PriorityQueue<Cell> queue = new PriorityQueue<>(Comparator.comparingInt(value -> value.distance));
        queue.add(start);

        while (!queue.isEmpty()) {
            Cell curr = queue.remove();
            for (int n = 0; n < 4; n++) {
                int i = curr.x + DX[n];
                int j = curr.y + DY[n];

                if (i >= 0 && i < newMap.length && j >= 0 && j < newMap[0].length) {
                    if (dist[i][j] > dist[curr.x][curr.y] + 1) {
                        if (newMap[i][j].charAt(0) <= newMap[curr.x][curr.y].charAt(0) + 1) {
                            if (dist[i][j] != Integer.MAX_VALUE) {
                                queue.remove(new Cell(i, j, dist[i][j]));
                            }

                            dist[i][j] = dist[curr.x][curr.y] + 1;
                            queue.add(new Cell(i, j, dist[i][j]));
                        }
                    }
                }
            }
        }
        return dist[end.x][end.y];
    }

    private static int[][] setInitialDistances(String[][] newMap) {
        int[][] dist = new int[newMap.length][newMap[0].length];
        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist[0].length; j++) {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }
        return dist;
    }

    @AllArgsConstructor
    static class Cell {
        int x;
        int y;
        int distance;
    }
}
