package advent2025;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.data.util.Pair;
import util.Util.Point3D;

import java.util.*;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.lang.Integer.compare;
import static util.InputUtils.fileStream;
import static util.Util.forEachTwo;

@Slf4j
public class Advent8 {
    // https://adventofcode.com/2025/day/8

    public Long runP1(String file, int edgeCount) {
        Result result = getGraphAndDistances(file);

        for (int i = 0; i < edgeCount; i++) {
            Pair<Point3D, Point3D> edge = result.sortedDistances().get(i).getKey();
            result.graph().addEdge(edge.getFirst(), edge.getSecond());
        }

        List<Set<Point3D>> sortedComponents = new ConnectivityInspector<>(result.graph())
                .connectedSets().stream()
                .sorted((set1, set2) -> compare(set2.size(), set1.size()))
                .toList();

        long size1 = sortedComponents.get(0).size();
        long size2 = sortedComponents.get(1).size();
        long size3 = sortedComponents.get(2).size();
        return size1 * size2 * size3;
    }

    public Long runP2(String file) {
        Result result = getGraphAndDistances(file);

        for (Pair<Point3D, Point3D> edge : result.sortedDistances.stream().map(Map.Entry::getKey).toList()) {
            result.graph.addEdge(edge.getFirst(), edge.getSecond());

            if (new ConnectivityInspector<>(result.graph).isConnected()) {
                return edge.getFirst().x() * edge.getSecond().x();
            }
        }

        throw new IllegalStateException("No connection made");
    }

    private static Result getGraphAndDistances(String file) {
        List<Point3D> junctions = fileStream(file)
                .map(line -> new Point3D(line.split(",")))
                .toList();

        LinkedHashMap<Pair<Point3D, Point3D>, Double> distances = newLinkedHashMap();
        forEachTwo(junctions, (point1, point2) -> distances.put(Pair.of(point1, point2), point1.distance(point2)));

        List<Map.Entry<Pair<Point3D, Point3D>, Double>> sortedDistances = distances
                .entrySet().stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .toList();

        SimpleGraph<Point3D, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        junctions.forEach(graph::addVertex);
        return new Result(graph, sortedDistances);
    }

    private record Result(SimpleGraph<Point3D, DefaultEdge> graph,
                          List<Map.Entry<Pair<Point3D, Point3D>, Double>> sortedDistances) {
    }
}
