package advent2019;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.ToString;
import util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

// 959 - too low

public class Advent20 {
    private static final int HEIGHT = 50;
    private static final int ROWS = 121; //121 //37ex
    private static final int COLS = 123; //123 //45ex

    public static final int INNER_TOP = 32; //32 //8ex
    public static final int INNER_BOTTOM = 88; //88 //28ex
    public static final int INNER_LEFT = 32; //32 //8ex
    public static final int INNER_RIGHT = 90; //90 //36ex

    public static final int OUTER_TOP = 2;
    public static final int OUTER_BOTTOM = ROWS - 3;
    public static final int OUTER_LEFT = 2;
    public static final int OUTER_RIGHT = COLS - 3;
    static char[][] map = new char[ROWS][COLS];
    static int[][][] mapCopy = new int[HEIGHT][ROWS][COLS];

    static Map<String, XYZ_Point> tempPortals = newHashMap();
    static Map<String, Portal> portals = newHashMap();

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        List<String> strings = Util.fileStream("advent2019/advent20")
                                   .collect(Collectors.toList());

        Arrays.stream(map).forEach(chars -> Arrays.fill(chars, '#'));
        Arrays.stream(mapCopy).forEach(chars -> Arrays.stream(chars).forEach(chars1 -> Arrays.fill(chars1, -1)));

        processMap(strings);
        encodePortals(strings);

        if (portals.size() != tempPortals.size() - 2) {
            throw new RuntimeException("Portal Mismatch");
        }

        fill();
        draw();

