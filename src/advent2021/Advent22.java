package advent2021;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent22 {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        List<Cuboid> cuboids = InputUtils.fileStream("advent2021/advent22")
                                   .map(Cuboid::new)
                                   .collect(Collectors.toList());

        Set<Cuboid> active = newHashSet(cuboids.remove(0));
        for (Cuboid cuboid : cuboids) {
            active = active.stream()
                           .flatMap(other -> cuboid.overlaps(other) ? other.split(cuboid) : Stream.of(other))
                           .filter(splits -> !cuboid.fullyContains(splits))
                           .collect(Collectors.toSet());
            if (cuboid.on) {
                active.add(cuboid);
            }
        }

        long overlap = active.stream()
                             .mapToLong(Cuboid::volume)
                             .sum();
        log.info("OVERLAP_POINTS: {}", overlap);
        log.info("{} ms", System.currentTimeMillis() - start);
    }

    @With
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

        boolean overlaps(Cuboid other) {
            long x_overlap = Math.max(0, Math.min(max_x, other.max_x + 1) - Math.max(min_x, other.min_x - 1));
            long y_overlap = Math.max(0, Math.min(max_y, other.max_y + 1) - Math.max(min_y, other.min_y - 1));
            long z_overlap = Math.max(0, Math.min(max_z, other.max_z + 1) - Math.max(min_z, other.min_z - 1));
            return x_overlap > 0 && y_overlap > 0 && z_overlap > 0;
        }

        boolean fullyContains(Cuboid other) {
            return other.min_x >= min_x && other.max_x <= max_x &&
                    other.min_y >= min_y && other.max_y <= max_y &&
                    other.min_z >= min_z && other.max_z <= max_z;
        }

        Stream<Cuboid> split(Cuboid other) {
            return Stream.of(this)
                         .flatMap(cuboid -> cuboid.splitX(other.min_x))
                         .flatMap(cuboid -> cuboid.splitX(other.max_x))
                         .flatMap(cuboid -> cuboid.splitY(other.min_y))
                         .flatMap(cuboid -> cuboid.splitY(other.max_y))
                         .flatMap(cuboid -> cuboid.splitZ(other.min_z))
                         .flatMap(cuboid -> cuboid.splitZ(other.max_z));
        }

        private Stream<Cuboid> splitX(long x) {
            if (x > min_x && x < max_x) {
                return Stream.of(this.withMin_x(x), this.withMax_x(x));
            } else {
                return Stream.of(this);
            }
        }

        private Stream<Cuboid> splitY(long y) {
            if (y > min_y && y < max_y) {
                return Stream.of(this.withMin_y(y), this.withMax_y(y));
            } else {
                return Stream.of(this);
            }
        }

        private Stream<Cuboid> splitZ(long z) {
            if (z > min_z && z < max_z) {
                return Stream.of(this.withMin_z(z), this.withMax_z(z));
            } else {
                return Stream.of(this);
            }
        }
    }
}