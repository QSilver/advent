package advent2023;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import util.Extensions;

import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent25 {
    // https://adventofcode.com/2023/day/25

    public Long runP1(String file) {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        fileStream(file).forEach(line -> {
            String[] split = line.split(": ");
            String from = split[0];
            graph.addVertex(from);
            split[1].split(" ")
                    .forEach(to -> addToGraph(from, to, graph));
        });

        StoerWagnerMinimumCut<String, DefaultEdge> minimumCut = new StoerWagnerMinimumCut<>(graph);
        int minCutSize = minimumCut.minCut().size();
        int size = graph.vertexSet().size();

        return (long) minCutSize * (size - minCutSize);
    }

    private static void addToGraph(String from, String to, Graph<String, DefaultEdge> graph) {
        graph.addVertex(to);
        graph.addEdge(from, to);
    }
}
