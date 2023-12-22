package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Util.Point;
import util.Util.Point3D;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Queues.newArrayDeque;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.parseInt;
import static java.util.Collections.disjoint;
import static java.util.UUID.randomUUID;
import static util.Util.fileStream;

@Slf4j
public class Advent22 {
    // https://adventofcode.com/2023/day/22
    static String brickIDs = "ABCDEFG";
    static int id = 0;

    public Long runP1(String file, boolean withRandomIds) {
        return (long) getCanDisintegrate(file, withRandomIds).canDisintegrate.size();
    }

    public Long runP2(String file, boolean withRandomIds) {
        Result result = getCanDisintegrate(file, withRandomIds);
        return (long) result.zSortedBricks.stream()
                .filter(brick -> !result.canDisintegrate.contains(brick))
                .mapToInt(this::getAllBricksRestingOn)
                .sum();
    }

    private Result getCanDisintegrate(String file, boolean withRandomIds) {
        List<Brick> bricks = getSnapshotSortedByZ(file, withRandomIds);
        bricks.forEach(brick -> fall(brick, bricks));

        // can disintegrate all bricks that don't have anything above
        Set<Brick> canDisintegrate = bricks.stream()
                .filter(brick -> brick.above.isEmpty())
                .collect(Collectors.toSet());

        // can disintegrate a bricks in the case where every other brick that depends on it has at least 1 other support
        bricks.forEach(brick -> {
            if (brick.above.stream().allMatch(Brick::hasMoreThanOneSupport)) {
                canDisintegrate.add(brick);
            }
        });
        return new Result(canDisintegrate, bricks);
    }

    private int getAllBricksRestingOn(Brick brick) {
        Set<Brick> willFall = newHashSet(brick);

        Queue<Brick> toProcess = newArrayDeque();
        toProcess.add(brick);

        while (!toProcess.isEmpty()) {
            Brick current = toProcess.poll();
            // get resting on current brick, check which ones rest on something else that won't fall
            for (Brick dependent : current.above) {
                if (isRestingOnAnyOtherBrickThatWillNotFall(dependent, willFall)) {
                    willFall.add(dependent);
                    toProcess.add(dependent);
                }
            }
        }

        // remove original brick since that doesn't fall, but gets disintegrated
        return willFall.size() - 1;
    }

    private boolean isRestingOnAnyOtherBrickThatWillNotFall(Brick dependent, Set<Brick> willFall) {
        Set<Brick> bricksThisOneRestsOn = newHashSet(dependent.below);
        bricksThisOneRestsOn.removeAll(willFall);
        return bricksThisOneRestsOn.isEmpty();
    }

    void fall(Brick current, List<Brick> brickList) {

        int currentWillFallToZ = 0;
        Set<Brick> willFallOn = newHashSet();

        for (Brick other : brickList) {
            // only look at bricks that are under this one since you can't fall down on a brick above you
            if (current == other) {
                break;
            }

            // if points in the projection of the brick overlap, then the falling brick will touch another
            if (!disjoint(current.surface, other.surface)) {
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

        current.below.addAll(willFallOn);
        willFallOn.forEach(supporting -> supporting.above.add(current));
    }

    private static List<Brick> getSnapshotSortedByZ(String file, boolean withRandomIds) {
        return fileStream(file)
                .map(line -> new Brick(line, withRandomIds))
                // sort bricks by Z-index to ensure lower ones fall first
                .sorted(Comparator.comparingInt(Brick::getMinZ))
                .collect(Collectors.toList());
    }

    static class Brick {
        String uuid;
        Set<Point3D> points = newHashSet();
        Set<Brick> above = newHashSet();
        Set<Brick> below = newHashSet();
        Set<Point> surface = newHashSet();

        public Brick(String input, boolean withRandomIds) {
            generateId(withRandomIds);

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

            // calculate the projection of the brick looking top-down
            // this makes it easy to see what bricks would intersect by falling
            surface = points.stream()
                    .map(point3D -> new Point(point3D.x(), point3D.y(), 0))
                    .collect(Collectors.toSet());
        }

        private void generateId(boolean withRandomIds) {
            if (withRandomIds) {
                this.uuid = randomUUID().toString();
            } else {
                this.uuid = brickIDs.charAt(id++ % brickIDs.length()) + "";
            }
        }

        public boolean hasMoreThanOneSupport() {
            return below.size() > 1;
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
            return uuid.equals(((Brick) o).uuid);
        }

        @Override
        public int hashCode() {
            return uuid.hashCode();
        }
    }

    record Result(Set<Brick> canDisintegrate, List<Brick> zSortedBricks) {

    }
}
