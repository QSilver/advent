package advent2019;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class Advent19 {
    private static final int SIZE = 2000;
    private static char[][] map = new char[SIZE][SIZE];

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Long> program = Util.splitLine(Util.fileStream("advent19"))
                                      .stream()
                                      .map(Long::parseLong)
                                      .collect(Collectors.toCollection(Lists::newArrayList));

        for (int row1 = 0; row1 < SIZE; row1++) {
            for (int col = 117 * row1 / 200; col <= 137 * row1 / 200 + 1; col++) {
                Computer drone = new Computer(program);
                drone.addInput(col);
                drone.addInput(row1);
                drone.solve();
                long output = drone.getOutput();

                if (output == 1) {
                    map[row1][col] = '#';
                } else {
                    map[row1][col] = '.';
                }
            }
            log.info("Generating {}", row1);
        }

        int line = 0;
        while (true) {
            int lastHash = Arrays.toString(map[line])
                    .replace(" ", "")
                    .replace(",", "")
                    .replace(".,", ".")
                    .replace("#,", "#").lastIndexOf('#');

            if (lastHash > 100 && map[line][lastHash - 100] == '#' && map[line + 99][lastHash - 100] == '#') {
                log.info("Solution {}", (lastHash - 100) * 10000 + line);
                return;
            }
            line++;

        }
    }
}