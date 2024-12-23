package advent2024;

import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.alg.clique.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import util.Extensions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static util.InputUtils.fileStream;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent23 {
    // https://adventofcode.com/2024/day/23

    public Long runP1(String file) {
        Set<Set<String>> cliques = newHashSet();

        getCliqueFinder(file).forEach(clique -> {
            List<String> list = clique.stream().toList();
            if (list.size() >= 3) {
                List<List<String>> subCliques = newArrayList();
                for (int i = 0; i < list.size() - 2; i++) {
                    for (int j = i + 1; j < list.size() - 1; j++) {
                        for (int k = j + 1; k < list.size(); k++) {
                            subCliques.add(newArrayList(list.get(i), list.get(j), list.get(k)));
                        }
                    }
                }
                subCliques.forEach(subClique -> {
                    if (subClique.stream().anyMatch(node -> node.startsWith("t"))) {
                        cliques.add(subClique.stream().collect(Collectors.toSet()));
                    }
                });
            }
        });

        return (long) cliques.size();
    }

    public String runP2(String file) {
        Set<String> maxClique = newHashSet();
        getCliqueFinder(file).forEach(clique -> {
            if (clique.size() > maxClique.size()) {
                maxClique.clear();
                maxClique.addAll(clique);
            }
        });

        return maxClique.stream().sorted().collect(Collectors.joining(","));
    }

    private static BronKerboschCliqueFinder<String, DefaultEdge> getCliqueFinder(String file) {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        fileStream(file).forEach(line -> {
            String[] split = line.split("-");

            String from = split[0];
            String to = split[1];

            graph.addVertex(from);
            graph.addVertex(to);
            graph.addEdge(from, to);
            graph.addEdge(to, from);
        });

        BronKerboschCliqueFinder<String, DefaultEdge> cliqueFinder = new BronKerboschCliqueFinder<>(graph);
        return cliqueFinder;
    }
}
