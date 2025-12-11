package advent2025;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Node;
import lombok.SneakyThrows;

import java.io.File;
import java.util.List;
import java.util.Map;

import static advent2025.Advent11.getNodes;
import static com.google.common.collect.Lists.newArrayList;
import static guru.nidi.graphviz.attribute.Color.RED;
import static guru.nidi.graphviz.attribute.Style.lineWidth;
import static guru.nidi.graphviz.engine.Graphviz.fromGraph;
import static guru.nidi.graphviz.model.Factory.graph;

public class Advent11Viz {
    @SneakyThrows
    static void main() {
        Map<String, Advent11.Node> nodes = getNodes("advent2025/advent11.in");

        List<Node> graphNodes = newArrayList();
        for (Advent11.Node vertex : nodes.values()) {
            Node gNode = Factory.node(vertex.name());

            if (newArrayList("dac", "fft", "you", "out").contains(vertex.name())) {
                gNode = gNode.with(RED).with(lineWidth(4));
            }

            for (Advent11.Node neighbour : vertex.neighbours()) {
                gNode = gNode.link(Factory.node(neighbour.name()));
            }
            graphNodes.add(gNode);
        }

        fromGraph(graph("reactor").directed().with(graphNodes))
                .render(Format.PNG).toFile(new File("reactor"));
    }
}
