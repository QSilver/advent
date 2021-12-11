package advent2021;

import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newLinkedList;

@Slf4j
public class Advent11 {
    private static final int SIZE = 10;
    private static final int[][] octopusMap = new int[SIZE][SIZE];
    private static final boolean[][] hasFlashed = new boolean[SIZE][SIZE];

    public static void main(String[] args) {
        AtomicInteger row = new AtomicInteger();
        Util.fileStream("advent2021/advent11")
            .forEach(s -> {
                String[] split = s.split("");
                AtomicInteger col = new AtomicInteger();
                Arrays.stream(split)
                      .forEach(c -> octopusMap[row.get()][col.getAndIncrement()] = Integer.parseInt(c));
                row.getAndIncrement();
            });

        int flashes = 0;
        int step = 0;
        while (true) {
            processNextToFlash(getNextToFlash());
            int newFlashes = countNewFlashes();
            flashes += newFlashes;

            step++;
            log.info("After step {} - {} flashes", step, flashes);

            if (newFlashes == SIZE * SIZE) {
                break;
            }
        }
    }

    private static Queue<Advent5.Point> getNextToFlash() {
        Queue<Advent5.Point> next = newLinkedList();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Advent11.octopusMap[r][c]++;
                if (Advent11.octopusMap[r][c] > 9) {
                    Advent11.hasFlashed[r][c] = true;
                    next.add(new Advent5.Point(r, c));
                }
            }
        }
        return next;
    }

    private static void processNextToFlash(Queue<Advent5.Point> next) {
        while (!next.isEmpty()) {
            Advent5.Point poll = next.poll();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (isNeighbour(poll, i, j)) {
                        Advent11.octopusMap[poll.X + i][poll.Y + j]++;
                        if (Advent11.octopusMap[poll.X + i][poll.Y + j] > 9 && !Advent11.hasFlashed[poll.X + i][poll.Y + j]) {
                            Advent11.hasFlashed[poll.X + i][poll.Y + j] = true;
                            next.add(new Advent5.Point(poll.X + i, poll.Y + j));
                        }
                    }
                }
            }
        }
    }

    private static int countNewFlashes() {
        int flashes = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (Advent11.hasFlashed[r][c]) {
                    flashes++;
                    Advent11.octopusMap[r][c] = 0;
                    Advent11.hasFlashed[r][c] = false;
                }
            }
        }
        return flashes;
    }

    private static boolean isNeighbour(Advent5.Point poll, int i, int j) {
        return poll.X + i >= 0 && poll.X + i < SIZE && poll.Y + j >= 0 && poll.Y + j < SIZE && !(i == 0 && j == 0);
    }
}
