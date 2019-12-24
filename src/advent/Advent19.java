package advent;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Advent19 {
    private static final int LOWER_BOUND = 1700;
    private static final int UPPER_BOUND = 1850;
    private static char[][] map = new char[UPPER_BOUND][UPPER_BOUND];

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Long> program = Util.splitLine(Util.fileStream("advent19"))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toCollection(Lists::newArrayList));

        for (int row1 = LOWER_BOUND; row1 < UPPER_BOUND; row1++) {
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
        }

        int line = LOWER_BOUND;
        while (true) {
            int lastHash = Arrays.toString(map[line])
                    .replace(" ", "")
                    .replace(",", "")
                    .replace(".,", ".")
                    .replace("#,", "#").lastIndexOf('#');

            if (map[line][lastHash - 100] == '#' && map[line + 99][lastHash - 100] == '#') {
                System.out.println("SOLUTION: " + line + " hash: " + (lastHash - 100));
                break;
            } else {
                line++;
            }
        }
    }
}