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

    public static void main(String[] args) {
        int[][] octopusMap = new int[SIZE][SIZE];
        boolean[][] hasFlashed = new boolean[SIZE][SIZE];

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
        int globalSum = 0;
        while (globalSum != SIZE * SIZE) {
            globalSum = 0;
            Queue<Advent5.Point> next = newLinkedList();
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    octopusMap[r][c]++;
                    hasFlashed[r][c] = false;
                    if (octopusMap[r][c] > 9) {
                        flashes++;
                        hasFlashed[r][c] = true;
                        next.add(new Advent5.Point(r, c));
                    }
                }
            }

            while (!next.isEmpty()) {
                Advent5.Point poll = next.poll();
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (poll.X + i >= 0 && poll.X + i < SIZE && poll.Y + j >= 0 && poll.Y + j < SIZE) {
                            if (!(i == 0 && j == 0)) {
                                octopusMap[poll.X + i][poll.Y + j]++;
                                if (octopusMap[poll.X + i][poll.Y + j] > 9 && !hasFlashed[poll.X + i][poll.Y + j]) {
                                    flashes++;
                                    hasFlashed[poll.X + i][poll.Y + j] = true;
                                    next.add(new Advent5.Point(poll.X + i, poll.Y + j));
                                }
                            }
                        }
                    }
                }
            }

            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (hasFlashed[r][c]) {
                        globalSum++;
                        octopusMap[r][c] = 0;
                    }
                }
            }

            step++;
            log.info("After step {} - {} flashes", step, flashes);
        }
    }
}
