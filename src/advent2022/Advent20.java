package advent2022;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import util.Util;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.floorMod;

// 1214599383 - too low
@Slf4j
public class Advent20 {
    private static final long DECRYPTION_KEY = 811589153L;

    public static void main(String[] args) {
        AtomicReference<Node> zero = new AtomicReference<>();
        List<Node> nodes = Util.fileStream("advent2022/advent20")
                .map(Long::parseLong)
                .map(value -> {
                    Node node = new Node(value);
                    if (value == 0) {
                        zero.set(node);
                    }
                    return node;
                })
                .collect(Collectors.toList());

        setLinks(nodes);
        mix(nodes);
        calculateSum(zero);

        setLinks(nodes);
        nodes.forEach(node -> node.value *= DECRYPTION_KEY);
        IntStream.range(0, 10).forEach(value -> mix(nodes));
        calculateSum(zero);
    }

    private static void calculateSum(AtomicReference<Node> zero) {
        long sum = 0;
        Node current = zero.get();
        for (int i = 0; i <= 3000; i++) {
            if (i % 1000 == 0) {
                sum += current.value;
            }
            current = current.next;
        }
        log.info("Sum: {}", sum);
    }

    private static void mix(List<Node> nodes) {
        nodes.stream()
                .map(node -> new Pair<>(node, node.value % (nodes.size() - 1)))
                .filter(pair -> pair.getSecond() != 0)
                .forEach(pair -> pair.getFirst().process(pair.getSecond()));
    }

    private static void setLinks(List<Node> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).prev = nodes.get(floorMod(i - 1, nodes.size()));
            nodes.get(i).next = nodes.get(floorMod(i + 1, nodes.size()));
        }
    }

    static class Node {
        Node prev;
        long value;
        Node next;

        public Node(long value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value + "";
        }

        void process(long newPos) {
            this.remove();
            this.insertNodeAfter(this.getNewPosition(newPos));
        }

        void insertNodeAfter(Node newPrev) {
            this.next = newPrev.next;
            this.prev = newPrev;
            this.next.prev = this;
            this.prev.next = this;
        }

        Node getNewPosition(long newPos) {
            Node newNode = prev;
            if (this.value < 0) {
                for (long i = 0; i > newPos; i--) {
                    newNode = newNode.prev;
                }
            } else {
                for (long i = 0; i < newPos; i++) {
                    newNode = newNode.next;
                }
            }
            return newNode;
        }

        void remove() {
            this.prev.next = this.next;
            this.next.prev = this.prev;
        }
    }
}