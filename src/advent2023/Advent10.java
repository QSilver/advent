package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;
import util.Util.Point;
import util.Util.Surface;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static util.Util.calculateSurface;


@Slf4j
public class Advent10 {
    // https://adventofcode.com/2023/day/10
    public static final String UP = "|7F";
    public static final String DOWN = "|LJ";
    public static final String LEFT = "-LF";
    public static final String RIGHT = "-J7";
    char[][] map;

    public Long runP1(String file) {
        PointWithSymbol start = parseInputAndGetStartPoint(file);

        List<PointWithSymbol> path = buildPath((int) start.p.row(), (int) start.p.col());
        path.addFirst(start);

        return path.size() / 2L;
    }

    public Long runP2(String file) {
        PointWithSymbol start = parseInputAndGetStartPoint(file);

        List<PointWithSymbol> path = buildPath((int) start.p.row(), (int) start.p.col());
        path.addFirst(start);

        Surface surface = calculateSurface(path.stream().map(pointWithSymbol -> pointWithSymbol.p).toList());
        return surface.insidePoints();
    }

    private PointWithSymbol parseInputAndGetStartPoint(String file) {
        List<String> list = InputUtils.fileStream(file).toList();

        PointWithSymbol start = new PointWithSymbol(new Point(-1, -1, 0), "#");

        map = new char[list.size() + 2][list.getFirst().length() + 2];
        for (int row = 0; row < list.size(); row++) {
            for (int col = 0; col < list.get(row).length(); col++) {
                map[row + 1][col + 1] = list.get(row).charAt(col);
                if (map[row + 1][col + 1] == 'S') {
                    start = new PointWithSymbol(new Point(row + 1, col + 1, 0), "S");
                }
            }
        }
        return start;
    }

    List<PointWithSymbol> buildPath(int row, int col) {
        int initialRow = row;
        int initialCol = col;
        String prev = "S";

        List<PointWithSymbol> path = newArrayList();
        do {
            PointWithSymbol up = new PointWithSymbol(new Point(row - 1, col, 0), map[row - 1][col] + "");
            PointWithSymbol down = new PointWithSymbol(new Point(row + 1, col, 0), map[row + 1][col] + "");
            PointWithSymbol left = new PointWithSymbol(new Point(row, col - 1, 0), map[row][col - 1] + "");
            PointWithSymbol right = new PointWithSymbol(new Point(row, col + 1, 0), map[row][col + 1] + "");

            if (UP.contains(up.symbol()) && ("S" + DOWN).contains(prev) && !path.contains(up)) {
                path.add(up);
                prev = up.symbol;
                row--;
            } else if (DOWN.contains(down.symbol) && ("S" + UP).contains(prev) && !path.contains(down)) {
                path.add(down);
                prev = down.symbol;
                row++;
            } else if (LEFT.contains(left.symbol) && ("S" + RIGHT).contains(prev) && !path.contains(left)) {
                path.add(left);
                prev = left.symbol;
                col--;
            } else if (RIGHT.contains(right.symbol) && ("S" + LEFT).contains(prev) && !path.contains(right)) {
                path.add(right);
                prev = right.symbol;
                col++;
            } else {
                break;
            }
        } while (!(row == initialRow && col == initialCol));
        return path;
    }

    record PointWithSymbol(Point p, String symbol) {

    }
}
