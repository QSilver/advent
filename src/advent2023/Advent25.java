package advent2023;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import static java.util.Arrays.stream;
import static util.InputUtils.fileStream;

@Slf4j
public class Advent25 {
    // https://adventofcode.com/2023/day/25

    public Long runP1(String file) {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        fileStream(file).forEach(line -> {
            String[] split = line.split(": ");
            graph.addVertex(split[0]);
            stream(split[1].split(" ")).forEach(to -> {
                graph.addVertex(to);
                graph.addEdge(split[0], to);
            });
        });

        StoerWagnerMinimumCut<String, DefaultEdge> minimumCut = new StoerWagnerMinimumCut<>(graph);
        int minCutSize = minimumCut.minCut().size();
        int size = graph.vertexSet().size();

        return (long) minCutSize * (size - minCutSize);
    }
}
