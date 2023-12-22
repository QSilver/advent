package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util.Point;
import util.Util.Point3D;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Queues.newArrayDeque;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.parseInt;
import static java.util.Collections.disjoint;
import static util.Util.fileStream;

// 84803 - too high

@Slf4j
public class Advent22 {
    // https://adventofcode.com/2023/day/22

    Map<Brick, Set<Brick>> restsOn = newHashMap();
    Map<Brick, Set<Brick>> isSupporting = newHashMap();
    static String brickIDs = "ABCDEFG";
    static int id = 0;

    public Long runP1(String file, boolean withRandomIds) {
        List<Brick> bricks = getBricks(file, withRandomIds);

        bricks.forEach(brick -> fall(brick, bricks));

        Set<Brick> canDisintegrate = bricks.stream().filter(brick -> !isSupporting.containsKey(brick)).collect(Collectors.toSet());
        isSupporting.forEach((brick, aboveList) -> {
            if (aboveList.stream().allMatch(brickAbove -> restsOn.get(brickAbove).size() > 1)) {
                canDisintegrate.add(brick);
            }
        });

        return (long) canDisintegrate.size();
    }

    public Long runP2(String file, boolean withRandomIds) {
        List<Brick> bricks = getBricks(file, withRandomIds);

        bricks.forEach(brick -> fall(brick, bricks));

        Set<Brick> canDisintegrate = bricks.stream().filter(brick -> !isSupporting.containsKey(brick)).collect(Collectors.toSet());
        isSupporting.forEach((brick, aboveList) -> {
            if (aboveList.stream().allMatch(brickAbove -> restsOn.get(brickAbove).size() > 1)) {
                canDisintegrate.add(brick);
            }
        });

        List<Brick> worthDisintegrating = bricks.stream().filter(brick -> !canDisintegrate.contains(brick)).toList();

        return (long) worthDisintegrating.stream()
                .mapToInt(this::getAllBricksRestingOn)
                .sum();
    }

    private int getAllBricksRestingOn(Brick brick) {
        Set<Brick> willFall = newHashSet();
        Queue<Brick> toProcess = newArrayDeque();
        willFall.add(brick);
        toProcess.add(brick);

        while (!toProcess.isEmpty()) {
            Brick current = toProcess.poll();
            Set<Brick> dependOnBrick = isSupporting.get(current);

            if (dependOnBrick != null) {
                Set<Brick> toAdd = dependOnBrick.stream()
                        .filter(testWillFall -> isItRestingOnAnyOtherBrick(testWillFall, willFall))
                        .collect(Collectors.toSet());

                willFall.addAll(toAdd);
                toProcess.addAll(toAdd);
            }
        }

        return willFall.size() - 1;
    }

    private boolean isItRestingOnAnyOtherBrick(Brick testWillFall, Set<Brick> willFall) {
        Set<Brick> bricksThisOneRestsOn = newHashSet(restsOn.get(testWillFall));
        bricksThisOneRestsOn.removeAll(willFall);
        return bricksThisOneRestsOn.isEmpty();
    }

    void fall(Brick brick, List<Brick> brickList) {
        Set<Point> surface = brick.getSurface();

        int minZ = 0;
        Set<Brick> willFallOn = newHashSet();
        for (Brick other : brickList) {
            if (brick == other) {
                break;
            }

            Set<Point> otherSurface = other.getSurface();
            boolean intersects = !disjoint(surface, otherSurface);
            if (intersects) {
                int maxZ = other.getMaxZ() + 1;
                if (maxZ > minZ) {
                    willFallOn.clear();
                    willFallOn.add(other);
                    minZ = maxZ;
                } else if (maxZ == minZ) {
                    willFallOn.add(other); // rests on more than 1 brick
                }
            }
        }

        brick.fall(minZ);

        restsOn.computeIfAbsent(brick, b -> newHashSet()).addAll(willFallOn);
        willFallOn.forEach(supporting -> isSupporting.computeIfAbsent(supporting, s -> newHashSet()).add(brick));
    }

    private static List<Brick> getBricks(String file, boolean withRandomIds) {
        return fileStream(file)
                .map(line -> new Brick(line, withRandomIds))
                .sorted(Comparator.comparingInt(Brick::getMinZ))
                .collect(Collectors.toList());
    }

    static class Brick {
        String uuid;
        public Set<Point3D> points = newHashSet();

        public Brick(String input, boolean withRandomIds) {
            if (withRandomIds) {
                this.uuid = UUID.randomUUID().toString();
            } else {
                this.uuid = brickIDs.charAt(id++) + "";
            }

            String[] split = input.split("~");
            String[] from = split[0].split(",");
            String[] to = split[1].split(",");

            for (int x = parseInt(from[0]); x <= parseInt(to[0]); x++) {
                for (int y = parseInt(from[1]); y <= parseInt(to[1]); y++) {
                    for (int z = parseInt(from[2]); z <= parseInt(to[2]); z++) {
                        points.add(new Point3D(x, y, z));
                    }
                }
            }
        }

        public Set<Point> getSurface() {
            return points.stream()
                    .map(point3D -> new Point(point3D.x(), point3D.y(), 0))
                    .collect(Collectors.toSet());
        }

        public int getMaxZ() {
            return points.stream()
                    .mapToInt(point3D -> (int) point3D.z())
                    .max().orElseThrow();
        }

        public int getMinZ() {
            return points.stream()
                    .mapToInt(point3D -> (int) point3D.z())
                    .min().orElseThrow();
        }

        public void fall(int minZ) {
            int deltaZ = getMinZ() - minZ;
            points = points.stream()
                    .map(point3D -> new Point3D(point3D.x(), point3D.y(), point3D.z() - deltaZ))
                    .collect(Collectors.toSet());
        }

        @Override
        public String toString() {
            return "Brick{" +
                    "uuid='" + uuid + '\'' +
                    ", points=" + points +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Brick brick = (Brick) o;

            return uuid.equals(brick.uuid);
        }

        @Override
        public int hashCode() {
            return uuid.hashCode();
        }
    }
}
