package advent2020;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class Advent11 {

    static char[][] seatMap = new char[97][97];
    //static char[][] seatMap = new char[13][13];
    static char[][] oldMap = new char[97][97];
    //static char[][] oldMap = new char[13][13];

    @SneakyThrows
    public static void main(String[] args) {
        AtomicInteger row = new AtomicInteger(0);
        InputUtils.fileStream("advent2020/advent11")
            .forEach(line -> {
                AtomicInteger col = new AtomicInteger(0);
                line.chars()
                    .forEach(c -> seatMap[row.get()][col.incrementAndGet()] = (char) c);
                row.incrementAndGet();
            });

        for (int i = 0; i < seatMap.length - 1; i++) {
            log.info(String.valueOf(seatMap[i]));
        }
        log.info("");

        while (true) {
            int seatHash = Arrays.stream(seatMap)
                                 .map(chars -> String.valueOf(String.valueOf(chars)
                                                                    .hashCode()))
                                 .collect(Collectors.joining())
                                 .hashCode();
            int oldHash = Arrays.stream(oldMap)
                                .map(chars -> String.valueOf(String.valueOf(chars)
                                                                   .hashCode()))
                                .collect(Collectors.joining())
                                .hashCode();
            log.info(seatHash + "");
            log.info(oldHash + "");
            if (seatHash == oldHash) {
                break;
            }


            for (int i = 0; i < seatMap.length; i++) {
                System.arraycopy(seatMap[i], 0, oldMap[i], 0, seatMap[i].length);
            }

            for (int i = 1; i < seatMap.length - 1; i++) {
                for (int j = 1; j < seatMap[i].length - 1; j++) {
                    int n = countNeighbours(i, j);
                    if (oldMap[i][j] == 'L' && n == 0) {
                        seatMap[i][j] = '#';
                    } else if (oldMap[i][j] == '#' && n >= 5) {
                        seatMap[i][j] = 'L';
                    }
                }
            }

            for (int i = 0; i < seatMap.length - 1; i++) {
                log.info(String.valueOf(seatMap[i]));
            }

            //Thread.sleep(5000);
        }
        log.info("Done");

        int occupied = 0;
        for (int i = 1; i < seatMap.length - 1; i++) {
            for (int j = 1; j < seatMap[i].length - 1; j++) {
                if (seatMap[i][j] == '#') {
                    occupied++;
                }
            }
        }
        log.info("Occupied Seats: {}", occupied);
    }

    public static int countNeighbours(int i, int j) {
        int neighbours = 0;

        for (int row = i - 1; row > 0; row--) {
            if (oldMap[row][j] == '.') {
                continue;
            }
            if (oldMap[row][j] == '#') {
                neighbours += 1;
            }
            break;
        }

        for (int row = i + 1; row < seatMap.length - 1; row++) {
            if (oldMap[row][j] == '.') {
                continue;
            }
            if (oldMap[row][j] == '#') {
                neighbours += 1;
            }
            break;
        }

        for (int col = j - 1; col > 0; col--) {
            if (oldMap[i][col] == '.') {
                continue;
            }
            if (oldMap[i][col] == '#') {
                neighbours += 1;
            }
            break;
        }

        for (int col = j + 1; col < seatMap[i].length - 1; col++) {
            if (oldMap[i][col] == '.') {
                continue;
            }
            if (oldMap[i][col] == '#') {
                neighbours += 1;
            }
            break;
        }

        int diag1 = i - 1;
        int diag2 = j - 1;
        while (diag1 > 0 && diag2 > 0) {
            if (oldMap[diag1][diag2] == '.') {
                diag1--;
                diag2--;
                continue;
            }
            if (oldMap[diag1][diag2] == '#') {
                neighbours += 1;
            }
            break;
        }

        diag1 = i + 1;
        diag2 = j + 1;
        while (diag1 < seatMap.length - 1 && diag2 < seatMap[i].length - 1) {
            if (oldMap[diag1][diag2] == '.') {
                diag1++;
                diag2++;
                continue;
            }
            if (oldMap[diag1][diag2] == '#') {
                neighbours += 1;
            }
            break;
        }

        diag1 = i + 1;
        diag2 = j - 1;
        while (diag1 < seatMap.length - 1 && diag2 > 0) {
            if (oldMap[diag1][diag2] == '.') {
                diag1++;
                diag2--;
                continue;
            }
            if (oldMap[diag1][diag2] == '#') {
                neighbours += 1;
            }
            break;
        }

        diag1 = i - 1;
        diag2 = j + 1;
        while (diag1 > 0 && diag2 < seatMap[i].length - 1) {
            if (oldMap[diag1][diag2] == '.') {
                diag1--;
                diag2++;
                continue;
            }
            if (oldMap[diag1][diag2] == '#') {
                neighbours += 1;
            }
            break;
        }

        return neighbours;
    }
}
