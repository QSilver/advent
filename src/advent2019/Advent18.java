package advent2019;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class Advent18 {
    private static Point player;
    private static final Map<Character, Point> keys = newHashMap();
    private static final Map<Character, Point> doors = newHashMap();

    private static final int[][] adjacencyMatrix = new int[123][123];

    private static final List<String> strings = InputUtils.fileStream("advent2019/advent18")
                                                    .collect(Collectors.toList());

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        int[][] map = processMap();

        Map<Character, Point> objects = newHashMap();
        objects.putAll(keys);
        objects.putAll(doors);
        objects.put('@', player);

        for (Map.Entry<Character, Point> entry1 : objects.entrySet()) {
            for (Map.Entry<Character, Point> entry2 : objects.entrySet()) {
                adjacencyMatrix[entry1.getKey()][entry2.getKey()] = fill(map, entry1.getValue(), entry2.getValue());
            }
        }

        log.info("");
    }

    private static int fill(int[][] prevMap, Point start, Point end) {
        int[][] map = copyMap(prevMap);

        List<Point> nextPoints = newArrayList();
        nextPoints.add(start);
        map[start.y][start.x] = start.steps;

        while (!nextPoints.isEmpty()) {
            List<Point> toAdd = newArrayList();
            List<Point> toRemove = newArrayList();
            nextPoints.forEach(point -> {
                toRemove.add(point);
                map[point.y][point.x] = point.steps;

                Point top = new Point(point.x, point.y - 1, point.steps + 1);
                Point bottom = new Point(point.x, point.y + 1, point.steps + 1);
                Point left = new Point(point.x - 1, point.y, point.steps + 1);
                Point right = new Point(point.x + 1, point.y, point.steps + 1);

                if (map[top.y][top.x] > point.steps) {
                    toAdd.add(top);
                }
                if (map[bottom.y][bottom.x] > point.steps) {
                    toAdd.add(bottom);
                }
                if (map[left.y][left.x] > point.steps) {
                    toAdd.add(left);
                }
                if (map[right.y][right.x] > point.steps) {
                    toAdd.add(right);
                }
            });

            nextPoints.addAll(toAdd);
            nextPoints.removeAll(toRemove);
        }

        return map[end.y][end.x];
    }

    private static int[][] copyMap(int[][] prevMap) {
        int[][] map = new int[prevMap.length][prevMap[0].length];
        for (int row = 0; row < prevMap.length; row++) {
            System.arraycopy(prevMap[row], 0, map[row], 0, prevMap[0].length);
        }
        return map;
    }

    private static int[][] processMap() {
        String firstLine = strings.get(0);
        int[][] map = new int[strings.size()][firstLine.length()];
        for (int row = 0; row < strings.size(); row++) {
            String line = strings.get(row);
            for (int col = 0; col < line.length(); col++) {
                char value = line.charAt(col);
                if (value == '#') {
                    map[row][col] = -1;
                } else if (value == '@') {
                    player = new Point(col, row, 0);
                    map[row][col] = Integer.MAX_VALUE;
                } else if (value >= 65 && value <= 90) {
                    map[row][col] = Integer.MAX_VALUE;
                    doors.put(value, new Point(col, row, 0));
                } else if (value >= 97 && value <= 122) {
                    map[row][col] = Integer.MAX_VALUE;
                    keys.put(value, new Point(col, row, 0));
                } else if (value == '.') {
                    map[row][col] = Integer.MAX_VALUE;
                } else {
                    log.info("Unknown character:-{}- at row:{} col:{}", value, row, col);
                    throw new UnsupportedOperationException();
                }
            }
        }
        return map;
    }
}