package util;

import java.util.*;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

public class Grid2D {

    public static List<SearchPoint> bfs(SearchPoint from, SearchPoint to, Function<SearchPoint, List<SearchPoint>> neighbourFunction) {
        Queue<SearchPoint> toVisit = newLinkedList();
        Set<SearchPoint> seen = newHashSet();

        toVisit.add(from);
        seen.add(from);

        while (!toVisit.isEmpty()) {
            SearchPoint current = toVisit.remove();

            if (current == to) {
                return newArrayList(current);
            }

            List<SearchPoint> neighbours = neighbourFunction.apply(current).stream()
                    .filter(neighbour -> !seen.contains(neighbour))
                    .toList();

            toVisit.addAll(neighbours);
            seen.addAll(neighbours);
        }

        return newArrayList();
    }

    public record SearchPoint(Point2D point, long distance, SearchPoint previous) {

    }

    public static List<Util2D.Node> getPaths(Util2D.Node from, Function<Util2D.Node, Boolean> endCondition, Function<Util2D.Node, List<Util2D.Node>> neighbourFunction, Comparator<Util2D.Node> sorting, boolean exhaustive) {
        List<Util2D.Node> paths = newArrayList();

        PriorityQueue<Util2D.Node> toVisit = new PriorityQueue<>(sorting);
        Map<Util2D.Node, Long> seen = newHashMap();

        toVisit.add(from);
        while (!toVisit.isEmpty()) {
            Util2D.Node current = toVisit.remove();

            if (endCondition.apply(current)) {
                paths.add(current);
                if (!exhaustive) {
                    return newArrayList(current);
                }
            }

            List<Util2D.Node> apply = neighbourFunction.apply(current);
            List<Util2D.Node> neighbours = apply
                    .stream().filter(node -> {
                        if (exhaustive) {
                            return seen.getOrDefault(node, Long.MAX_VALUE) > current.distance();
                        } else {
                            return !seen.containsKey(node);
                        }
                    })
                    .toList();

            toVisit.addAll(neighbours);
            neighbours.forEach(node -> seen.put(node, node.distance()));
        }

        return paths;
    }
}
