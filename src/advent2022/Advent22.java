package advent2022;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

// 29364 - too low
@Slf4j
public class Advent22 {
    static Map<Point, Point> map = newHashMap();
    static Point playerPosition;
    public static Facing playerFacing;

    public static void main(String[] args) {
        List<String> input = Util.fileStream("advent2022/advent22").collect(Collectors.toList());

        parseMapLines(input.subList(0, input.size() - 2));
        List<Instruction> instructionList = getInstructions(input);

        part1(instructionList);
    }

    private static void part1(List<Instruction> instructionList) {
        instructionList.forEach(instruction -> {
            logPosition();
            if (instruction.turn == null) {
                move(instruction.move);
            } else {
                rotate(instruction.turn);
            }
        });
        logPosition();
        log.info("Score: {}", 1000 * (playerPosition.down + 1) + 4 * (playerPosition.across + 1) + playerFacing.ordinal());
    }

    private static List<Instruction> getInstructions(List<String> input) {
        List<Instruction> instructionList = newArrayList();
        String instructions = input.get(input.size() - 1);
        int mem = 0;
        for (int c = 0; c < instructions.length(); c++) {
            if (instructions.charAt(c) == 'L' || instructions.charAt(c) == 'R') {
                instructionList.add(new Instruction(Integer.parseInt(instructions.substring(mem, c)), null));
                instructionList.add(new Instruction(0, instructions.charAt(c)));
                mem = c + 1;
            }
        }
        instructionList.add(new Instruction(Integer.parseInt(instructions.substring(mem)), null));
        return instructionList;
    }

    private static void logPosition() {
        log.info("Position: down:{}, across:{}, facing:{}", playerPosition.down + 1, playerPosition.across + 1, playerFacing);
    }

    public static void rotate(Character turn) {
        int newFacing = playerFacing.ordinal() + (turn == 'L' ? -1 : 1);
        int wrapFacing = (newFacing < 0 ? Facing.values().length - 1 : newFacing) % Facing.values().length;
        playerFacing = Facing.values()[wrapFacing];
    }

    static void move(int move) {
        for (int step = 0; step < move; step++) {
            Point newPoint = new Point(playerPosition.down, playerPosition.across, false);
            switch (playerFacing) {
                case RIGHT -> {
                    newPoint = new Point(playerPosition.down, playerPosition.across + 1, false);
                    if (!map.containsKey(newPoint)) {
                        List<Point> sameRow = map.keySet().stream().filter(point -> point.down == playerPosition.down)
                                .sorted(Comparator.comparingInt(o -> o.across))
                                .collect(Collectors.toList());
                        newPoint = sameRow.get(0);
                    }
                }
                case DOWN -> {
                    newPoint = new Point(playerPosition.down + 1, playerPosition.across, false);
                    if (!map.containsKey(newPoint)) {
                        List<Point> sameCol = map.keySet().stream().filter(point -> point.across == playerPosition.across)
                                .sorted(Comparator.comparingInt(o -> o.down))
                                .collect(Collectors.toList());
                        newPoint = sameCol.get(0);
                    }
                }
                case LEFT -> {
                    newPoint = new Point(playerPosition.down, playerPosition.across - 1, false);
                    if (!map.containsKey(newPoint)) {
                        List<Point> sameRow = map.keySet().stream().filter(point -> point.down == playerPosition.down)
                                .sorted(Comparator.comparingInt(o -> o.across))
                                .collect(Collectors.toList());
                        newPoint = sameRow.get(sameRow.size() - 1);
                    }
                }
                case UP -> {
                    newPoint = new Point(playerPosition.down - 1, playerPosition.across, false);
                    if (!map.containsKey(newPoint)) {
                        List<Point> sameCol = map.keySet().stream().filter(point -> point.across == playerPosition.across)
                                .sorted(Comparator.comparingInt(o -> o.down))
                                .collect(Collectors.toList());
                        newPoint = sameCol.get(sameCol.size() - 1);
                    }
                }
            }
            if (map.get(newPoint).isWall) {
                return;
            }
            playerPosition = new Point(newPoint.down, newPoint.across, false);
        }
    }

    @AllArgsConstructor
    static class Instruction {
        int move;
        Character turn;

        @Override
        public String toString() {
            return (move == 0 ? "" : move) + "" + (turn == null ? "" : turn);
        }
    }

    private static void parseMapLines(List<String> mapLines) {
        for (int down = 0; down < mapLines.size(); down++) {
            for (int across = 0; across < mapLines.get(down).length(); across++) {
                char c = mapLines.get(down).charAt(across);
                switch (c) {
                    case '.' -> {
                        Point point = new Point(down, across, false);
                        map.put(point, point);
                        if (playerPosition == null) {
                            playerPosition = new Point(point.down, point.across, false);
                        }
                    }
                    case '#' -> {
                        Point point = new Point(down, across, true);
                        map.put(point, point);
                    }
                }
            }
        }
        playerFacing = Facing.RIGHT;
    }

    record Point(int down, int across, boolean isWall) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return down == point.down && across == point.across;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(down, across);
        }
    }

    enum Facing {
        RIGHT, DOWN, LEFT, UP
    }
}