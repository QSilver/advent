package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util2D;
import util.Util2D.Node;
import util.Util2D.Point2D;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Comparator.comparingLong;
import static util.InputUtils.get2DPoints;
import static util.InputUtils.loadCharMatrix;
import static util.Util2D.Direction.RIGHT;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent20 {
    // https://adventofcode.com/2024/day/20

    private static Character[][] map;

    public Long runP1(String file) {
        return run(file, 2);
    }

    public Long runP2(String file) {
        return run(file, 20);
    }

    private long run(String file, int maxCheat) {
        Node shortestPath = getShortestPath(file);
        Map<Point2D, Long> distanceMap = getDistanceAlongPath(shortestPath);

        Map<Long, Long> cheatFrequencies = newHashMap();
        distanceMap.forEach((current, currentDistance) ->
                current.getNAround(maxCheat).forEach(cheatPoint -> {
                    long cheatAmount = cheatPoint.manhattanDistanceTo(current);
                    if (cheatAmount <= maxCheat) {
                        long saved = distanceMap.getOrDefault(cheatPoint, 0L) - (currentDistance + cheatAmount);
                        if (saved > 0) {
                            // cheat is a shortcut
                            cheatFrequencies.merge(saved, 1L, Long::sum);
                        }
                    }
                }));

        return cheatFrequencies.entrySet().stream()
                .filter(entry -> entry.getKey() >= 100)
                .mapToLong(Map.Entry::getValue)
                .sum();
    }

    private static Map<Point2D, Long> getDistanceAlongPath(Node shortestPath) {
        Map<Point2D, Long> distanceMap = newHashMap();
        Node traversal = shortestPath;
        while (traversal != null) {
            distanceMap.put(traversal.point(), traversal.distance());
            traversal = traversal.previous();
        }
        return distanceMap;
    }

    private Node getShortestPath(String file) {
        map = loadCharMatrix(file);

        Point2D start = get2DPoints(file, 'S').getFirst();
        Point2D end = get2DPoints(file, 'E').getFirst();

        Node startNode = new Node(start, RIGHT, 0, null);

        Function<Node, List<Node>> neighbourFunction = this::getNeighbours;
        Function<Node, Boolean> endCondition = current -> current.point().equals(end);
        Comparator<Node> sorting = comparingLong(Node::distance);

        return Util2D.getShortestPath(startNode, endCondition, neighbourFunction, sorting);
    }

    private List<Node> getNeighbours(Node current) {
        return Util2D.Direction.values().stream()
                .map(direction -> new Node(current.point().neighbour(direction), direction, current.distance() + 1, current))
                .filter(node -> node.point().row() >= 0 && node.point().col() >= 0 && node.point().row() < map.length && node.point().col() < map[0].length)
                .filter(node -> !isWall(node))
                .toList();
    }

    private static boolean isWall(Node node) {
        return map.atPos(node.point()) == '#';
    }
}
