package advent2021;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Character.isUpperCase;

@Slf4j
public class Advent12 {
    static int pathCount = 0;

    public static void main(String[] args) {
        Map<String, Node> nodes = newHashMap();
        Util.fileStream("advent2021/advent12")
            .forEach(s -> {
                String[] split = s.split("-");
                nodes.put(split[0], new Node(split[0], newHashSet()));
                nodes.put(split[1], new Node(split[1], newHashSet()));
            });

        Util.fileStream("advent2021/advent12")
            .forEach(s -> {
                String[] split = s.split("-");
                nodes.get(split[0]).adjacent.add(nodes.get(split[1]));
                nodes.get(split[1]).adjacent.add(nodes.get(split[0]));
            });

        //floodFill(nodes.get("start"), newHashSet(), "start", true); P1
        floodFill(nodes.get("start"), newHashSet(), "start", false);
        log.info("Paths: {}", pathCount);
    }

    public static void floodFill(Node current, Set<Node> visited, String path, boolean hasVisitTwice) {
        if ("end".equals(current.label)) {
            pathCount++;
            return;
        }

        boolean newHasVisitTwice = hasVisitTwice;
        Set<Node> newVisited = newHashSet(visited);
        if (visited.contains(current)) {
            if (hasVisitTwice) {
                return;
            } else {
                newHasVisitTwice = true;
            }
        } else {
            if (!isUpperCase(current.label.charAt(0))) {
                newVisited.add(current);
            }
        }

        boolean finalNewHasVisitTwice = newHasVisitTwice;
        current.adjacent.forEach(neighbour -> {
            if (!"start".equals(neighbour.label)) {
                floodFill(neighbour, newVisited, path + "," + neighbour.label, finalNewHasVisitTwice);
            }
        });
    }

    @AllArgsConstructor
    static class Node {
        String label;
        Set<Node> adjacent;
    }
}
