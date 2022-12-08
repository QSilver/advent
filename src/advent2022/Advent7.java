package advent2022;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Queues.newArrayDeque;

@Slf4j
public class Advent7 {
    public static void main(String[] args) {
        List<String> commands = Util.fileStream("advent2022/advent7").collect(Collectors.toList());

        Node root = new Node("/", null, true, 0);
        AtomicReference<Node> current = new AtomicReference<>(root);
        AtomicBoolean outputMode = new AtomicBoolean(false);

        commands.forEach(command -> {
            if (command.startsWith("$")) {
                outputMode.set(false);
                String[] split = command.split(" ");
                if (split[1].equals("cd")) {
                    if (split[2].equals("/")) {
                        current.set(root);
                    } else if (split[2].equals("..")) {
                        current.set(current.get().parent);
                    } else {
                        current.set(current.get().subnodes.get(split[2]));
                    }
                } else if (split[1].equals("ls")) {
                    outputMode.set(true);
                }
            } else if (outputMode.get()) {
                String[] split = command.split(" ");
                if (split[0].equals("dir")) {
                    Node newSubdir = new Node(split[1], current.get(), true, 0);
                    current.get().subnodes.put(split[1], newSubdir);
                } else {
                    Node file = new Node(split[1], current.get(), false, Long.parseLong(split[0]));
                    current.get().subnodes.put(split[1], file);
                    log.info("Found file {} of size {}", split[1], split[0]);
                }
            }
        });

        root.calculateSize();

        part1(root);
        part2(root);
    }

    private static void part2(Node root) {
        long unused = 70000000 - root.size;
        log.info("Unused Space {}", unused);
        long needed = 30000000 - unused;
        log.info("Needed Space {}", needed);

        long minSize = Long.MAX_VALUE;
        Queue<Node> nodes = newArrayDeque();
        nodes.add(root);

        while (!nodes.isEmpty()) {
            Node top = nodes.poll();
            if (top.size > needed && top.size < minSize) {
                minSize = top.size;
            }
            nodes.addAll(top.subnodes.values());
        }
        log.info("Smallest to delete {}", minSize);
    }

    private static void part1(Node root) {
        Queue<Node> nodes = newArrayDeque();
        nodes.add(root);

        long oversizedSum = 0;
        while (!nodes.isEmpty()) {
            Node top = nodes.poll();
            if (top.size < 100000) {
                oversizedSum += top.size;
            }
            nodes.addAll(top.subnodes.values());
        }
        log.info("Oversized Sum {}", oversizedSum);
    }

    static class Node {
        String identifier;
        boolean isDirectory;
        Node parent;
        Map<String, Node> subnodes;
        long size;

        public Node(String identifier, Node parent, boolean isDirectory, long size) {
            this.identifier = identifier;
            this.parent = parent;
            this.isDirectory = isDirectory;
            this.subnodes = newHashMap();
            this.size = size;
        }

        public long calculateSize() {
            this.size = subnodes.values().stream().mapToLong(node -> node.size).sum() +
                    subnodes.values().stream().mapToLong(Node::calculateSize).sum();
            return size;
        }

        @Override
        public String toString() {
            return "Node {" + identifier + "}, " + isDirectory + subnodes + " - " + size;
        }
    }
}