        XYZ_Point end = tempPortals.get("ZZ");
        System.out.println(mapCopy[0][end.y][end.x]);
    }

    private static void draw() {
        for (int row = 0; row < ROWS; row++) {
            for (int depth = 0; depth < HEIGHT; depth++) {
                for (int col = 0; col < COLS; col++) {
                    int val = mapCopy[depth][row][col];
                    if (val == -1) {
                        System.out.print("#");
                    } else if (val != Integer.MAX_VALUE) {
                        System.out.print(val % 10);
                    } else {
                        System.out.print(" ");
                    }
                }
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("------------------------");
    }

    private static void processMap(List<String> strings) {
        for (int row = 0; row < strings.size(); row++) {
            for (int col = 0; col < strings.get(row).length(); col++) {
                map[row][col] = strings.get(row).charAt(col);
                for (int depth = 0; depth < HEIGHT; depth++) {
                    if (map[row][col] == '#') {
                        mapCopy[depth][row][col] = -1;
                    } else if (map[row][col] == '.') {
                        mapCopy[depth][row][col] = Integer.MAX_VALUE;
                    }
                }
            }
        }
    }

    private static void encodePortals(List<String> strings) {
        for (int row = 0; row < strings.size(); row++) {
            for (int col = 0; col < strings.get(row).length(); col++) {
                if ((row == OUTER_TOP || row == INNER_BOTTOM) && map[row][col] == '.' && map[row - 1][col] > 64) {
                    matchPortal(new XYZ_Point(col, row, 0, 0), map[row - 2][col] + "" + map[row - 1][col]);
                }
                if ((row == INNER_TOP || row == OUTER_BOTTOM) && map[row][col] == '.' && map[row + 1][col] > 64) {
                    matchPortal(new XYZ_Point(col, row, 0, 0), map[row + 1][col] + "" + map[row + 2][col]);
                }
                if ((col == OUTER_LEFT || col == INNER_RIGHT) && map[row][col] == '.' && map[row][col - 1] > 64) {
                    matchPortal(new XYZ_Point(col, row, 0, 0), map[row][col - 2] + "" + map[row][col - 1]);
                }
                if ((col == INNER_LEFT || col == OUTER_RIGHT) && map[row][col] == '.' && map[row][col + 1] > 64) {
                    matchPortal(new XYZ_Point(col, row, 0, 0), map[row][col + 1] + "" + map[row][col + 2]);
                }
            }
        }

        portals.forEach((s, portal) -> newArrayList(portal.point1, portal.point2).forEach(xyz_point -> {
            if (isOuterPortal(xyz_point)) {
                mapCopy[0][xyz_point.y][xyz_point.x] = -1;
            } else if (isInnerPortal(xyz_point)) {
                mapCopy[HEIGHT - 1][xyz_point.y][xyz_point.x] = -1;
            }
        }));
    }

    private static void matchPortal(XYZ_Point value, String key) {
        if (tempPortals.containsKey(key)) {
            portals.put(key, new Portal(tempPortals.get(key), value));
        }
        tempPortals.put(key, value);
    }

    private static void fill() {
        List<XYZ_Point> nextPoints = newArrayList();
        XYZ_Point start = tempPortals.get("AA");
        start.steps = 0;
        nextPoints.add(start);
        mapCopy[0][start.y][start.x] = start.steps;

        while (!nextPoints.isEmpty()) {
            List<XYZ_Point> toAdd = newArrayList();
            List<XYZ_Point> toRemove = newArrayList();
            nextPoints.forEach(point -> {
                toRemove.add(point);
                mapCopy[point.z][point.y][point.x] = point.steps;

                if (point == tempPortals.get("ZZ")) {
                    return;
                }

                XYZ_Point top = new XYZ_Point(point.x, point.y - 1, point.z, point.steps + 1);
                XYZ_Point bottom = new XYZ_Point(point.x, point.y + 1, point.z, point.steps + 1);
                XYZ_Point left = new XYZ_Point(point.x - 1, point.y, point.z, point.steps + 1);
                XYZ_Point right = new XYZ_Point(point.x + 1, point.y, point.z, point.steps + 1);

                addWithPortal(toAdd, point, top);
                addWithPortal(toAdd, point, bottom);
                addWithPortal(toAdd, point, left);
                addWithPortal(toAdd, point, right);
            });

            nextPoints.addAll(toAdd);
            nextPoints.removeAll(toRemove);
        }
    }

    private static void addWithPortal(List<XYZ_Point> toAdd, XYZ_Point current, XYZ_Point newPoint) {
        Optional<Map.Entry<String, Portal>> optionalPortal = portals.entrySet().stream().filter(entry -> entry.getValue().contains(newPoint)).findFirst();
        if (optionalPortal.isPresent() && mapCopy[newPoint.z][newPoint.y][newPoint.x] > current.steps) {
            XYZ_Point opposite = portals.get(optionalPortal.get().getKey()).getOpposite(newPoint);
            XYZ_Point oppositeToAdd = new XYZ_Point(opposite.x, opposite.y, opposite.z, newPoint.steps + 1);
            if (isInnerPortal(newPoint) && newPoint.z < HEIGHT - 1 && mapCopy[newPoint.z + 1][oppositeToAdd.y][oppositeToAdd.x] > current.steps) {
                oppositeToAdd.z = newPoint.z + 1;
            } else if (isOuterPortal(newPoint) && newPoint.z > 1 && mapCopy[newPoint.z - 1][oppositeToAdd.y][oppositeToAdd.x] > current.steps) {
                oppositeToAdd.z = newPoint.z - 1;
            }
            toAdd.add(oppositeToAdd);
        } else if (mapCopy[newPoint.z][newPoint.y][newPoint.x] > current.steps) {
            toAdd.add(newPoint);
        }
    }

    private static boolean isInnerPortal(XYZ_Point newPoint) {
        return newPoint.x == INNER_LEFT && newPoint.y > INNER_TOP && newPoint.y < INNER_BOTTOM || //left edge
                newPoint.x == INNER_RIGHT && newPoint.y > INNER_TOP && newPoint.y < INNER_BOTTOM || //right edge
                newPoint.y == INNER_TOP && newPoint.x > INNER_LEFT && newPoint.x < INNER_RIGHT || //top edge
                newPoint.y == INNER_BOTTOM && newPoint.x > INNER_LEFT && newPoint.x < INNER_RIGHT; //bottom edge
    }

    private static boolean isOuterPortal(XYZ_Point newPoint) {
        return newPoint.x == OUTER_LEFT && newPoint.y > OUTER_TOP && newPoint.y < OUTER_BOTTOM || //left edge
                newPoint.x == OUTER_RIGHT && newPoint.y > OUTER_TOP && newPoint.y < OUTER_BOTTOM || //right edge
                newPoint.y == OUTER_TOP && newPoint.x > OUTER_LEFT && newPoint.x < OUTER_RIGHT || //top edge
                newPoint.y == OUTER_BOTTOM && newPoint.x > OUTER_LEFT && newPoint.x < OUTER_RIGHT; //bottom edge
    }
}

class Portal {
    XYZ_Point point1;
    XYZ_Point point2;

    public Portal(XYZ_Point point1, XYZ_Point point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public XYZ_Point getOpposite(XYZ_Point p) {
        return p.x == point2.x && p.y == point2.y ? point1 : point2;
    }

    public boolean contains(XYZ_Point p) {
        return (p.x == point1.x && p.y == point1.y) || (p.x == point2.x && p.y == point2.y);
    }
}

@ToString
@AllArgsConstructor
class XYZ_Point {
    int x;
    int y;
    int z;
    int steps;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XYZ_Point point = (XYZ_Point) o;
        return x == point.x && y == point.y && z == point.z;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y, z);
    }
}
