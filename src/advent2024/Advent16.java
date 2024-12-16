package advent2024;

import lombok.With;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util.Direction;
import util.Util2D.Point2D;

import java.util.*;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Comparator.comparingLong;
import static util.Util.Direction.*;
import static util.Util2D.get2DPoints;
import static util.Util2D.loadCharMatrix;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent16 {
    // https://adventofcode.com/2024/day/16

    private static Character[][] map;

    public Long runP1(String file) {
        map = loadCharMatrix(file);
        Point2D start = get2DPoints(file, 'S').getFirst();
        Point2D end = get2DPoints(file, 'E').getFirst();

        Node startRightNode = new Node((int) start.row(), (int) start.col(), RIGHT, 0, null);

        Function<Node, List<Node>> neighbourFunction = this::getNeighbours;
        Function<Node, Boolean> endCondition = current -> (current.row == end.row() && current.col == end.col());
        Comparator<Node> sorting = comparingLong(value -> value.distance);

        List<Node> paths = getAllPaths(startRightNode, endCondition, neighbourFunction, sorting, _ -> 0);
        return paths.getFirst().distance;
    }

    public Long runP2(String file) {
        map = loadCharMatrix(file);
        Point2D start = get2DPoints(file, 'S').getFirst();
        Point2D end = get2DPoints(file, 'E').getFirst();

        Node startRightNode = new Node((int) start.row(), (int) start.col(), RIGHT, 0, null);

        Function<Node, List<Node>> neighbourFunction = this::getNeighbours;
        Function<Node, Boolean> endCondition = current -> (current.row == end.row() && current.col == end.col());
        Comparator<Node> sorting = comparingLong(value -> value.distance);

        List<Node> paths = getAllPaths(startRightNode, endCondition, neighbourFunction, sorting, _ -> 0);
        long min = paths.getFirst().distance;

        Set<Point2D> onBestPath = newHashSet();
        paths.stream()
                .filter(node -> node.distance == min)
                .forEach(node -> {
                    Node traversal = node;
                    while (traversal != null) {
                        onBestPath.add(new Point2D(traversal.row, traversal.col));
                        traversal = traversal.previous;
                    }
                });

        return (long) onBestPath.size();
    }

    Node getShortestPath(Node from, Function<Node, Boolean> endCondition, Function<Node, List<Node>> neighbourFunction, Comparator<Node> sorting, Function<Node, Integer> distanceFunction) {
        PriorityQueue<Node> toVisit = new PriorityQueue<>(sorting);
        Set<Node> seen = newHashSet();

        toVisit.add(from);
        Node end = null;
        while (end == null) {
            if (toVisit.isEmpty()) {
                break;
            }

            Node current = toVisit.remove();
            end = endCondition.apply(current) ? current : null;

            List<Node> neighbours = neighbourFunction.apply(current)
                    .stream().filter(node -> !seen.contains(node))
                    .toList();

            toVisit.addAll(neighbours);
            seen.addAll(neighbours);
        }

        return end;
    }

    List<Node> getAllPaths(Node from, Function<Node, Boolean> endCondition, Function<Node, List<Node>> neighbourFunction, Comparator<Node> sorting, Function<Node, Integer> distanceFunction) {
        List<Node> paths = newArrayList();

        PriorityQueue<Node> toVisit = new PriorityQueue<>(sorting);
        Map<Node, Long> seen = newHashMap();

        toVisit.add(from);
        while (!toVisit.isEmpty()) {
            Node current = toVisit.remove();

            if (endCondition.apply(current)) {
                paths.add(current);
            }

            List<Node> apply = neighbourFunction.apply(current);
            List<Node> neighbours = apply
                    .stream().filter(node -> seen.getOrDefault(node, Long.MAX_VALUE) > current.distance)
                    .toList();

            toVisit.addAll(neighbours);
            neighbours.forEach(node -> seen.put(node, node.distance));
        }

        return paths;
    }

    private List<Node> getNeighbours(Node current) {
        List<Node> neighbours = newArrayList();

        final Node up = new Node(current.row - 1, current.col, UP, current.distance + 1001, current);
        final Node down = new Node(current.row + 1, current.col, DOWN, current.distance + 1001, current);
        final Node left = new Node(current.row, current.col - 1, LEFT, current.distance + 1001, current);
        final Node right = new Node(current.row, current.col + 1, RIGHT, current.distance + 1001, current);

        switch (current.direction) {
            case UP -> {
                neighbours.add(new Node(current.row - 1, current.col, UP, current.distance + 1, current));
                neighbours.add(left);
                neighbours.add(right);
            }
            case DOWN -> {
                neighbours.add(new Node(current.row + 1, current.col, DOWN, current.distance + 1, current));
                neighbours.add(left);
                neighbours.add(right);
            }
            case LEFT -> {
                neighbours.add(new Node(current.row, current.col - 1, LEFT, current.distance + 1, current));
                neighbours.add(up);
                neighbours.add(down);
            }
            case RIGHT -> {
                neighbours.add(new Node(current.row, current.col + 1, RIGHT, current.distance + 1, current));
                neighbours.add(up);
                neighbours.add(down);
            }
        }

        neighbours.removeIf(node -> !(node.row >= 0 && node.row < map.length && node.col >= 0 && node.col < map[0].length));
        neighbours.removeIf(node -> map[node.row][node.col] == '#');
        return neighbours;
    }

    @With
    record Node(int row, int col, Direction direction, long distance, Node previous) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (row != node.row) return false;
            if (col != node.col) return false;
            return direction == node.direction;
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 151 * result + col;
            result = 151 * result + direction.hashCode();
            return result;
        }
    }
}