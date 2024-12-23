package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.alg.clique.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import util.Extensions;

import static java.lang.Integer.compare;
import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;
import static util.InputUtils.fileStream;
import static util.Util.generateSubsets;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent23 {
    // https://adventofcode.com/2024/day/23

    public Long runP1(String file) {
        return stream(getCliqueFinder(file).spliterator(), false) // get all maximal cliques
                .filter(clique -> clique.size() >= 3) // only size 3 or more
                .flatMap(clique -> generateSubsets(3, clique).stream()) // generate all subsets of size 3 (1234 -> 123 124 134 234)
                .distinct()
                .filter(subClique -> subClique.stream().anyMatch(node -> node.startsWith("t"))) // only those that start with t
                .count();
    }

    public String runP2(String file) {
        return stream(getCliqueFinder(file).spliterator(), false) // get all maximal cliques
                .sorted((o1, o2) -> compare(o2.size(), o1.size())) // sort by size
                .findFirst().orElseThrow() // get largest clique
                .stream().sorted() // sort it alphabetically
                .collect(joining(",")); // join with commas
    }

    private static BronKerboschCliqueFinder<String, DefaultEdge> getCliqueFinder(String file) {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        fileStream(file).forEach(line -> addToGraph(line, graph));
        return new BronKerboschCliqueFinder<>(graph);
    }

    private static void addToGraph(String line, Graph<String, DefaultEdge> graph) {
        String[] split = line.split("-");
        graph.addVertex(split[0]);
        graph.addVertex(split[1]);
        graph.addEdge(split[0], split[1]);
    }
}
