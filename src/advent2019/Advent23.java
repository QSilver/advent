package advent2019;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;

@Slf4j
public class Advent23 {
    private static Computer[] network = new Computer[50];
    private static boolean[] networkIdle = new boolean[50];
    private static ArrayList<Queue<Packet>> inputQueue = newArrayList();

    private static Packet NAT;

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Long> program = Util.splitLine(Util.fileStream("advent23"))
                                      .stream()
                                      .map(Long::parseLong)
                                      .collect(Collectors.toCollection(Lists::newArrayList));

        for (int node = 0; node < 50; node++) {
            network[node] = new Computer(program);
            network[node].addInput(node);
            LinkedList<Packet> e = newLinkedList();
            e.add(new Packet(-1, -1));
            inputQueue.add(e);
        }

        int step = 0;
        long prevY = -1;
        while (step != 2000) {
            log.info("{}", step++);
            if (IntStream.range(0, networkIdle.length)
                         .allMatch(i -> networkIdle[i]) && inputQueue.stream()
                                                                     .allMatch(Collection::isEmpty)) {
                inputQueue.get(0)
                          .add(new Packet(NAT.x, NAT.y));
                networkIdle[0] = false;
                log.info("NAT sending to Node 0, X: {}, Y: {}", NAT.x, NAT.y);

                if (prevY == NAT.y) {
                    log.info("DUPLICATE VALUE {}", NAT.y);
                    return;
                }

                prevY = NAT.y;
            }

            for (int node = 0; node < 50; node++) {
                Packet peek = inputQueue.get(node)
                                        .peek();
                if (peek != null) {
                    network[node].addInput(peek.x);
                    network[node].addInput(peek.y);
                    inputQueue.get(node)
                              .remove();
                    networkIdle[node] = false;
                }

                network[node].solve();
                networkIdle[node] = network[node].isPaused();

                while (network[node].hasOutput()) {
                    long address = network[node].getOutput();
                    long x = network[node].getOutput();
                    long y = network[node].getOutput();

                    if (address == 255) {
                        NAT = new Packet(x, y);
                        log.info("Node {} sending to NAT, X: {}, Y: {}", node, x, y);
                        continue;
                    }

                    inputQueue.get((int) address)
                              .add(new Packet(x, y));
                    networkIdle[(int) address] = false;
                    log.info("Node {} sending to Node {}, X: {}, Y: {}", node, address, x, y);
                }
            }
        }
    }
}

@AllArgsConstructor
class Packet {
    long x;
    long y;
}