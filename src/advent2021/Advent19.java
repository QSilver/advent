package advent2021;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent19 {
    static final int[][] ORIENTATIONS = {
            {-1, -1, -1},
            {-1, -1, 1},
            {-1, 1, -1},
            {-1, 1, 1},
            {1, -1, -1},
            {1, -1, 1},
            {1, 1, -1},
            {1, 1, 1},
    };

    static final int[] FLIPS = {0, 1, 2, 3, 4, 5};

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        List<String> inputs = Util.fileStream("advent2021/advent19")
                                  .filter(s -> !s.isEmpty())
                                  .collect(Collectors.toList());
        List<Scanner> scanners = getScanners(inputs);
        Scanner source = scanners.remove(0);

        List<Scanner> matchedScanners = newArrayList();
        while (scanners.size() > 0) {
            for (int i = 0; i < scanners.size(); ) {
                Scanner other = scanners.get(i);

                pointScan:
                for (int[] orientation : ORIENTATIONS) {
                    for (int flip : FLIPS) {
                        Set<Point3D> orient = orient(other.points, orientation);
                        Set<Point3D> flipped = flip(orient, flip);

                        for (Point3D origin : source.points) {
                            for (Point3D overlap : flipped) {
                                Set<Point3D> translate = translate(flipped, origin, overlap);
                                Set<Point3D> verify = newHashSet(translate);
                                verify.retainAll(source.points);
                                if (verify.size() >= 12) {
                                    Point3D relativeDelta = origin.add(overlap.orient(ORIENTATIONS[0]));
                                    log.info("Matched Scanner {} delta: {}", other.id, relativeDelta);
                                    List<Point3D> collect = other.points.stream()
                                                                        .map(point3D -> point3D.orient(orientation)
                                                                                               .flip(flip)
                                                                                               .add(relativeDelta))
                                                                        .collect(Collectors.toList());
                                    source.points.addAll(collect);
                                    scanners.remove(other);
                                    other.relativeDelta = relativeDelta;
                                    matchedScanners.add(other);
                                    i--;
                                    break pointScan;
                                }
                            }
                        }
                    }
                }
                i++;
            }
        }

        log.info("P1: {}", source.points.size());

        int maxDistance = 0;
        for (int i = 0; i < matchedScanners.size() - 1; i++) {
            for (int j = i + 1; j < matchedScanners.size(); j++) {
                int dist = manhattan(matchedScanners.get(i), matchedScanners.get(j));
                if (dist > maxDistance) {
                    maxDistance = dist;
                }
            }
        }
        log.info("P2: {}", maxDistance);

        log.info("{}ms", System.currentTimeMillis() - start);
    }

    private static int manhattan(Scanner scanner1, Scanner scanner2) {
        return scanner1.relativeDelta.x - scanner2.relativeDelta.x +
                scanner1.relativeDelta.y - scanner2.relativeDelta.y +
                scanner1.relativeDelta.z - scanner2.relativeDelta.z;
    }

    static Set<Point3D> flip(Set<Point3D> toFlip, int flip) {
        return toFlip.stream()
                     .map(point3D -> point3D.flip(flip))
                     .collect(Collectors.toSet());
    }

    static Set<Point3D> orient(Set<Point3D> toOrient, int[] orientation) {
        return toOrient.stream()
                       .map(point3D -> point3D.orient(orientation))
                       .collect(Collectors.toSet());
    }

    static Set<Point3D> translate(Set<Point3D> toTranslate, Point3D origin, Point3D overlap) {
        int dx = origin.x - overlap.x;
        int dy = origin.y - overlap.y;
        int dz = origin.z - overlap.z;

        return toTranslate.stream()
                          .map(point3D -> point3D.translate(dx, dy, dz))
                          .collect(Collectors.toSet());
    }

    static List<Scanner> getScanners(List<String> inputs) {
        int currentScanner = -1;
        List<Scanner> scanners = newArrayList();
        for (String input : inputs) {
            if (input.contains("---")) {
                String[] split = input.split(" ");
                currentScanner = Integer.parseInt(split[2]);
                scanners.add(new Scanner(currentScanner));
            } else {
                scanners.get(currentScanner).points.add(new Point3D(input));
            }
        }
        return scanners;
    }

    @AllArgsConstructor
    static class Permutation {
        Set<Point3D> points;
        int[] orientation;
        int flip;
    }

    static class Scanner {
        int id;
        Set<Point3D> points;
        Point3D relativeDelta;

        public Scanner(int id) {
            this.id = id;
            this.points = newHashSet();
        }
    }

    @EqualsAndHashCode
    static class Point3D {
        int x;
        int y;
        int z;

        public Point3D(String input) {
            String[] split = input.split(",");
            this.x = Integer.parseInt(split[0]);
            this.y = Integer.parseInt(split[1]);
            this.z = Integer.parseInt(split[2]);
        }

        public Point3D(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point3D flip(int flip) {
            return switch (flip) {
                default -> new Point3D(x, y, z);
                case 1 -> new Point3D(x, z, y);
                case 2 -> new Point3D(y, x, z);
                case 3 -> new Point3D(y, z, x);
                case 4 -> new Point3D(z, x, y);
                case 5 -> new Point3D(z, y, x);
            };
        }

        public Point3D add(Point3D point3D) {
            return translate(point3D.x, point3D.y, point3D.z);
        }

        public Point3D translate(int dx, int dy, int dz) {
            return new Point3D(x + dx, y + dy, z + dz);
        }

        public Point3D orient(int[] o) {
            return new Point3D(x * o[0], y * o[1], z * o[2]);
        }

        @Override
        public String toString() {
            return x + "," + y + "," + z;
        }
    }
}
