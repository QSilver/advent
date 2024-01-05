package advent2019;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import util.InputUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

@Slf4j
public class Advent15 {
    public static final int SIZE = 41;
    private static final int[][] map = new int[SIZE][SIZE];

    static List<Computer> robots = newArrayList();
    static List<Point> robotPositions = newArrayList();
    static List<Integer> robotDirections = newArrayList();
    static List<Computer> robotsToRemove = newArrayList();
    static Point start = new Point(SIZE / 2 + 1, SIZE / 2 + 1, 0);
    static Point oxygen;
    static final ArrayList<Long> program = InputUtils.splitLine(InputUtils.fileStream("advent2019/advent15"))
                                               .stream()
                                               .map(Long::parseLong)
                                               .collect(Collectors.toCollection(Lists::newArrayList));

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        spawn(0, start, new Computer(program));

        List<Computer> robotsCopy = newArrayList(robots);
        while (!robots.isEmpty()) {
            robotsCopy.forEach(computer -> {
                int index = robots.indexOf(computer);
                robotsToRemove.add(computer);
                spawn(robotDirections.get(index), robotPositions.get(index), computer);
            });
            robotsToRemove.forEach(computer -> {
                int index = robots.indexOf(computer);
                if (index > -1) {
                    robots.remove(index);
                    robotDirections.remove(index);
                    robotPositions.remove(index);
                }
            });
            robotsToRemove = newArrayList();
            robotsCopy = newArrayList(robots);
        }

        BFSSolver.BFS(map, start, oxygen);
        BFSSolver.BFS(map, oxygen);
    }

    static void spawn(int previous, Point position, Computer computer) {
        final int ignore = getIgnore(previous);
        IntStream.of(1, 2, 3, 4)
                 .filter(direction -> direction != ignore)
                 .forEach(direction -> {
                     Computer robot = new Computer(computer.getMemory());
                     robot.addInput(direction);
                     robot.solve();
                     int output = (int) robot.getOutput();

                     if (processOutput(direction, output, position)) {
                         robots.add(robot);
                         robotDirections.add(direction);
                     }
                 });
    }

    private static int getIgnore(int previous) {
        switch (previous) {
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 4;
            case 4:
                return 3;
            default:
                return 0;
        }
    }

    private static boolean processOutput(int move, int output, Point position) {
        int tempX = 0;
        int tempY = 0;
        switch (move) {
            case 0:
                return false;
            case 1:
                tempY--;
                break;
            case 2:
                tempY++;
                break;
            case 3:
                tempX--;
                break;
            case 4:
                tempX++;
                break;
        }
        switch (output) {
            case 0:
                map[position.y + tempY][position.x + tempX] = -1;
                return false;
            case 2:
                oxygen = new Point(position.x + tempX, position.y + tempY, 0);
        }
        robotPositions.add(new Point(position.x + tempX, position.y + tempY, 0));
        return true;
    }

    static void draw() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (x == start.x && y == start.y) {
                    System.out.print("X");
                } else if (oxygen != null && x == oxygen.x && y == oxygen.y) {
                    System.out.print("O");
                } else {
                    switch (map[y][x]) {
                        case 0:
                            System.out.print(" ");
                            break;
                        case -1:
                            System.out.print("▓");
                            break;
                        default:
                            System.out.print(map[y][x] % 10);
                    }
                }
            }
            System.out.println();
        }
        System.out.println(IntStream.range(0, SIZE)
                                    .mapToObj(i -> "=")
                                    .collect(Collectors.joining()));
    }
}

@Slf4j
class BFSSolver {
    private static final int[][] mapCopy = new int[Advent15.SIZE][Advent15.SIZE];

    public static void BFS(int[][] map, Point start, Point end) {
        for (int i = 0; i < Advent15.SIZE; i++) {
            System.arraycopy(map[i], 0, mapCopy[i], 0, map.length);
        }
        fill(start, 0);
        log.info("Shortest path = {}", map[end.y][end.x]);
        Advent15.draw();
    }

    public static void BFS(int[][] map, Point start) {
        for (int i = 0; i < Advent15.SIZE; i++) {
            System.arraycopy(map[i], 0, mapCopy[i], 0, map.length);
        }
        fill(start, 0);

        OptionalInt max = Arrays.stream(mapCopy)
                                .map(Arrays::stream)
                                .map(IntStream::max)
                                .mapToInt(OptionalInt::getAsInt)
                                .max();

        log.info("Max Value = {}", max.getAsInt());
        Advent15.draw();
    }

    private static void fill(Point toFill, int number) {
        mapCopy[toFill.y][toFill.x] = number;
        if (mapCopy[toFill.y][toFill.x - 1] == 0) {
            fill(new Point(toFill.x - 1, toFill.y, 0), number + 1);
        }
        if (mapCopy[toFill.y][toFill.x + 1] == 0) {
            fill(new Point(toFill.x + 1, toFill.y, 0), number + 1);
        }
        if (mapCopy[toFill.y - 1][toFill.x] == 0) {
            fill(new Point(toFill.x, toFill.y - 1, 0), number + 1);
        }
        if (mapCopy[toFill.y + 1][toFill.x] == 0) {
            fill(new Point(toFill.x, toFill.y + 1, 0), number + 1);
        }
    }
}