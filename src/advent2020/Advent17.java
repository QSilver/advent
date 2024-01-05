package advent2020;

import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Advent17 {
    private static final int SIZE = 31;
    private static final int OFFSET = SIZE / 2 - 1;
    static int[][][][] pocketDimension = new int[SIZE][SIZE][SIZE][SIZE];
    static int[][][][] oldState = new int[SIZE][SIZE][SIZE][SIZE];

    public static void main(String[] args) {
        List<String> input = InputUtils.fileStream("advent2020/advent17")
                                 .collect(Collectors.toList());

        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.get(i)
                                     .length(); j++) {
                pocketDimension[SIZE / 2][SIZE / 2][i + OFFSET][j + OFFSET] = input.get(i)
                                                                                   .charAt(j) == '#' ? 1 : 0;
            }
        }

        for (int cycle = 0; cycle < 6; cycle++) {
            cycle();
        }
        //print();
        count();
    }

    private static void cycle() {
        oldState = copy();
        for (int hyper = 1; hyper < SIZE - 1; hyper++) {
            for (int layer = 1; layer < SIZE - 1; layer++) {
                for (int i = 1; i < SIZE - 1; i++) {
                    for (int j = 1; j < SIZE - 1; j++) {
                        assignValue(hyper, layer, i, j);
                    }
                }
            }
        }
    }

    private static void assignValue(int hyper, int layer, int i, int j) {
        int cube = oldState[hyper][layer][i][j];
        int n = countNeighbours(i, j, layer, hyper);
        if (cube == 0 && n == 3) {
            pocketDimension[hyper][layer][i][j] = 1;
        }
        if (cube == 1) {
            if (n == 2 || n == 3) {
                pocketDimension[hyper][layer][i][j] = 1;
            } else {
                pocketDimension[hyper][layer][i][j] = 0;
            }
        }
    }

    private static void print() {
        for (int layer = 0; layer < SIZE; layer++) {
            log.info("-----------Layer-{}-----------", layer);
            for (int i = 0; i < SIZE; i++) {
                int finalLayer = layer;
                int finalI = i;
                String hyperLine = IntStream.range(0, SIZE)
                                            .mapToObj(hyper -> Arrays.stream(pocketDimension[hyper][finalLayer][finalI])
                                                                     .mapToObj(operand -> operand == 1 ? "#" : ".")
                                                                     .collect(Collectors.joining()))
                                            .collect(Collectors.joining("    "));
                log.info("{}", hyperLine);
            }
        }
    }

    private static void count() {
        int count = 0;
        for (int hyper = 0; hyper < SIZE; hyper++) {
            for (int layer = 0; layer < SIZE; layer++) {
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        count += pocketDimension[hyper][layer][i][j];
                    }
                }
            }
        }
        log.info("Active Cubes: {}", count);
    }

    private static int countNeighbours(int i, int j, int layer, int hyper) {
        int neighbours = 0;
        for (int hyperOffset = -1; hyperOffset <= 1; hyperOffset++) {
            for (int layerOffset = -1; layerOffset <= 1; layerOffset++) {
                for (int ii = -1; ii <= 1; ii++) {
                    for (int jj = -1; jj <= 1; jj++) {
                        if (!(hyperOffset == 0 && layerOffset == 0 && ii == 0 && jj == 0)) {
                            neighbours += oldState[hyper + hyperOffset][layer + layerOffset][i + ii][j + jj];
                        }
                    }
                }
            }
        }
        return neighbours;
    }

    private static int[][][][] copy() {
        int[][][][] copy = new int[SIZE][SIZE][SIZE][SIZE];
        for (int k = 0; k < SIZE; k++) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    System.arraycopy(pocketDimension[k][i][j], 0, copy[k][i][j], 0, SIZE);
                }
            }
        }
        return copy;
    }
}
