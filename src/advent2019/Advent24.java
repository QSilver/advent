package advent2019;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent24 {
    private static ArrayList<Grid> layers = newArrayList();

    public static void main(String[] args) {
        List<String> advent24 = InputUtils.fileStream("advent2019/advent24")
                                    .collect(Collectors.toList());

        boolean[][] startingMap = new boolean[5][5];
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (advent24.get(row)
                            .charAt(col) == '#') {
                    startingMap[row][col] = true;
                }
            }
        }
        layers.add(new Grid(startingMap));

        int step = 0;
        while (step++ < 200) {
            if (layers.get(0)
                      .isNotEmpty()) {
                layers.add(0, new Grid(new boolean[7][7]));
            }

            if (layers.get(layers.size() - 1)
                      .isNotEmpty()) {
                layers.add(new Grid(new boolean[7][7]));
            }

            ArrayList<Grid> layersCopy = newArrayList();
            for (int grid = 0; grid < layers.size(); grid++) {
                layersCopy.add(processGrid(
                        grid == 0 ? null : layers.get(grid - 1),
                        layers.get(grid),
                        grid == layers.size() - 1 ? null : layers.get(grid + 1)
                ));
            }
            layers = layersCopy;

            log.info("Bugs at min {} : {}", step, layers.stream()
                                                        .map(Grid::count)
                                                        .mapToInt(value -> value)
                                                        .sum());
        }
    }

    private static Grid processGrid(Grid outerGrid, Grid grid, Grid innerGrid) {
        boolean[][] newMap = new boolean[5][5];

        boolean[][] outer = outerGrid == null ? new boolean[5][5] : outerGrid.map;
        boolean[][] inner = innerGrid == null ? new boolean[5][5] : innerGrid.map;
        boolean[][] current = grid.map;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int adjacent = calculateAdjacent(outer, current, inner, row, col);
                newMap[row][col] = newBug(current[row][col], adjacent);
            }
        }

        return new Grid(newMap);
    }

    private static int calculateAdjacent(boolean[][] outer, boolean[][] current, boolean[][] inner, int row, int col) {
        if (row == 2 && col == 2) {
            return 0;
        }

        int top = row == 0 ? (outer[1][2] ? 1 : 0) : current[row - 1][col] ? 1 : 0;
        int bottom = row == 4 ? (outer[3][2] ? 1 : 0) : current[row + 1][col] ? 1 : 0;
        int left = col == 0 ? (outer[2][1] ? 1 : 0) : current[row][col - 1] ? 1 : 0;
        int right = col == 4 ? (outer[2][3] ? 1 : 0) : current[row][col + 1] ? 1 : 0;

        if (row == 1 && col == 2) {
            bottom = 0;
            for (int i = 0; i < 5; i++) {
                bottom += inner[0][i] ? 1 : 0;
            }
        } else if (row == 3 && col == 2) {
            top = 0;
            for (int i = 0; i < 5; i++) {
                top += inner[4][i] ? 1 : 0;
            }
        } else if (row == 2 && col == 1) {
            right = 0;
            for (int i = 0; i < 5; i++) {
                right += inner[i][0] ? 1 : 0;
            }
        } else if (row == 2 && col == 3) {
            left = 0;
            for (int i = 0; i < 5; i++) {
                left += inner[i][4] ? 1 : 0;
            }
        }

        return top + bottom + left + right;
    }

    private static boolean newBug(boolean current, int adjacent) {
        if (current) {
            if (adjacent != 1) {
                return false;
            }
        } else {
            if (adjacent == 1 || adjacent == 2) {
                return true;
            }
        }

        return current;
    }
}

@AllArgsConstructor
class Grid {
    boolean[][] map;

    public boolean isNotEmpty() {
        return IntStream.range(0, map.length)
                        .anyMatch(j -> IntStream.range(0, map[j].length)
                                                .anyMatch(i -> map[j][i]));
    }

    public int count() {
        return (int) Arrays.stream(map)
                           .mapToInt(row -> (int) IntStream.range(0, row.length)
                                                           .mapToObj(value -> row[value])
                                                           .filter(aBoolean -> aBoolean)
                                                           .count())
                           .mapToLong(value -> value)
                           .sum();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (boolean[] booleans : map) {
            for (boolean b : booleans) {
                stringBuilder.append(b ? "#" : ".");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}