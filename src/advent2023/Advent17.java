package advent2023;

import lombok.With;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;

import static advent2023.Advent17.Direction.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.parseInt;
import static java.lang.Math.min;

@Slf4j
public class Advent17 {
    // https://adventofcode.com/2023/day/17
    int[][] map;
    int[][] distances;

    public Long runP1(String file) {
        parseInput(file);

        Node start = new Node(0, 0, RIGHT, 0, 0);

        Function<Node, List<Node>> neighbourFunction = node -> getNeighbours(node, 1, 3);
        Function<Node, Boolean> endCondition = current -> (current.row == map.length - 1 && current.col == map[0].length - 1);
        Comparator<Node> sorting = Comparator.comparingInt(value -> value.distance);

        return getShortestPath(start, endCondition, neighbourFunction, sorting);
    }

    public Long runP2(String file) {
        parseInput(file);

        Node startRight = new Node(0, 0, RIGHT, 0, 0);
        Node startDown = new Node(0, 0, DOWN, 0, 0);

        Function<Node, List<Node>> neighbourFunction = node -> getNeighbours(node, 4, 10);
        Function<Node, Boolean> endCondition = current -> ((current.row == map.length - 1 && current.col == map[0].length - 1) && current.line >= 4);
        Comparator<Node> sorting = Comparator.comparingInt(value -> value.distance);

        long pathRight = getShortestPath(startRight, endCondition, neighbourFunction, sorting);
        long pathDown = getShortestPath(startDown, endCondition, neighbourFunction, sorting);
        return min(pathRight, pathDown);
    }

    long getShortestPath(Node from, Function<Node, Boolean> endCondition, Function<Node, List<Node>> neighbourFunction, Comparator<Node> sorting) {
        PriorityQueue<Node> toVisit = new PriorityQueue<>(sorting);
        Set<Node> seen = newHashSet();

        toVisit.add(from);
        Node end = null;
        while (end == null) {
            if (toVisit.isEmpty()) {
                return -1;
            }

            Node current = toVisit.remove();
            end = endCondition.apply(current) ? current : null;

            List<Node> neighbours = neighbourFunction.apply(current)
                    .stream().filter(node -> !seen.contains(node))
                    .map(node -> node.withDistance(current.distance + map[node.row][node.col]))
                    .toList();

            toVisit.addAll(neighbours);
            seen.addAll(neighbours);
        }

        return end.distance;
    }

    private List<Node> getNeighbours(Node current, int minStep, int maxStep) {
        List<Node> neighbours = newArrayList();

        final Node up = new Node(current.row - 1, current.col, UP, 1, 0);
        final Node down = new Node(current.row + 1, current.col, DOWN, 1, 0);
        final Node left = new Node(current.row, current.col - 1, LEFT, 1, 0);
        final Node right = new Node(current.row, current.col + 1, RIGHT, 1, 0);

        switch (current.direction) {
            case UP -> {
                if (current.line < maxStep) {
                    neighbours.add(new Node(current.row - 1, current.col, UP, current.line + 1, 0));
                }
                if (current.line >= minStep) {
                    neighbours.add(left);
                    neighbours.add(right);
                }
            }
            case DOWN -> {
                if (current.line < maxStep) {
                    neighbours.add(new Node(current.row + 1, current.col, DOWN, current.line + 1, 0));
                }
                if (current.line >= minStep) {
                    neighbours.add(left);
                    neighbours.add(right);
                }
            }
            case LEFT -> {
                if (current.line < maxStep) {
                    neighbours.add(new Node(current.row, current.col - 1, LEFT, current.line + 1, 0));
                }
                if (current.line >= minStep) {
                    neighbours.add(up);
                    neighbours.add(down);
                }
            }
            case RIGHT -> {
                if (current.line < maxStep) {
                    neighbours.add(new Node(current.row, current.col + 1, RIGHT, current.line + 1, 0));
                }
                if (current.line >= minStep) {
                    neighbours.add(up);
                    neighbours.add(down);
                }
            }
        }

        neighbours.removeIf(node -> !(node.row >= 0 && node.row < map.length && node.col >= 0 && node.col < map[0].length));
        return neighbours;
    }

    private void parseInput(String file) {
        List<String> list = Util.fileStream(file).toList();

        map = new int[list.size()][list.getFirst().length()];
        distances = new int[list.size()][list.getFirst().length()];
        for (int row = 0; row < list.size(); row++) {
            for (int col = 0; col < list.get(row).length(); col++) {
                map[row][col] = parseInt(list.get(row).charAt(col) + "");
                distances[row][col] = MAX_VALUE;
            }
        }
    }

    @With
    record Node(int row, int col, Direction direction, int line, int distance) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (row != node.row) return false;
            if (col != node.col) return false;
            if (line != node.line) return false;
            return direction == node.direction;
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 151 * result + col;
            result = 151 * result + direction.hashCode();
            result = 151 * result + line;
            return result;
        }
    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}