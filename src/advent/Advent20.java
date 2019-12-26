package advent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class Advent20 {
    private static final int ROWS = 121; //121
    private static final int COLS = 123; //123
    public static final int INNER_TOP = 32; //32
    public static final int INNER_BOTTOM = 88; //88
    public static final int INNER_LEFT = 32; //32
    public static final int INNER_RIGHT = 90; //90
    public static final int OUTER_TOP = 2;
    public static final int OUTER_BOTTOM = ROWS - 3;
    public static final int OUTER_LEFT = 2;
    public static final int OUTER_RIGHT = COLS - 3;
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

        fill();

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
                if ((row == OUTER_TOP || row == INNER_BOTTOM) && map[row][col] == '.' && map[row - 1][col] > 64) { //88
                    matchPortal(new Point(col, row, 0), map[row - 2][col] + "" + map[row - 1][col]);
                }
                if ((row == INNER_TOP || row == OUTER_BOTTOM) && map[row][col] == '.' && map[row + 1][col] > 64) { //32
                    matchPortal(new Point(col, row, 0), map[row + 1][col] + "" + map[row + 2][col]);
                }
                if ((col == OUTER_LEFT || col == INNER_RIGHT) && map[row][col] == '.' && map[row][col - 1] > 64) { //90
                    matchPortal(new Point(col, row, 0), map[row][col - 2] + "" + map[row][col - 1]);
                }
                if ((col == INNER_LEFT || col == OUTER_RIGHT) && map[row][col] == '.' && map[row][col + 1] > 64) { //32
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

    private static void fill() {
        List<Point> nextPoints = newArrayList();
        List<Point> toAdd = newArrayList();
        List<Point> toRemove = newArrayList();

        Point start = tempPortals.get("AA");
        start.steps = 0;
        nextPoints.add(start);
        mapCopy[start.y][start.x] = start.steps;

        while (!nextPoints.isEmpty()) {
            nextPoints.forEach(point -> {
                toRemove.add(point);
                mapCopy[point.y][point.x] = point.steps;

                if (mapCopy[point.y][point.x - 1] > point.steps) {
                    addWithPortal(toAdd, new Point(point.x - 1, point.y, point.steps + 1));
                }
                if (mapCopy[point.y][point.x + 1] > point.steps) {
                    addWithPortal(toAdd, new Point(point.x + 1, point.y, point.steps + 1));
                }
                if (mapCopy[point.y - 1][point.x] > point.steps) {
                    addWithPortal(toAdd, new Point(point.x, point.y - 1, point.steps + 1));
                }
                if (mapCopy[point.y + 1][point.x] > point.steps) {
                    addWithPortal(toAdd, new Point(point.x, point.y + 1, point.steps + 1));
                }
            });

            nextPoints.addAll(toAdd);
            nextPoints.removeAll(toRemove);
        }
    }

    private static void addWithPortal(List<Point> toAdd, Point newPoint) {
        Optional<Map.Entry<String, Portal>> optionalEntry = portals.entrySet().stream().filter(entry -> entry.getValue().contains(newPoint)).findFirst();
        if (optionalEntry.isPresent()) {
            Point opposite = portals.get(optionalEntry.get().getKey()).getOpposite(newPoint);
            opposite.steps = newPoint.steps + 1;
            toAdd.add(opposite);
        } else {
            if (!newPoint.equals(tempPortals.get("AA"))) {
                toAdd.add(newPoint);
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
