package advent2023;

import lombok.With;
import lombok.extern.slf4j.Slf4j;
import util.Util;
import util.Util.Direction;

import java.util.*;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Comparator.comparingInt;
import static util.Util.Direction.*;

@Slf4j
public class Advent23 {
    // https://adventofcode.com/2023/day/23

    char[][] map;
    int[][] distances;
    List<Integer> pathDistances = newArrayList();

    public Long runP1(String file) {
        parseInput(file);
        getPath(new PointWithDirection(0, 1, 0, DOWN, newHashSet()),
                current -> (current.row() == map.length - 1 && current.col() == map[0].length - 2),
                this::getNeighbours,
                (o1, o2) -> Integer.compare(o2.distance, o1.distance));
        return pathDistances.stream().mapToLong(value -> value).max().orElseThrow();
    }

    public Long runP2(String file) {
        parseInput(file);
        getDfsPath(new PointWithDirection(0, 1, 0, DOWN, newHashSet()),
                current -> (current.row() == map.length - 1 && current.col() == map[0].length - 2),
                this::getNeighboursIgnoringSlopes,
                comparingInt(PointWithDirection::distance));
        return pathDistances.stream().mapToLong(value -> value).max().orElseThrow();
    }

    void recordPath(int distance) {
        pathDistances.add(distance);
        log.info("Found Path of distance {}", distance);
    }

    long getPath(PointWithDirection from, Function<PointWithDirection, Boolean> endCondition, Function<PointWithDirection, List<PointWithDirection>> neighbourFunction, Comparator<PointWithDirection> sorting) {
        PriorityQueue<PointWithDirection> toVisit = new PriorityQueue<>(sorting);
        Set<PointWithDirection> seen = newHashSet();

        toVisit.add(from);
        while (true) {
            if (toVisit.isEmpty()) {
                return -1;
            }

            PointWithDirection current = toVisit.remove();

            if (endCondition.apply(current)) {
                recordPath(current.distance);
            }

            List<PointWithDirection> neighbours = neighbourFunction.apply(current)
                    .stream().filter(node -> !seen.contains(node))
                    .map(node -> node.withDistance(current.distance() + 1))
                    .toList();

            toVisit.addAll(neighbours);
            seen.addAll(neighbours);
        }
    }

    long getDfsPath(PointWithDirection from, Function<PointWithDirection, Boolean> endCondition, Function<PointWithDirection, List<PointWithDirection>> neighbourFunction, Comparator<PointWithDirection> sorting) {
        Stack<PointWithDirection> toVisit = new Stack<>();
        Set<PointWithDirection> seen = newHashSet();

        toVisit.add(from);
        while (true) {
            if (toVisit.isEmpty()) {
                return -1;
            }

            PointWithDirection current = toVisit.pop();

            if (endCondition.apply(current)) {
                recordPath(current.distance);
            }

            List<PointWithDirection> neighbours = neighbourFunction.apply(current)
                    .stream().filter(node -> !seen.contains(node))
                    .map(node -> node.withDistance(current.distance() + 1))
                    .toList();

            toVisit.addAll(neighbours);
            seen.addAll(neighbours);
        }
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

    private List<PointWithDirection> getNeighboursIgnoringSlopes(PointWithDirection current) {

        List<PointWithDirection> neighbours = newArrayList();

        final PointWithDirection up = new PointWithDirection(current.row() - 1, current.col(), 1, UP, newHashSet(current.previous));
        final PointWithDirection down = new PointWithDirection(current.row() + 1, current.col(), 1, DOWN, newHashSet(current.previous));
        final PointWithDirection left = new PointWithDirection(current.row(), current.col() - 1, 1, LEFT, newHashSet(current.previous));
        final PointWithDirection right = new PointWithDirection(current.row(), current.col() + 1, 1, RIGHT, newHashSet(current.previous));

        if (current.direction != DOWN && isInMap(up) && canWalk(up, '#')) {
            if (up.unvisited(current.previous)) {
                neighbours.add(up);
                up.previous.add(current);
            }
        }
        if (current.direction != UP && isInMap(down) && canWalk(down, '#')) {
            if (down.unvisited(current.previous)) {
                neighbours.add(down);
                down.previous.add(current);
            }
        }
        if (current.direction != RIGHT && isInMap(left) && canWalk(left, '#')) {
            if (left.unvisited(current.previous)) {
                neighbours.add(left);
                left.previous.add(current);
            }
        }
        if (current.direction != LEFT && isInMap(right) && canWalk(right, '#')) {
            if (right.unvisited(current.previous)) {
                neighbours.add(right);
                right.previous.add(current);
            }
        }

        return neighbours;
    }

    private boolean isInMap(PointWithDirection node) {
        return (node.row() >= 0 && node.row() < map.length && node.col() >= 0 && node.col() < map[0].length);
    }

    private boolean canWalk(PointWithDirection walkTo, char blocker) {
        return map[walkTo.row()][walkTo.col()] != '#' && map[walkTo.row()][walkTo.col()] != blocker;
    }

    private void parseInput(String file) {
        List<String> list = Util.fileStream(file).toList();

        map = new char[list.size()][list.getFirst().length()];
        distances = new int[list.size()][list.getFirst().length()];
        for (int row = 0; row < list.size(); row++) {
            for (int col = 0; col < list.get(row).length(); col++) {
                map[row][col] = list.get(row).charAt(col);
                distances[row][col] = MAX_VALUE;
            }
        }
    }

    @With
    public record PointWithDirection(int row, int col, int distance, Direction direction, Set<PointWithDirection> previous) {

        boolean unvisited(Set<PointWithDirection> list) {
            return list.stream().noneMatch(other -> other.row == row() && other.col == col());
        }

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
}
