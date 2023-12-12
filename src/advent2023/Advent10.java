package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.abs;


@Slf4j
public class Advent10 {
    char[][] map;
    Set<Point> visited = newHashSet();
    List<Point> path = newArrayList();

    static String UP = "|7F";
    static String DOWN = "|LJ";
    static String LEFT = "-LF";
    static String RIGHT = "-J7";

    public Long runP1(String file) {
        List<String> list = Util.fileStream(file).toList();

        Point start = new Point(-1, -1, "#");

        map = new char[list.size() + 2][list.getFirst().length() + 2];
        for (int row = 0; row < list.size(); row++) {
            for (int col = 0; col < list.get(row).length(); col++) {
                map[row + 1][col + 1] = list.get(row).charAt(col);
                if (map[row + 1][col + 1] == 'S') {
                    start = new Point(row + 1, col + 1, "S");
                }
            }
        }

        buildPath(start.row, start.col);
        path.addFirst(start);

        return path.size() / 2L;
    }

    public Long runP2(String file) {
        List<String> list = Util.fileStream(file).toList();

        Point start = new Point(-1, -1, "#");

        map = new char[list.size() + 2][list.getFirst().length() + 2];
        for (int row = 0; row < list.size(); row++) {
            for (int col = 0; col < list.get(row).length(); col++) {
                map[row + 1][col + 1] = list.get(row).charAt(col);
                if (map[row + 1][col + 1] == 'S') {
                    start = new Point(row + 1, col + 1, "S");
                }
            }
        }

        buildPath(start.row, start.col);
        path.addFirst(start);
        visited.add(start);

        // https://en.m.wikipedia.org/wiki/Shoelace_formula
        long area = 0L;
        for (int i = 0; i < path.size() - 1; i++) {
            area += (((long) path.get(i).row * path.get(i + 1).col) - ((long) path.get(i + 1).row * path.get(i).col));
        }
        area += (((long) path.getLast().row * path.getFirst().col) - ((long) path.getFirst().row * path.getLast().col));
        area = abs(area) / 2L;

        // https://en.m.wikipedia.org/wiki/Pick%27s_theorem
        return area + 1 - path.size() / 2;
    }

    void buildPath(int row, int col) {
        int initialRow = row;
        int initialCol = col;

        String prev = "S";

        do {
            Point up = new Point(row - 1, col, map[row - 1][col] + "");
            Point down = new Point(row + 1, col, map[row + 1][col] + "");
            Point left = new Point(row, col - 1, map[row][col - 1] + "");
            Point right = new Point(row, col + 1, map[row][col + 1] + "");

            if (UP.contains(up.c()) && !visited.contains(up) && "S|LJ".contains(prev)) {
                visited.add(up);
                path.add(up);
                prev = up.c;
                row--;
            } else if (DOWN.contains(down.c) && !visited.contains(down) && "S|7F".contains(prev)) {
                visited.add(down);
                path.add(down);
                prev = down.c;
                row++;
            } else if (LEFT.contains(left.c) && !visited.contains(left) && "S-J7".contains(prev)) {
                visited.add(left);
                path.add(left);
                prev = left.c;
                col--;
            } else if (RIGHT.contains(right.c) && !visited.contains(right) && "S-LF".contains(prev)) {
                visited.add(right);
                path.add(right);
                prev = right.c;
                col++;
            } else {
                return;
            }
        } while (!(row == initialRow && col == initialCol));
    }

    record Point(int row, int col, String c) {
    }
}
