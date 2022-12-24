package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static advent2022.Advent24.Direction.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent24 {
    static Set<Point> map = newHashSet();
    static Set<Blizzard> blizzards = newHashSet();
    static int maxDown, maxAcross = 0;

    public static void main(String[] args) {
        List<String> mapLines = Util.fileStream("advent2022/advent24").collect(Collectors.toList());
        parseMapLines(mapLines);

        Point start = map.stream().filter(point -> point.down == 0).findFirst().get();
        Point end = map.stream().filter(point -> point.down == mapLines.size() - 1).findFirst().get();

        findPathBetween(start, end);
        findPathBetween(end, start);
        findPathBetween(start, end);
    }

    private static void findPathBetween(Point start, Point end) {
        Path starting = new Path();
        starting.route.add(start);
        Set<Path> paths = newHashSet(starting);
        AtomicReference<Path> exit = new AtomicReference<>();
        AtomicBoolean foundExit = new AtomicBoolean(false);
        do {
            blizzards.forEach(Blizzard::move);
            Set<Path> newPaths = newHashSet();
            paths.forEach(path -> {
                Point current = path.route.get(path.route.size() - 1);
                if (current.equals(end)) {
                    foundExit.set(true);
                    exit.set(path);
                }
                current.getAdjacentOrWait()
                        .forEach(point -> newPaths.add(path.append(point)));
            });
            paths = newPaths;
        } while (!foundExit.get());
        log.info("Shortest Path: {}", exit.get().route.size() - 1);
    }

    static void display() {
        StringBuffer sb = new StringBuffer("\n");
        for (int d = 0; d < maxDown; d++) {
            for (int a = 0; a < maxAcross; a++) {
                Point current = new Point(d, a);
                List<Blizzard> blizzards = Advent24.blizzards.stream().filter(blizzard -> blizzard.position.equals(current)).collect(Collectors.toList());
                if (blizzards.size() > 0) {
                    if (blizzards.size() == 1) {
                        switch (blizzards.get(0).direction) {
                            case UP -> sb.append("^");
                            case DOWN -> sb.append("v");
                            case LEFT -> sb.append("<");
                            case RIGHT -> sb.append(">");
                        }
                    } else {
                        sb.append(blizzards.size());
                    }
                } else if (map.contains(current)) {
                    sb.append(".");
                } else {
                    sb.append("#");
                }
            }
            sb.append("\n");
        }
        log.info("{}", sb);
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

    static class Path {
        List<Point> route = newArrayList();

        Path append(Point point) {
            Path path = new Path();
            path.route.addAll(route);
            path.route.add(point);
            return path;
        }

        @Override
        public String toString() {
            return route + "";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Path other = (Path) o;
            int size = route.size() - 1;
            int otherSize = other.route.size() - 1;
            return size == otherSize && route.get(size).equals(other.route.get(otherSize));
        }

        @Override
        public int hashCode() {
            int size = route.size() - 1;
            return Objects.hash(route.get(size), size);
        }
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

        Point move() {
            Point newPosition;
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
                default -> {
                    log.error("Invalid Blizzard Direction");
                    return null;
                }
            }
            position = newPosition;
            return newPosition;
        }

        @Override
        public String toString() {
            return position + " " + direction;
        }
    }

    record Point(int down, int across) {
        Set<Point> getAdjacentOrWait() {
            return newHashSet(
                    new Point(down - 1, across),
                    new Point(down + 1, across),
                    new Point(down, across - 1),
                    new Point(down, across + 1),
                    new Point(down, across))
                    .stream().filter(point -> map.contains(point))
                    .filter(point -> blizzards.stream().noneMatch(blizzard -> blizzard.position.equals(point)))
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