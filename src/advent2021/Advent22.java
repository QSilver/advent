package advent2021;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent22 {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        List<Cuboid> cuboids = Util.fileStream("advent2021/advent22")
                                   .map(Cuboid::new)
                                   .toList();

        Set<Cuboid> active = newHashSet();
        for (Cuboid cuboid : cuboids) {
            if (active.isEmpty()) {
                if (cuboid.on) {
                    active = newHashSet(cuboid);
                    continue;
                }
            }

            active = active.stream()
                           .flatMap(other -> other.split(cuboid)
                                                  .stream())
                           .filter(other -> !other.fullyContains(cuboid))
                           .collect(Collectors.toSet());
            if (cuboid.on) {
                active.add(cuboid);
            }
            log.info("Currently active {}", active.size());
        }

        long overlap = active.stream()
                             .mapToLong(Cuboid::volume)
                             .sum();
        log.info("OVERLAP_POINTS: {}", overlap);
        log.info("{} ms", System.currentTimeMillis() - start);
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    static class Cuboid {
        boolean on;
        long min_x;
        long max_x;
        long min_y;
        long max_y;
        long min_z;
        long max_z;

        long volume() {
            return (max_x - min_x) * (max_y - min_y) * (max_z - min_z);
        }

        public Cuboid(String line) {
            String[] split = line.split(" ");
            this.on = split[0].equals("on");
            String[] pos = split[1].split(",");

            String[] x = pos[0].split("=")[1].split("\\.\\.");
            this.min_x = Integer.parseInt(x[0]);
            this.max_x = Integer.parseInt(x[1]) + 1;
            String[] y = pos[1].split("=")[1].split("\\.\\.");
            this.min_y = Integer.parseInt(y[0]);
            this.max_y = Integer.parseInt(y[1]) + 1;
            String[] z = pos[2].split("=")[1].split("\\.\\.");
            this.min_z = Integer.parseInt(z[0]);
            this.max_z = Integer.parseInt(z[1]) + 1;
        }

        boolean fullyContains(Cuboid other) {
            return other.min_x >= min_x &&
                    other.max_x <= max_x &&
                    other.min_y >= min_y &&
                    other.max_y <= max_y &&
                    other.min_z >= min_z &&
                    other.max_z <= max_z;
        }

        Set<Cuboid> split(Cuboid other) {
            Set<Cuboid> splits = newHashSet(this);
            splits.addAll(splitX(other.min_x));
            splits.addAll(splitX(other.max_x));
            splits.addAll(splitY(other.min_y));
            splits.addAll(splitY(other.max_y));
            splits.addAll(splitZ(other.min_z));
            splits.addAll(splitZ(other.max_z));
            return splits;
        }

        Set<Cuboid> splitX(long x) {
            Set<Cuboid> splits = newHashSet(this);
            if (x > min_x && x < max_x) {
                splits.add(new Cuboid(on, x, max_x, min_y, max_y, min_z, max_z));
                splits.add(new Cuboid(on, min_x, x, min_y, max_y, min_z, max_z));
            }
            return splits;
        }

        Set<Cuboid> splitY(long y) {
            Set<Cuboid> splits = newHashSet(this);
            if (y > min_y && y < max_y) {
                splits.add(new Cuboid(on, min_x, max_x, y, max_y, min_z, max_z));
                splits.add(new Cuboid(on, min_x, max_x, min_y, y, min_z, max_z));
            }
            return splits;
        }

        Set<Cuboid> splitZ(long z) {
            Set<Cuboid> splits = newHashSet(this);
            if (z > min_z && z < max_z) {
                splits.add(new Cuboid(on, min_x, max_x, min_y, max_y, z, max_z));
                splits.add(new Cuboid(on, min_x, max_x, min_y, max_y, min_z, z));
            }
            return splits;
        }

        @Override
        public String toString() {
            return (on ? "on" : "off") + " "
                    + "x=" + min_x + ".." + (max_x - 1) + ","
                    + "y" + min_y + ".." + (max_y - 1) + ","
                    + "z=" + min_z + ".." + (max_z - 1);
        }
    }
}