package advent2022;

import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Queues.newArrayDeque;

// 2482 - too low
@Slf4j
public class Advent18 {
    static int exteriorSides;

    public static void main(String[] args) {
        Map<Point3D, Cube> cubeMap = InputUtils.fileStream("advent2022/advent18")
                .map(Cube::new)
                .collect(Collectors.toMap(cube -> cube.coords, cube -> cube));
        part1(cubeMap);
        part2(cubeMap);
    }

    private static void part2(Map<Point3D, Cube> cubeMap) {
        int maxX = cubeMap.keySet().stream().max(Comparator.comparingInt(point3D -> point3D.x)).get().x;
        int maxY = cubeMap.keySet().stream().max(Comparator.comparingInt(point3D -> point3D.y)).get().y;
        int maxZ = cubeMap.keySet().stream().max(Comparator.comparingInt(point3D -> point3D.z)).get().z;
        boolean[][][] visited = new boolean[maxX + 2][maxY + 2][maxZ + 2];
        Queue<Point3D> floodPoints = newArrayDeque();
        floodPoints.add(new Point3D(0, 0, 0));

        while (floodPoints.size() > 0) {
            Queue<Point3D> nextPoints = newArrayDeque();
            for (Point3D source : floodPoints) {
                nextPoints.addAll(flood(source, visited, cubeMap));
            }
            floodPoints = nextPoints;
        }
        log.info("Exterior Surface: {}", exteriorSides);
    }

    private static List<Point3D> flood(Point3D source, boolean[][][] visited, Map<Point3D, Cube> cubeMap) {
        List<Point3D> possible = newArrayList();
        possible.add(new Point3D(source.x - 1, source.y, source.z));
        possible.add(new Point3D(source.x + 1, source.y, source.z));
        possible.add(new Point3D(source.x, source.y - 1, source.z));
        possible.add(new Point3D(source.x, source.y + 1, source.z));
        possible.add(new Point3D(source.x, source.y, source.z - 1));
        possible.add(new Point3D(source.x, source.y, source.z + 1));

        List<Point3D> nextPoints = newArrayList();
        possible.forEach(nextPoint -> {
            // if next point is in bounds
            if (nextPoint.x >= 0 && nextPoint.y >= 0 && nextPoint.z >= 0 && nextPoint.x < visited.length && nextPoint.y < visited[0].length && nextPoint.z < visited[0][0].length) {
                // count touching cubes
                if (cubeMap.containsKey(nextPoint)) {
                    exteriorSides++;
                }

                // if next point is not already visited
                if (!visited[nextPoint.x][nextPoint.y][nextPoint.z]) {
                    // if next point not a cube
                    if (!cubeMap.containsKey(nextPoint)) {
                        nextPoints.add(nextPoint);
                        visited[nextPoint.x][nextPoint.y][nextPoint.z] = true;
                    }
                }
            }
        });
        return nextPoints;
    }

    private static void part1(Map<Point3D, Cube> cubeMap) {
        List<Point3D> openSides = newArrayList();
        cubeMap.forEach((key, value) -> value.sides.forEach(side -> {
            // if no cube exists where side would be
            if (!cubeMap.containsKey(side)) {
                openSides.add(side);
            }
        }));
        log.info("Surface: {}", openSides.size());
    }

    static class Cube {
        Point3D coords;
        List<Point3D> sides = newArrayList();

        public Cube(String input) {
            String[] split = input.split(",");
            int x = Integer.parseInt(split[0]) + 1;
            int y = Integer.parseInt(split[1]) + 1;
            int z = Integer.parseInt(split[2]) + 1;
            Point3D coords = new Point3D(x, y, z);
            this.coords = coords;
            sides.add(new Point3D(coords.x - 1, coords.y, coords.z));
            sides.add(new Point3D(coords.x + 1, coords.y, coords.z));
            sides.add(new Point3D(coords.x, coords.y - 1, coords.z));
            sides.add(new Point3D(coords.x, coords.y + 1, coords.z));
            sides.add(new Point3D(coords.x, coords.y, coords.z - 1));
            sides.add(new Point3D(coords.x, coords.y, coords.z + 1));
        }
    }

    record Point3D(int x, int y, int z) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point3D point3D = (Point3D) o;
            return x == point3D.x && y == point3D.y && z == point3D.z;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(x, y, z);
        }
    }
}
