package advent2025;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.data.util.Pair;
import util.Util.Point3D;

import java.util.*;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.lang.Integer.parseInt;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent8 {
    // https://adventofcode.com/2025/day/8

    public Long runP1(String file) {
        List<String> input = fileStream(file).toList();

        List<Point3D> junctions = input.stream().map(line -> {
            String[] split = line.split(",");
            return new Point3D(parseInt(split[0]), parseInt(split[1]), parseInt(split[2]));
        }).toList();

        LinkedHashMap<Pair<Point3D, Point3D>, Double> distances = newLinkedHashMap();
        for (int i = 0; i < junctions.size() - 1; i++) {
            for (int j = i + 1; j < junctions.size(); j++) {
                double distance = junctions.get(i).distance(junctions.get(j));
                distances.put(Pair.of(junctions.get(i), junctions.get(j)), distance);
            }
        }

        List<Map.Entry<Pair<Point3D, Point3D>, Double>> sortedDistances = distances
                .entrySet().stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .toList();

        SimpleGraph<Point3D, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        junctions.forEach(graph::addVertex);

        for (int i = 0; i < 1000; i++) {
            Pair<Point3D, Point3D> edge = sortedDistances.get(i).getKey();
            graph.addEdge(edge.getFirst(), edge.getSecond());
        }

        ConnectivityInspector<Point3D, DefaultEdge> inspector = new ConnectivityInspector<>(graph);

        List<Set<Point3D>> sortedComponents = inspector
                .connectedSets().stream()
                .sorted((set1, set2) -> Integer.compare(set2.size(), set1.size()))
                .toList();

        long size1 = sortedComponents.get(0).size();
        long size2 = sortedComponents.get(1).size();
        long size3 = sortedComponents.get(2).size();

        return size1 * size2 * size3;
    }

    public Long runP2(String file) {
        List<String> input = fileStream(file).toList();

        List<Point3D> junctions = input.stream().map(line -> {
            String[] split = line.split(",");
            return new Point3D(parseInt(split[0]), parseInt(split[1]), parseInt(split[2]));
        }).toList();

        LinkedHashMap<Pair<Point3D, Point3D>, Double> distances = newLinkedHashMap();
        for (int i = 0; i < junctions.size() - 1; i++) {
            for (int j = i + 1; j < junctions.size(); j++) {
                double distance = junctions.get(i).distance(junctions.get(j));
                distances.put(Pair.of(junctions.get(i), junctions.get(j)), distance);
            }
        }

        List<Map.Entry<Pair<Point3D, Point3D>, Double>> sortedDistances = distances
                .entrySet().stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .toList();

        SimpleGraph<Point3D, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        junctions.forEach(graph::addVertex);


        for (Pair<Point3D, Point3D> edge : sortedDistances.stream().map(Map.Entry::getKey).toList()) {
            graph.addEdge(edge.getFirst(), edge.getSecond());

            if (new ConnectivityInspector<>(graph).isConnected()) {
                return edge.getFirst().x() * edge.getSecond().x();
            }
        }

        throw new IllegalStateException("No connection made");
    }
}
