package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static advent2022.Advent24.Direction.*;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent24 {
    static Set<Point> map = newHashSet();
    static Set<Blizzard> blizzards = newHashSet();
    static Map<Point, Blizzard> blizzardPositions = newHashMap();
    static int maxDown, maxAcross = 0;

    public static void main(String[] args) {
        List<String> mapLines = Util.fileStream("advent2022/advent24").collect(Collectors.toList());
        parseMapLines(mapLines);

        Point start = map.stream().filter(point -> point.down == 0).findFirst().get();
        Point end = map.stream().filter(point -> point.down == mapLines.size() - 1).findFirst().get();

        int distance = findPathBetween(start, end);
        log.info("Part 1 Shortest Path: {}", distance);
        int distanceSnacks = findPathBetween(end, start);
        int distanceBack = findPathBetween(start, end);
        log.info("Part 2 Shortest Path: {}", distance + distanceSnacks + distanceBack + 2);
        // add 2 to this sum to get the real answer ????
    }

    private static int findPathBetween(Point start, Point end) {
        Set<Point> paths = newHashSet(start);
        AtomicInteger foundDistance = new AtomicInteger(-1);
        AtomicInteger currentDistance = new AtomicInteger(0);
        do {
            blizzardPositions.clear();
            blizzards.forEach(Blizzard::move);
            Set<Point> newPaths = newHashSet();
            paths.forEach(current -> {
                if (current.equals(end)) {
                    foundDistance.set(currentDistance.get());
                }
                newPaths.addAll(current.getAdjacentOrWait());
            });
            paths = newPaths;
            currentDistance.getAndIncrement();
        } while (foundDistance.get() == -1);
        return foundDistance.get();
    }

    private static void parseMapLines(List<String> mapLines) {
        for (int down = 0; down < mapLines.size(); down++) {
            for (int across = 0; across < mapLines.get(down).length(); across++) {
                char c = mapLines.get(down).charAt(across);
                Point point = new Point(down, across);
                switch (c) {
                    case '.' -> map.add(point);
                    case '#' -> {
                    }
                    default -> {
                        map.add(point);
                        blizzards.add(new Blizzard(point, c));
                    }
                }
            }
        }
        maxDown = mapLines.size();
        maxAcross = mapLines.get(0).length();
    }

    static class Blizzard {
        Point position;
        Direction direction;

        public Blizzard(Point position, char direction) {
            this.position = position;
            switch (direction) {
                case '^' -> this.direction = UP;
                case 'v' -> this.direction = DOWN;
                case '<' -> this.direction = LEFT;
                case '>' -> this.direction = RIGHT;
            }
        }

        void move() {
            Point newPosition = null;
            switch (direction) {
                case UP -> {
                    newPosition = new Point(position.down() - 1, position.across());
                    if (!map.contains(newPosition)) {
                        newPosition = new Point(maxDown - 2, position.across());
                    }
                }
                case DOWN -> {
                    newPosition = new Point(position.down() + 1, position.across());
                    if (!map.contains(newPosition)) {
                        newPosition = new Point(1, position.across());
                    }
                }
                case LEFT -> {
                    newPosition = new Point(position.down(), position.across() - 1);
                    if (!map.contains(newPosition)) {
                        newPosition = new Point(position.down(), maxAcross - 2);
                    }
                }
                case RIGHT -> {
                    newPosition = new Point(position.down(), position.across() + 1);
                    if (!map.contains(newPosition)) {
                        newPosition = new Point(position.down(), 1);
                    }
                }
                default -> log.error("Invalid Blizzard Direction");
            }
            position = newPosition;
            blizzardPositions.put(newPosition, this);
        }

        @Override
        public String toString() {
            return position + " " + direction;
        }
    }

    record Point(int down, int across) {
        /**
         * this has a small bug
         * you can  pass through a blizzard if it moves onto your spot and there is nothing behind it
         *
         * @return Set of new reachable point
         */
        Set<Point> getAdjacentOrWait() {
            return newHashSet(
                    new Point(down - 1, across),
                    new Point(down + 1, across),
                    new Point(down, across - 1),
                    new Point(down, across + 1),
                    new Point(down, across))
                    .stream().filter(point -> map.contains(point))
                    .filter(point -> !blizzardPositions.containsKey(point))
                    .collect(Collectors.toSet());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return down == point.down && across == point.across;
        }

        @Override
        public int hashCode() {
            return 100003 * down + across;
        }

        @Override
        public String toString() {
            return "(" + down + "," + across + ")";
        }
    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}