package advent2023;

import lombok.With;
import lombok.extern.slf4j.Slf4j;
import util.Util2D.Direction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Queues.newArrayDeque;
import static com.google.common.collect.Sets.newHashSet;
import static util.InputUtils.loadCharMatrix;
import static util.Util2D.Direction.*;

@Slf4j
public class Advent23 {
    // https://adventofcode.com/2023/day/23

    Character[][] map;
    List<Integer> pathDistances = newArrayList();

    public Long runP1(String file) {
        parseInput(file);
        getDfsPath(new PointWithDirection(0, 1, 0, DOWN, newHashSet()),
                current -> (current.row() == map.length - 1 && current.col() == map[0].length - 2),
                this::getNeighbours
        );
        return pathDistances.stream().mapToLong(value -> value).max().orElseThrow();
    }

    public Long runP2(String file) {
        List<Point> intersectionList = parseInput(file);

        Point start = new Point(0, 1, 0);
        Point end = new Point(map.length - 1, map[0].length - 2, 0);
        intersectionList.addFirst(start);
        intersectionList.addLast(end);

        Graph graph = new Graph();

        for (Point intersection : intersectionList) {
            Queue<Point> toVisit = newArrayDeque();
            toVisit.add(intersection);
            Map<Point, Integer> intermediate = newHashMap();

            while (!toVisit.isEmpty()) {
                Point current = toVisit.remove();
                for (Point neighbour : getNeighbours(current)) {
                    if (intermediate.containsKey(neighbour)) {
                        continue;
                    }
                    if (intersectionList.contains(neighbour) && !intersection.equals(neighbour)) {
                        graph.addEdge(intersection, neighbour, intermediate.getOrDefault(current, 0) + 1);
                        continue;
                    }
                    intermediate.put(neighbour, intermediate.getOrDefault(current, 0) + 1);
                    toVisit.add(neighbour);
                }
            }
        }

        return (long) findLongestPath(start, end, 0, newArrayList(), graph);
    }

    void getDfsPath(PointWithDirection from, Function<PointWithDirection, Boolean> endCondition, Function<PointWithDirection, List<PointWithDirection>> neighbourFunction) {
        Stack<PointWithDirection> toVisit = new Stack<>();
        Set<PointWithDirection> seen = newHashSet();

        toVisit.add(from);
        while (true) {
            if (toVisit.isEmpty()) {
                return;
            }

            PointWithDirection current = toVisit.pop();

            if (endCondition.apply(current)) {
                pathDistances.add(current.distance);
                log.info("Found Path of distance {}", current.distance);
            }

            List<PointWithDirection> neighbours = neighbourFunction.apply(current)
                    .stream().filter(node -> !seen.contains(node))
                    .map(node -> node.withDistance(current.distance() + 1))
                    .collect(Collectors.toList());

            toVisit.addAll(neighbours);
            seen.addAll(neighbours);
        }
    }

    int findLongestPath(Point current, Point to, int distance, List<Point> path, Graph graph) {
        if (current.equals(to)) {
            return distance;
        }

        path.add(current);

        return graph.edges.get(current).entrySet().stream()
                .filter(e -> !path.contains(e.getKey()))
                .mapToInt(e -> findLongestPath(e.getKey(), to, distance + e.getValue(), newArrayList(path), graph))
                .max()
                .orElse(distance);
    }

    private List<PointWithDirection> getNeighbours(PointWithDirection current) {
        List<PointWithDirection> neighbours = newArrayList();

        final PointWithDirection up = new PointWithDirection(current.row() - 1, current.col(), 1, UP, newHashSet(current.previous));
        final PointWithDirection down = new PointWithDirection(current.row() + 1, current.col(), 1, DOWN, newHashSet(current.previous));
        final PointWithDirection left = new PointWithDirection(current.row(), current.col() - 1, 1, LEFT, newHashSet(current.previous));
        final PointWithDirection right = new PointWithDirection(current.row(), current.col() + 1, 1, RIGHT, newHashSet(current.previous));

        if (current.direction != DOWN && isInMap(up) && canWalk(up, 'v')) {
            neighbours.add(up);
        }
        if (current.direction != UP && isInMap(down) && canWalk(down, '^')) {
            neighbours.add(down);
        }
        if (current.direction != RIGHT && isInMap(left) && canWalk(left, '>')) {
            neighbours.add(left);
        }
        if (current.direction != LEFT && isInMap(right) && canWalk(right, '<')) {
            neighbours.add(right);
        }

        return neighbours;
    }

    private boolean isInMap(PointWithDirection node) {
        return (node.row() >= 0 && node.row() < map.length && node.col() >= 0 && node.col() < map[0].length);
    }

    private boolean canWalk(PointWithDirection walkTo, char blocker) {
        return map[walkTo.row()][walkTo.col()] != '#' && map[walkTo.row()][walkTo.col()] != blocker;
    }

    private List<Point> parseInput(String file) {
        map = loadCharMatrix(file);

        List<Point> intersectionList = newArrayList();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                Point current = new Point(row, col, 0);
                if (map[row][col] == '.' && getNeighbours(current).size() > 2) {
                    intersectionList.add(new Point(current.row, current.col, 0));
                }
            }
        }
        return intersectionList;
    }

    private List<Point> getNeighbours(Point current) {
        List<Point> neighbours = newArrayList();

        final Point up = new Point(current.row() - 1, current.col(), 0);
        final Point down = new Point(current.row() + 1, current.col(), 0);
        final Point left = new Point(current.row(), current.col() - 1, 0);
        final Point right = new Point(current.row(), current.col() + 1, 0);

        if (up.isInMap(map) && up.isNotWall(map)) {
            neighbours.add(up);
        }
        if (down.isInMap(map) && down.isNotWall(map)) {
            neighbours.add(down);
        }
        if (left.isInMap(map) && left.isNotWall(map)) {
            neighbours.add(left);
        }
        if (right.isInMap(map) && right.isNotWall(map)) {
            neighbours.add(right);
        }
        return neighbours;
    }

    @With
    public record PointWithDirection(int row, int col, int distance, Direction direction, Set<PointWithDirection> previous) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PointWithDirection that = (PointWithDirection) o;

            if (row != that.row) return false;
            if (col != that.col) return false;
            return distance == that.distance;
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 10091 * result + col;
            result = 10091 * result + distance;
            return result;
        }

        @Override
        public String toString() {
            return row + "," + col + ", d=" + distance + ", " + direction + ", " + previous;
        }
    }

    @With
    record Point(int row, int col, int distance) {
        boolean isInMap(Character[][] map) {
            return row >= 0 && row < map.length && col >= 0 && col < map[0].length;
        }

        boolean isNotWall(Character[][] map) {
            return map[row][col] != '#';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (row != point.row) return false;
            return col == point.col;
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 31 * result + col;
            return result;
        }
    }

    static class Graph {
        Map<Point, Map<Point, Integer>> edges = newHashMap();

        void addEdge(Point from, Point to, int distance) {
            edges.computeIfAbsent(from, point -> newHashMap()).put(to, distance);
            edges.computeIfAbsent(to, point -> newHashMap()).put(from, distance);
        }
    }
}
