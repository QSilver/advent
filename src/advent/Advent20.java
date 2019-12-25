package advent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

public class Advent20 {
    private static final int ROWS = 121; //121
    private static final int COLS = 123; //124
    static char[][] map = new char[ROWS][COLS];
    static int[][] mapCopy = new int[ROWS][COLS];

    static Map<String, Point> tempPortals = newHashMap();
    static Map<String, Portal> portals = newHashMap();

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        List<String> strings = Util.fileStream("advent20").collect(Collectors.toList());

        Arrays.stream(map).forEach(chars -> Arrays.fill(chars, '#'));
        Arrays.stream(mapCopy).forEach(chars -> Arrays.fill(chars, -1));

        processMap(strings);
        encodePortals(strings);

        fill(tempPortals.get("AA"), 0);

        draw();

        Point end = tempPortals.get("ZZ");
        System.out.println(mapCopy[end.y][end.x]);
    }

    private static void draw() {
        for (int[] chars : mapCopy) {
            for (int i : chars) {
                if (i == -1) {
                    System.out.print("#");
                } else {
                    System.out.print(i % 10);
                }
            }
            System.out.println();
        }
        System.out.println("------------------------");
    }

    private static void processMap(List<String> strings) {
        for (int row = 0; row < strings.size(); row++) {
            for (int col = 0; col < strings.get(row).length(); col++) {
                map[row][col] = strings.get(row).charAt(col);
                if (map[row][col] == '#') {
                    mapCopy[row][col] = -1;
                } else if (map[row][col] == '.') {
                    mapCopy[row][col] = Integer.MAX_VALUE;
                }
            }
        }
    }

    private static void encodePortals(List<String> strings) {
        for (int row = 0; row < strings.size(); row++) {
            for (int col = 0; col < strings.get(row).length(); col++) {
                if ((row == 2 || row == 88) && map[row][col] == '.' && map[row - 1][col] > 64) { //88
                    matchPortal(new Point(col, row, 0), map[row - 2][col] + "" + map[row - 1][col]);
                }
                if ((row == 32 || row == ROWS - 3) && map[row][col] == '.' && map[row + 1][col] > 64) { //32
                    matchPortal(new Point(col, row, 0), map[row + 1][col] + "" + map[row + 2][col]);
                }
                if ((col == 2 || col == 90) && map[row][col] == '.' && map[row][col - 1] > 64) { //90
                    matchPortal(new Point(col, row, 0), map[row][col - 2] + "" + map[row][col - 1]);
                }
                if ((col == 32 || col == COLS - 3) && map[row][col] == '.' && map[row][col + 1] > 64) { //32
                    matchPortal(new Point(col, row, 0), map[row][col + 1] + "" + map[row][col + 2]);
                }
            }
        }
    }

    private static void matchPortal(Point value, String key) {
        if (tempPortals.containsKey(key)) {
            portals.put(key, new Portal(tempPortals.get(key), value));
        }
        tempPortals.put(key, value);
    }

    private static void fill(Point toFill, int number) {
        mapCopy[toFill.y][toFill.x] = number;
        if (mapCopy[toFill.y][toFill.x - 1] > number) {
            fillWithPortal(number + 1, new Point(toFill.x - 1, toFill.y, 0));
        }
        if (mapCopy[toFill.y][toFill.x + 1] > number) {
            fillWithPortal(number + 1, new Point(toFill.x + 1, toFill.y, 0));
        }
        if (mapCopy[toFill.y - 1][toFill.x] > number) {
            fillWithPortal(number + 1, new Point(toFill.x, toFill.y - 1, 0));
        }
        if (mapCopy[toFill.y + 1][toFill.x] > number) {
            fillWithPortal(number + 1, new Point(toFill.x, toFill.y + 1, 0));
        }
    }

    private static void fillWithPortal(int number, Point newPoint) {
        Optional<Map.Entry<String, Portal>> optionalEntry = portals.entrySet().stream().filter(entry -> entry.getValue().contains(newPoint)).findFirst();
        if (optionalEntry.isPresent()) {
            fill(portals.get(optionalEntry.get().getKey()).getOpposite(newPoint), number + 1);
        } else {
            if (!newPoint.equals(tempPortals.get("AA"))) {
                fill(newPoint, number);
            }
        }
    }
}

class Portal {
    private Point point1;
    private Point point2;

    public Portal(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public Point getOpposite(Point p) {
        return p.equals(point1) ? point2 : point1;
    }

    public boolean contains(Point p) {
        return p.equals(point1) || p.equals(point2);
    }
}
