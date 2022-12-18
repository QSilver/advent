package advent2022;

import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent18 {
    static List<Point3D> openSides = newArrayList();

    public static void main(String[] args) {
        List<Cube> cubes = Util.fileStream("advent2022/advent18")
                .map(Cube::new)
                .collect(Collectors.toList());

        cubes.forEach(cube -> {
            cube.sides.forEach(side -> {
                if (cubes.stream().noneMatch(other -> other.coords.equals(side))) {
                    openSides.add(side);
                }
            });
        });

        log.info("Surface: {}", openSides.size());
    }

    static class Cube {
        Point3D coords;
        List<Point3D> sides = newArrayList();

        public Cube(String input) {
            String[] split = input.split(",");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);
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
