package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Util2D.Direction;
import util.Util2D.Node;
import util.Util2D.Point2D;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Comparator.comparingLong;
import static util.InputUtils.fileStream;
import static util.Util2D.Direction.DOWN;
import static util.Util2D.getPaths;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent18 {
    // https://adventofcode.com/2024/day/18

    public Long runP1(String file, int size, int bitsFallen) {
        return getPathsFromFile(file, size, bitsFallen).getFirst().distance();
    }

    public String runP2(String file, int size) {
        List<Point2D> bits = fileStream(file)
                .map(Point2D::fromCoords)
                .toList();

        int min = 0;
        int max = bits.size();
        while (true) {
            int fallen = (min + max) / 2;

            if (min == max - 1) {
                System.out.println(STR."\{max}th wall blocks any path");
                return bits.get(min).toString();
            }

            List<Node> paths = getPathsFromFile(file, size, fallen);
            if (paths.isEmpty()) {
                max = fallen;
                System.out.println(STR."After \{fallen} walls, found no path");
            } else {
                min = fallen;
                System.out.println(STR."After \{fallen} walls, found path of length \{paths.getFirst().distance()}");
            }
        }
    }

    private List<Node> getPathsFromFile(String file, int size, int bitsFallen) {
        List<Point2D> bits = fileStream(file)
                .map(Point2D::fromCoords)
                .toList();

        Node startNode = new Node(new Point2D(0, 0), DOWN, 0, null);
        Function<Node, Boolean> endCondition = current -> current.point().equals(new Point2D(size, size));
        Comparator<Node> sorting = comparingLong(Node::distance);

        List<Point2D> currentBits = bits.subList(0, bitsFallen);
        Function<Node, List<Node>> neighbourFunction = node -> getNeighbours(node, newHashSet(currentBits), size);

        return getPaths(startNode, endCondition, neighbourFunction, false);
    }

    private List<Node> getNeighbours(Node current, Set<Point2D> bits, int size) {
        return Direction.values().stream()
                .map(direction -> new Node(current.point().neighbour(direction), direction, current.distance() + 1, current))
                .filter(node -> node.point().row() >= 0 && node.point().col() >= 0 && node.point().row() <= size && node.point().col() <= size)
                .filter(node -> !bits.contains(node.point()))
                .toList();
    }
}