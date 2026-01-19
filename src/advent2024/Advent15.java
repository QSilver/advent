package advent2024;

import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import util.Extensions;
import util.Point2D;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static util.InputUtils.readDoubleNewlineBlocks;

@Slf4j
@ExtensionMethod({Extensions.class})
public class Advent15 {
    // https://adventofcode.com/2024/day/15

    public Long runP1(String file) {
        String[] strings = readDoubleNewlineBlocks(file);
        Character[][] characters = loadCharMatrix(strings[0]);
        AtomicReference<Point2D> robot = new AtomicReference<>(findBot(characters));

        strings[1].chars().forEach(move -> {
            int row = (int) robot.get().row();
            int col = (int) robot.get().col();

            if (move == '^') {
                if (characters[row - 1][col] == '.') {
                    robot.set(new Point2D(row - 1, col));
                    characters[row - 1][col] = '@';
                    characters[row][col] = '.';
                } else if (characters[row - 1][col] == '#') {

                } else {
                    while (true) {
                        try {
                            if (characters[--row][col] == '.') {
                                characters[(int) robot.get().row()][(int) robot.get().col()] = '.';
                                robot.set(new Point2D(robot.get().row() - 1, col));
                                characters[row][col] = 'O';
                                characters[(int) robot.get().row()][(int) robot.get().col()] = '@';
                                break;
                            } else if (characters[row][col] == '#') {
                                break;
                            }
                        } catch (ArrayIndexOutOfBoundsException _) {
                            break;
                        }
                    }
                }
            } else if (move == 'v') {
                if (characters[row + 1][col] == '.') {
                    robot.set(new Point2D(row + 1, col));
                    characters[row + 1][col] = '@';
                    characters[row][col] = '.';
                } else if (characters[row + 1][col] == '#') {

                } else {
                    while (true) {
                        try {
                            if (characters[++row][col] == '.') {
                                characters[(int) robot.get().row()][(int) robot.get().col()] = '.';
                                robot.set(new Point2D(robot.get().row() + 1, col));
                                characters[row][col] = 'O';
                                characters[(int) robot.get().row()][(int) robot.get().col()] = '@';
                                break;
                            } else if (characters[row][col] == '#') {
                                break;
                            }
                        } catch (ArrayIndexOutOfBoundsException _) {
                            break;
                        }
                    }
                }
            } else if (move == '>') {
                if (characters[row][col + 1] == '.') {
                    robot.set(new Point2D(row, col + 1));
                    characters[row][col + 1] = '@';
                    characters[row][col] = '.';
                } else if (characters[row][col + 1] == '#') {

                } else {
                    while (true) {
                        try {
                            if (characters[row][++col] == '.') {
                                characters[(int) robot.get().row()][(int) robot.get().col()] = '.';
                                robot.set(new Point2D(row, (int) robot.get().col() + 1));
                                characters[row][col] = 'O';
                                characters[(int) robot.get().row()][(int) robot.get().col()] = '@';
                                break;
                            } else if (characters[row][col] == '#') {
                                break;
                            }
                        } catch (ArrayIndexOutOfBoundsException _) {
                            break;
                        }
                    }
                }
            } else if (move == '<') {
                if (characters[row][col - 1] == '.') {
                    robot.set(new Point2D(row, col - 1));
                    characters[row][col - 1] = '@';
                    characters[row][col] = '.';
                } else if (characters[row][col - 1] == '#') {

                } else {
                    while (true) {
                        try {
                            if (characters[row][--col] == '.') {
                                characters[(int) robot.get().row()][(int) robot.get().col()] = '.';
                                robot.set(new Point2D(row, robot.get().col() - 1));
                                characters[row][col] = 'O';
                                characters[(int) robot.get().row()][(int) robot.get().col()] = '@';
                                break;
                            } else if (characters[row][col] == '#') {
                                break;
                            }
                        } catch (ArrayIndexOutOfBoundsException _) {
                            break;
                        }
                    }
                }
            }
        });

        long sum = 0;
        for (int row = 0; row < characters.length; row++) {
            for (int col = 0; col < characters[row].length; col++) {
                if (characters[row][col] == 'O') {
                    sum += 100L * row + col;
                }
            }
        }

        return sum;
    }

