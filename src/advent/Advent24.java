package advent;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class Advent24 {
    private static boolean[][] map = new boolean[7][7];
    private static Set<Long> biodiversity = newHashSet();

    public static void main(String[] args) {
        List<String> advent24 = Util.fileStream("advent24")
                                    .collect(Collectors.toList());

        for (int row = 0; row < advent24.size(); row++) {
            for (int col = 0; col < advent24.get(row)
                                            .length(); col++) {
                if (advent24.get(row)
                            .charAt(col) == '#') {
                    map[row + 1][col + 1] = true;
                }
            }
        }

        while (true) {
            boolean[][] mapCopy = new boolean[7][7];
            draw();
            for (int row = 1; row <= 5; row++) {
                for (int col = 1; col <= 5; col++) {
                    mapCopy[row][col] = newBug(row, col);
                }
            }
            map = mapCopy;

            long mapBiodiversity = calculateBiodiversity();
            if (biodiversity.contains(mapBiodiversity)) {
                log.info("REPEATING : {}", mapBiodiversity);
                draw();
                return;
            }
            biodiversity.add(mapBiodiversity);
        }
    }

    private static void draw() {
        for (int row = 0; row <= 6; row++) {
            for (int col = 0; col <= 6; col++) {
                System.out.print(map[row][col] ? "#" : ".");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static long calculateBiodiversity() {
        long biodiversity = 0L;
        for (int row = 1; row <= 5; row++) {
            for (int col = 1; col <= 5; col++) {
                biodiversity += map[row][col] ? Math.pow(2, (col - 1) + 5 * (row - 1)) : 0;
            }
        }
        return biodiversity;
    }

    private static boolean newBug(int row, int col) {
        int adjacent = (map[row - 1][col] ? 1 : 0) + (map[row + 1][col] ? 1 : 0) + (map[row][col - 1] ? 1 : 0) + (map[row][col + 1] ? 1 : 0);

        if (map[row][col]) {
            if (adjacent != 1) {
                return false;
            }
        } else {
            if (adjacent == 1 || adjacent == 2) {
                return true;
            }
        }

        return map[row][col];
    }
}