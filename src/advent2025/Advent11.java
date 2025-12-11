package advent2025;

import com.google.common.base.Objects;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent11 {
    // https://adventofcode.com/2025/day/11

    public Long runP1(String file) {
        Map<String, Node> nodes = getNodes(file);

        List<Node> paths = getPaths(nodes.get("you"),
                node -> node.name().equals("out"),
                Node::getNeighbours,
                Comparator.comparingLong(Node::distance)
        );

        return (long) paths.size();
    }

    public Long runP2(String file) {
        Map<String, Node> nodes = getNodes(file);

        List<Node> paths = getPaths(nodes.get("svr"),
                node -> node.name().equals("out"),
                Node::getNeighbours,
                Comparator.comparingLong(Node::distance)
        );

        return (long) paths.stream()
                .filter(path -> path.path().stream().anyMatch(n -> n.name().equals("dac")))
                .filter(path -> path.path().stream().anyMatch(n -> n.name().equals("fft")))
                .toList().size();
    }

    private static Map<String, Node> getNodes(String file) {
        Stream<String> stringStream = fileStream(file);

        Map<String, Node> nodes = newHashMap();

        stringStream.forEach(line -> {
            String[] split = line.split(":");

            String trim = split[0].trim();

            Node node = nodes.getOrDefault(trim, new Node(trim, newArrayList(), 0L, newArrayList()));
            nodes.put(trim, node);

            String[] edges = split[1].trim().split(" ");
            for (String edge : edges) {
                String to = edge.trim();
                Node toNode = nodes.getOrDefault(to, new Node(to, newArrayList(), 0L, newArrayList()));
                node.neighbours.add(toNode);
                nodes.put(to, toNode);
            }
        });
        return nodes;
    }

    static List<Node> getPaths(Node from, Function<Node, Boolean> endCondition, Function<Node, List<Node>> neighbourFunction, Comparator<Node> sorting) {
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
            List<Node> neighbours = apply.stream()
                    .filter(node -> seen.getOrDefault(node, Long.MAX_VALUE) > current.distance)
                    .toList();

            toVisit.addAll(neighbours);
            neighbours.forEach(node -> seen.put(node, node.distance));
        }

        return paths;
    }

    record Node(String name, List<Node> neighbours, long distance, List<Node> path) {
        public List<Node> getNeighbours() {
            List<Node> currentPath = newArrayList(this.path);
            currentPath.add(this);
            return neighbours.stream()
                    .filter(neighbour -> !currentPath.contains(neighbour))
                    .map(neighbour -> new Node(neighbour.name, neighbour.neighbours, this.distance + 1, currentPath))
                    .toList();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return distance == node.distance && Objects.equal(name, node.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, distance);
        }

        @Override
        public @NonNull String toString() {
            return STR."Node{name='\{name}', neighbours=\{neighbours}}";
        }
    }
}