    private static Character[][] loadCharMatrix(String inout) {
        List<String> list = inout.split("\n").stream().toList();

        Character[][] matrix = new Character[list.size()][list.getFirst().length()];
        for (int row = 0; row < list.size(); row++) {
            String s = list.get(row);
            for (int col = 0; col < s.length(); col++) {
                matrix[row][col] = s.charAt(col);
            }
        }
        return matrix;
    }

    @NonNull
    private static Point2D findBot(Character[][] map) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] == '@') {
                    return new Point2D(row, col);
                }
            }
        }
        log.error("Invalid Input");
        return new Point2D(-1, -1);
    }

    private static long COUNTER = 0;

    public Long runP2(String file, boolean displayOn) {
        String[] strings = readDoubleNewlineBlocks(file).stream()
                .map(s -> {
                    s = s.replace("#", "##");
                    s = s.replace("O", "[]");
                    s = s.replace(".", "..");
                    s = s.replace("@", "@.");
                    return s;
                })
                .toArray(String[]::new);

        List<Point2D> walls = get2DPoints(strings, '#');
        List<Point2D> cratesLeft = get2DPoints(strings, '[');
        List<Point2D> cratesRight = get2DPoints(strings, ']');
        final Point2D[] robot = {get2DPoints(strings, '@').getFirst()};

        display('X', walls, cratesLeft, robot[0], displayOn);
        strings[1].chars().forEach(move -> {
            if (move == 10) {
                // skip new lines
                return;
            }

            List<Point2D> cratesOnSameRow = cratesLeft.stream()
                    .filter(crateLeft -> crateLeft.row() == robot[0].row())
                    .collect(toList());

            if (move == '>') {
                if (!walls.contains(robot[0].RIGHT()) && !cratesLeft.contains(robot[0].RIGHT())) {
                    robot[0] = robot[0].RIGHT();
                } else if (cratesLeft.contains(robot[0].RIGHT())) {
                    Point2D track = robot[0].RIGHT();
                    while (true) {
                        if (cratesRight.contains(track) || cratesLeft.contains(track)) {
                        } else if (walls.contains(track)) {
                            display((char) move, walls, cratesLeft, robot[0], displayOn);
                            return;
                        } else {
                            break;
                        }
                        track = track.RIGHT();
                    }

                    Point2D finalTrack = track;
                    cratesOnSameRow.stream()
                            .filter(crate -> crate.col() > robot[0].col() && crate.col() < finalTrack.col())
                            .forEach(crate -> moveCrate(cratesLeft, cratesRight, crate, crate.RIGHT()));
                    robot[0] = robot[0].RIGHT();
                }
            }

            if (move == '<') {
                if (!walls.contains(robot[0].LEFT()) && !cratesRight.contains(robot[0].LEFT())) {
                    robot[0] = robot[0].LEFT();
                } else if (cratesRight.contains(robot[0].LEFT())) {
                    Point2D track = robot[0].LEFT();
                    while (true) {
                        if (cratesRight.contains(track) || cratesLeft.contains(track)) {
                        } else if (walls.contains(track)) {
                            display((char) move, walls, cratesLeft, robot[0], displayOn);
                            return;
                        } else {
                            break;
                        }
                        track = track.LEFT();
                    }

                    Point2D finalTrack = track;
                    cratesOnSameRow.stream()
                            .filter(crate -> crate.col() < robot[0].col() && crate.col() > finalTrack.col())
                            .forEach(crate -> moveCrate(cratesLeft, cratesRight, crate, crate.LEFT()));
                    robot[0] = robot[0].LEFT();
                }
            }

            if (move == '^') {
                if (!walls.contains(robot[0].UP()) && !cratesLeft.contains(robot[0].UP()) && !cratesRight.contains(robot[0].UP())) {
                    robot[0] = robot[0].UP();
                } else {
                    if (!walls.contains(robot[0].UP())) {
                        List<Point2D> toProcess = newArrayList(robot[0].UP());
                        Set<Point2D> toMove = newHashSet();

                        while (!toProcess.isEmpty()) {
                            Point2D current = toProcess.removeFirst();

                            if (cratesLeft.contains(current)) {
                                if (walls.contains(current.UP()) || walls.contains(current.RIGHT().UP())) {
                                    display((char) move, walls, cratesLeft, robot[0], displayOn);
                                    return;
                                }

                                toMove.add(current);
                                if (cratesLeft.contains(current.UP())) {
                                    toProcess.add(current.UP());
                                } else if (cratesRight.contains(current.UP())) {
                                    toProcess.add(current.UP().LEFT());
                                }
                                if (cratesLeft.contains(current.RIGHT().UP())) {
                                    toProcess.add(current.UP().RIGHT());
                                }
                            } else if (cratesRight.contains(current)) {
                                toProcess.add(current.LEFT());
                            }
                        }

                        toMove.forEach(crate -> moveCrate(cratesLeft, cratesRight, crate, crate.UP()));
                        robot[0] = robot[0].UP();
                    }
                }
            }

            if (move == 'v') {
                if (!walls.contains(robot[0].DOWN()) && !cratesLeft.contains(robot[0].DOWN()) && !cratesRight.contains(robot[0].DOWN())) {
                    robot[0] = robot[0].DOWN();
                } else {
                    if (!walls.contains(robot[0].DOWN())) {
                        List<Point2D> toProcess = newArrayList(robot[0].DOWN());
                        Set<Point2D> toMove = newHashSet();

                        while (!toProcess.isEmpty()) {
                            Point2D current = toProcess.removeFirst();

                            if (cratesLeft.contains(current)) {
                                if (walls.contains(current.DOWN()) || walls.contains(current.RIGHT().DOWN())) {
                                    display((char) move, walls, cratesLeft, robot[0], displayOn);
                                    return;
                                }

                                toMove.add(current);
                                if (cratesLeft.contains(current.DOWN())) {
                                    toProcess.add(current.DOWN());
                                } else if (cratesRight.contains(current.DOWN())) {
                                    toProcess.add(current.DOWN().LEFT());
                                }
                                if (cratesLeft.contains(current.DOWN().RIGHT())) {
                                    toProcess.add(current.DOWN().RIGHT());
                                }
                            } else if (cratesRight.contains(current)) {
                                toProcess.add(current.LEFT());
                            }
                        }

                        toMove.forEach(crate -> moveCrate(cratesLeft, cratesRight, crate, crate.DOWN()));
                        robot[0] = robot[0].DOWN();
                    }
                }
            }

            display((char) move, walls, cratesLeft, robot[0], displayOn);
        });

        return cratesLeft.stream()
                .mapToLong(crate -> crate.row() * 100 + crate.col())
                .sum();
    }

    private static void moveCrate(List<Point2D> cratesLeft, List<Point2D> cratesRight, Point2D crate, Point2D newCrate) {
        cratesLeft.remove(crate);
        cratesLeft.add(newCrate);

        cratesRight.remove(crate.RIGHT());
        cratesRight.add(newCrate.RIGHT());
    }

    private void display(char move, List<Point2D> walls, List<Point2D> cratesLeft, Point2D robot, boolean displayOn) {
        if (!displayOn) return;

        long rows = walls.stream().mapToLong(Point2D::row).max().getAsLong();
        long cols = walls.stream().mapToLong(Point2D::col).max().getAsLong();

        System.out.printf("%s %s%n", COUNTER++, move);
        for (int row = 0; row <= rows; row++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col <= cols; col++) {
                Point2D current = new Point2D(row, col);
                if (walls.contains(current)) {
                    sb.append('#');
                } else if (cratesLeft.contains(current)) {
                    sb.append("[]");
                    col++;
                } else if (current.equals(robot)) {
                    sb.append('@');
                } else {
                    sb.append('.');
                }
            }
            System.out.println(sb);
        }
        System.out.println();
    }

    private static List<Point2D> get2DPoints(String[] input, char point) {
        String[] map = input[0].split("\n");

        List<Point2D> points = newArrayList();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length(); col++) {
                if (map[row].charAt(col) == point) {
                    points.add(new Point2D(row, col));
                }
            }
        }
        return points;
    }
}
