package advent2019;

import util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class Advent6 {

    public static void main(String[] args) {
        System.out.println(solve());
    }

    private static int solve() {
        List<String> strings = Util.fileStream("advent6")
                                   .collect(Collectors.toList());

        Set<Node> danglingRoots = newHashSet();

        Node root = new Node("COM", null);
        strings.forEach(s -> {
            ArrayList<String> orbitPair = newArrayList(s.split("\\)"));
            processNode(danglingRoots, root, new Node(orbitPair.get(1), orbitPair.get(0)), newHashSet());
        });

        while (!danglingRoots.isEmpty()) {
            HashSet<Node> toRemove = newHashSet();
            danglingRoots.forEach(node -> processNode(danglingRoots, root, node, toRemove));
            danglingRoots.removeAll(toRemove);
        }

        System.out.println(calculateDepth(root, 0));

        Node you = getNodeFromTree(root, "YOU");
        Node santa = getNodeFromTree(root, "SAN");

        Node ancestor = lowestCommonAncestor(you, santa);
        return distanceToParent(you, ancestor) + distanceToParent(santa, ancestor);
    }

    private static int distanceToParent(Node child, Node parent) {
        int distance = -1;
        while (child != parent) {
            distance++;
            child = child.root;
        }
        return distance;
    }

    private static Node lowestCommonAncestor(Node node1, Node node2) {
        List<Node> node1OrbitMap = ancestors(node1).stream()
                                                   .filter(Objects::nonNull)
                                                   .collect(Collectors.toList());
        List<Node> node2OrbitMap = ancestors(node2).stream()
                                                   .filter(Objects::nonNull)
                                                   .collect(Collectors.toList());
        Collections.reverse(node1OrbitMap);
        Collections.reverse(node2OrbitMap);

        int i = 0;
        while (node1OrbitMap.get(i).name.equals(node2OrbitMap.get(i).name)) {
            i++;
        }
        return node1OrbitMap.get(i - 1);
    }

    private static List<Node> ancestors(Node node) {
        List<Node> ancestors = newArrayList();
        while (node != null) {
            ancestors.add(node.root);
            node = node.root;
        }
        return ancestors;
    }

    private static int calculateDepth(Node root, int depth) {
        if (root == null) {
            return depth;
        }
        return depth + root.children.stream()
                                    .map(node -> calculateDepth(node, depth + 1))
                                    .mapToInt(value -> value)
                                    .sum();
    }

    public static void processNode(Set<Node> danglingRoots, Node root, Node child, Set<Node> toRemove) {
        Node parent = getNodeFromTree(root, child.rootName);
        if (parent != null) {
            parent.addChild(child);
            child.root = parent;
            toRemove.add(child);
        } else {
            danglingRoots.add(child);
        }
    }

    private static Node getNodeFromTree(Node root, String toFind) {
        if (root != null) {
            if (root.name.equals(toFind)) {
                return root;
            } else {
                for (Node child : root.children) {
                    Node nodeFromTree = getNodeFromTree(child, toFind);
                    if (nodeFromTree != null && nodeFromTree.name.equals(toFind)) {
                        return nodeFromTree;
                    }
                }
            }
        }
        return null;
    }
}

class Node {
    Node root;
    String name;
    String rootName;
    Set<Node> children = newHashSet();

    Node(String name, String rootName) {
        this.name = name;
        this.rootName = rootName;
    }

    @Override
    public String toString() {
        return "Node{" + "name='" + name + '\'' + ", rootName='" + rootName + '\'' + ", children=" + children + '}';
    }

    void addChild(Node child) {
        children.add(child);
    }
}
