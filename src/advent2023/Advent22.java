package advent2023;

import lombok.extern.slf4j.Slf4j;
import util.Point2D;
import util.Util.Point3D;

import java.util.List;
import java.util.Queue;
import java.util.Set;

import static com.google.common.collect.Queues.newArrayDeque;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.parseInt;
import static java.util.Collections.disjoint;
import static java.util.Comparator.comparingInt;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent22 {
    // https://adventofcode.com/2023/day/22
    static final String BRICK_IDS = "ABCDEFG";
    static int id = 0;

    public Long runP1(String file, boolean withRandomIds) {
        return (long) processBricks(file, withRandomIds).canDisintegrate.size();
    }

    public Long runP2(String file, boolean withRandomIds) {
        return (long) processBricks(file, withRandomIds).zSortedBricks.stream()
                .mapToInt(Brick::getAllBricksRestingOnThisThatWouldFall)
                .sum();
    }

    private Result processBricks(String file, boolean withRandomIds) {
        List<Brick> bricks = getSnapshotSortedByZ(file, withRandomIds);
        bricks.forEach(brick -> brick.fall(bricks));

        // can disintegrate all bricks that don't have anything above
        Set<Brick> canDisintegrate = bricks.stream()
                .filter(brick -> brick.above.isEmpty())
                .collect(toSet());

        // can disintegrate a brick in the case where every other brick directly above has at least 1 other support
        bricks.forEach(brick -> {
            if (brick.above.stream().allMatch(Brick::hasMoreThanOneSupport)) {
                canDisintegrate.add(brick);
            }
        });

        // consider only bricks that would cause falling
        bricks.removeAll(canDisintegrate);
        return new Result(canDisintegrate, bricks);
    }

    private static List<Brick> getSnapshotSortedByZ(String file, boolean withRandomIds) {
        return fileStream(file)
                .map(line -> new Brick(line, withRandomIds))
                // sort bricks by Z-index to ensure lower ones fall first
                .sorted(comparingInt(Brick::getMinZ))
                .collect(toList());
    }

    static class Brick {
        String uuid;
        Set<Point3D> points = newHashSet();
        Set<Brick> above = newHashSet();
        Set<Brick> below = newHashSet();
        Set<Point2D> surface = newHashSet();

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
                    .map(point3D -> new Point2D(point3D.x(), point3D.y()))
                    .collect(toSet());
        }

        void generateId(boolean withRandomIds) {
            if (withRandomIds) {
                this.uuid = randomUUID().toString();
            } else {
                this.uuid = BRICK_IDS.charAt(id++ % BRICK_IDS.length()) + "";
            }
        }

        boolean hasMoreThanOneSupport() {
            return below.size() > 1;
        }

        int getAllBricksRestingOnThisThatWouldFall() {
            Set<Brick> willFall = newHashSet(this);

            Queue<Brick> toProcess = newArrayDeque();
            toProcess.add(this);

            while (!toProcess.isEmpty()) {
                Brick current = toProcess.poll();
                // get resting on current brick, check which ones rest on something else that won't fall
                current.above.stream()
                        .filter(dependent -> dependent.isRestingOnAnyOtherBrickThatWillNotFall(willFall))
                        .forEach(dependent -> {
                            willFall.add(dependent);
                            toProcess.add(dependent);
                        });
            }

            // remove original brick since that doesn't fall, but gets disintegrated
            return willFall.size() - 1;
        }

        boolean isRestingOnAnyOtherBrickThatWillNotFall(Set<Brick> falling) {
            Set<Brick> bricksThisOneRestsOn = newHashSet(below);
            bricksThisOneRestsOn.removeAll(falling);
            return bricksThisOneRestsOn.isEmpty();
        }

        void fall(List<Brick> brickList) {
            int currentWillFallToZ = 0;
            Set<Brick> willFallOn = newHashSet();

            for (Brick other : brickList) {
                // only look at bricks that are under this one since you can't fall down on a brick above you
                if (this == other) {
                    break;
                }

                // if points in the projection of the brick overlap, then the falling brick will touch another
                if (!disjoint(surface, other.surface)) {
                    int heightOfTower = other.getMaxZ() + 1;
                    if (heightOfTower > currentWillFallToZ) {
                        // we've discovered an intersecting brick higher than the previously found one
                        // forget about the old one and start again from the new highest point
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

            int deltaZ = getMinZ() - currentWillFallToZ;
            points = points.stream()
                    .map(point3D -> new Point3D(point3D.x(), point3D.y(), point3D.z() - deltaZ))
                    .collect(toSet());

            below.addAll(willFallOn);
            willFallOn.forEach(supporting -> supporting.above.add(this));
        }

        int getMaxZ() {
            return points.stream()
                    .mapToInt(point3D -> (int) point3D.z())
                    .max().orElseThrow();
        }

        int getMinZ() {
            return points.stream()
                    .mapToInt(point3D -> (int) point3D.z())
                    .min().orElseThrow();
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
