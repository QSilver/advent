package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util.Node;
import util.Util2D.Point2D;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Comparator.comparingLong;
import static util.Util.Direction.*;
import static util.Util.getAllPaths;
import static util.Util2D.get2DPoints;
import static util.Util2D.loadCharMatrix;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent16 {
    // https://adventofcode.com/2024/day/16

    private static Character[][] map;

    public Long runP1(String file) {
        return getPaths(file).getFirst().distance();
    }

    public Long runP2(String file) {
        List<Node> paths = getPaths(file);

        long minDistance = paths.getFirst().distance();
        return paths.stream()
                .filter(node -> node.distance() == minDistance)
                .flatMap(Advent16::traverse)
                .distinct()
                .count();
    }

    private List<Node> getPaths(String file) {
        map = loadCharMatrix(file);
        Point2D start = get2DPoints(file, 'S').getFirst();
        Point2D end = get2DPoints(file, 'E').getFirst();

        Node startRightNode = new Node((int) start.row(), (int) start.col(), RIGHT, 0, null);

        Function<Node, List<Node>> neighbourFunction = this::getNeighbours;
        Function<Node, Boolean> endCondition = current -> (current.row() == end.row() && current.col() == end.col());
        Comparator<Node> sorting = comparingLong(Node::distance);

        return getAllPaths(startRightNode, endCondition, neighbourFunction, sorting);
    }

    private static Stream<Point2D> traverse(Node node) {
        Set<Point2D> onBestPath = newHashSet();
        Node traversal = node;
        while (traversal != null) {
            onBestPath.add(new Point2D(traversal.row(), traversal.col()));
            traversal = traversal.previous();
        }
        return onBestPath.stream();
    }

    private List<Node> getNeighbours(Node current) {
        List<Node> neighbours = newArrayList();

        final Node up = new Node(current.row() - 1, current.col(), UP, current.distance() + 1001, current);
        final Node down = new Node(current.row() + 1, current.col(), DOWN, current.distance() + 1001, current);
        final Node left = new Node(current.row(), current.col() - 1, LEFT, current.distance() + 1001, current);
        final Node right = new Node(current.row(), current.col() + 1, RIGHT, current.distance() + 1001, current);

        switch (current.direction()) {
            case UP -> {
                neighbours.add(new Node(current.row() - 1, current.col(), UP, current.distance() + 1, current));
                neighbours.add(left);
                neighbours.add(right);
            }
            case DOWN -> {
                neighbours.add(new Node(current.row() + 1, current.col(), DOWN, current.distance() + 1, current));
                neighbours.add(left);
                neighbours.add(right);
            }
            case LEFT -> {
                neighbours.add(new Node(current.row(), current.col() - 1, LEFT, current.distance() + 1, current));
                neighbours.add(up);
                neighbours.add(down);
            }
            case RIGHT -> {
                neighbours.add(new Node(current.row(), current.col() + 1, RIGHT, current.distance() + 1, current));
                neighbours.add(up);
                neighbours.add(down);
            }
        }

        neighbours.removeIf(node -> !(node.row() >= 0 && node.row() < map.length && node.col() >= 0 && node.col() < map[0].length));
        neighbours.removeIf(node -> map[node.row()][node.col()] == '#');
        return neighbours;
    }
}