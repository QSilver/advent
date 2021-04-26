package advent2020;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static advent2020.Advent23.CUPS;
import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent23 {
    private static final String INPUT = "215694783";
    private static final int MOVES = 10000000;
    static final int CUPS = 1000000;

    public static void main(String[] args) {
        CircularLinkedList cups = new CircularLinkedList();

        Arrays.stream(INPUT.split(""))
              .map(Integer::parseInt)
              .forEach(cups::addNode);

        IntStream.range(10, CUPS + 1)
                 .forEach(cups::addNode);

        for (int m = 0; m < MOVES; m++) {
            if (m % 10000 == 0) {
                log.info("{}% - {}", m / 100000.0, m);
            }
            cups.shuffle();
        }

        Node one = cups.getNodeByValue(1);
        log.info("{} * {} = {}", one.nextNode.value, one.nextNode.nextNode.value, BigInteger.valueOf(one.nextNode.value)
                                                                                            .multiply(BigInteger.valueOf(one.nextNode.nextNode.value)));
    }
}

class Node {
    int value;
    Node nextNode;

    public Node(int value) {
        this.value = value;
    }
}

class CircularLinkedList {
    private Node head;
    private Node tail;
    private Node pointer;

    private final Map<Integer, Node> lookup = new HashMap<>();

    public void addNode(int value) {
        Node newNode = new Node(value);
        lookup.put(value, newNode);

        if (head == null) {
            head = newNode;
            pointer = head;
        } else {
            tail.nextNode = newNode;
        }

        tail = newNode;
        tail.nextNode = head;
    }

    public void shuffle() {
        Node current = getNodeByDelta(pointer, 0);

        ArrayList<Integer> removed = newArrayList(
                getNodeByDelta(current, 1).value,
                getNodeByDelta(current, 2).value,
                getNodeByDelta(current, 3).value);

        Node moved = current.nextNode;
        current.nextNode = getNodeByDelta(current, 4);

        int toMove = current.value - 1;
        if (toMove == 0) {
            toMove = CUPS;
        }
        while (removed.contains(toMove)) {
            toMove--;
            if (toMove == 0) {
                toMove = CUPS;
            }
        }

        Node destination = getNodeByValue(toMove);
        Node destinationPlus1 = destination.nextNode;

        destination.nextNode = moved;
        getNodeByDelta(moved, 2).nextNode = destinationPlus1;

        this.pointer = current.nextNode;
    }

    public Node getNodeByDelta(Node fromPos, int delta) {
        Node current = fromPos;
        while (delta-- > 0) {
            current = current.nextNode;
        }
        return current;
    }

    public Node getNodeByValue(int searchValue) {
        return lookup.get(searchValue);
    }
}
