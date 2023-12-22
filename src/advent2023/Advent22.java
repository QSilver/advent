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

    static Map<Brick, Set<Brick>> brickRestsOn = newHashMap();
    static Map<Brick, Set<Brick>> brickIsSupporting = newHashMap();
    static String brickIDs = "ABCDEFG";
    static int id = 0;

    public Long runP1(String file, boolean withRandomIds) {
        List<Brick> bricks = getBricks(file, withRandomIds);

        bricks.forEach(brick -> fall(brick, bricks));

        // can disintegrate all bricks that don't have anything above
        Set<Brick> canDisintegrate = bricks.stream()
                .filter(brick -> !brickIsSupporting.containsKey(brick))
                .collect(Collectors.toSet());

        // can disintegrate a bricks in the case where every other brick that depends on it has at least 1 other support
        brickIsSupporting.forEach((brick, dependents) -> {
            if (dependents.stream().allMatch(Brick::hasMoreThanOneSupport)) {
                canDisintegrate.add(brick);
            }
        });

        return (long) canDisintegrate.size();
    }

    public Long runP2(String file, boolean withRandomIds) {
        List<Brick> bricks = getBricks(file, withRandomIds);

        bricks.forEach(brick -> fall(brick, bricks));

        // can disintegrate all bricks that don't have anything above
        Set<Brick> canDisintegrate = bricks.stream()
                .filter(brick -> !brickIsSupporting.containsKey(brick))
                .collect(Collectors.toSet());

        // can disintegrate a bricks in the case where every other brick that rests on it has at least 1 other support
        brickIsSupporting.forEach((brick, aboveList) -> {
            if (aboveList.stream().allMatch(Brick::hasMoreThanOneSupport)) {
                canDisintegrate.add(brick);
            }
        });

        // bricks that will make something fall
        Set<Brick> worthDisintegrating = bricks.stream()
                .filter(brick -> !canDisintegrate.contains(brick))
                .collect(Collectors.toSet());

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
            Set<Brick> dependOnBrick = brickIsSupporting.get(current);

            if (dependOnBrick != null) {
                // for each brick, get the other bricks that depend on it
                // for each of those dependents, check which ones rest on something else that won't fall
                Set<Brick> toAdd = dependOnBrick.stream()
                        .filter(dependent -> isRestingOnAnyOtherBrickThatWillNotFall(dependent, willFall))
                        .collect(Collectors.toSet());

                willFall.addAll(toAdd);
                toProcess.addAll(toAdd);
            }
        }

        // remove original brick since that doesn't fall, but gets disintegrated
        return willFall.size() - 1;
    }

    private boolean isRestingOnAnyOtherBrickThatWillNotFall(Brick dependent, Set<Brick> willFall) {
        Set<Brick> bricksThisOneRestsOn = newHashSet(brickRestsOn.get(dependent));
        bricksThisOneRestsOn.removeAll(willFall);
        return bricksThisOneRestsOn.isEmpty();
    }

    void fall(Brick current, List<Brick> brickList) {
        Set<Point> surface = current.getSurface();

        int currentWillFallToZ = 0;
        Set<Brick> willFallOn = newHashSet();

        for (Brick other : brickList) {
            // only look at bricks that are under this one since you can't fall down on a brick above you
            if (current == other) {
                break;
            }

            // if points in the projection of the brick overlap, then the falling brick will touch another
            if (!disjoint(surface, other.getSurface())) {
                int heightOfTower = other.getMaxZ() + 1;

                // we've discovered an intersecting brick higher than the previously found one
                // forget about that and start again from the new highest point
                if (heightOfTower > currentWillFallToZ) {
                    willFallOn.clear();
                    willFallOn.add(other);
                    currentWillFallToZ = heightOfTower;
                } else if (heightOfTower == currentWillFallToZ) {
                    // there is more than one brick at the top level
                    // currently falling one will rest on more than one brick
                    willFallOn.add(other);
                }
            }
        }

        current.fallDownTo(currentWillFallToZ);

        brickRestsOn.computeIfAbsent(current, b -> newHashSet()).addAll(willFallOn);
        willFallOn.forEach(supporting -> brickIsSupporting.computeIfAbsent(supporting, s -> newHashSet()).add(current));
    }

    private static List<Brick> getBricks(String file, boolean withRandomIds) {
        return fileStream(file)
                .map(line -> new Brick(line, withRandomIds))
                // sort bricks by Z-index to ensure lower ones fall first
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

        public boolean hasMoreThanOneSupport() {
            return brickRestsOn.get(this).size() > 1;
        }

        // calculate the projection of the brick looking top-down
        // this makes it easy to see what bricks would intersect by falling
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

        public void fallDownTo(int fallDownToZ) {
            int deltaZ = getMinZ() - fallDownToZ;
            points = points.stream()
                    .map(point3D -> new Point3D(point3D.x(), point3D.y(), point3D.z() - deltaZ))
                    .collect(Collectors.toSet());
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
