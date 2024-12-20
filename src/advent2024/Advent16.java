package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Comparator.comparingLong;
import static util.InputUtils.get2DPoints;
import static util.InputUtils.loadCharMatrix;
import static util.Util2D.Direction.RIGHT;
import static util.Util2D.*;

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

        Node startRightNode = new Node(start, RIGHT, 0, null);

        Function<Node, List<Node>> neighbourFunction = this::getNeighbours;
        Function<Node, Boolean> endCondition = current -> current.point().equals(end);
        Comparator<Node> sorting = comparingLong(Node::distance);

        return getAllPaths(startRightNode, endCondition, neighbourFunction, sorting);
    }

    private static Stream<Point2D> traverse(Node node) {
        Set<Point2D> onBestPath = newHashSet();
        Node traversal = node;
        while (traversal != null) {
            onBestPath.add(traversal.point());
            traversal = traversal.previous();
        }
        return onBestPath.stream();
    }

    private List<Node> getNeighbours(Node current) {
        List<Node> neighbours = newArrayList();

        final Node forward = new Node(current.point().neighbour(current.direction()), current.direction(), current.distance() + 1, current);
        final Node clockwise = current
                .withPoint(current.point().neighbour(current.direction().clockwise()))
                .withDirection(current.direction().clockwise())
                .withDistance(current.distance() + 1001)
                .withPrevious(current);
        final Node counterclockwise = current
                .withPoint(current.point().neighbour(current.direction().counterclockwise()))
                .withDirection(current.direction().counterclockwise())
                .withDistance(current.distance() + 1001)
                .withPrevious(current);
        neighbours.add(forward);
        neighbours.add(clockwise);
        neighbours.add(counterclockwise);

        neighbours.removeIf(Advent16::isWall);
        return neighbours;
    }

    private static boolean isWall(Node node) {
        return map.atPos(node.point()) == '#';
    }
}