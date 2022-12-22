package advent2022;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.Util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static advent2022.Advent22.Facing.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Math.sqrt;

// 29364 - too low part 1
@Slf4j
public class Advent22 {
    // tiles in input
    //   1 2
    //   3
    // 4 5
    // 6

    static Map<Point, Point> map = newHashMap();
    static Player player = new Player();
    public static Facing playerFacing;

    public static void main(String[] args) {
        List<String> input = Util.fileStream("advent2022/advent22").collect(Collectors.toList());

        parseMapLines(input.subList(0, input.size() - 2));
        List<Instruction> instructionList = getInstructions(input);

        part1(instructionList);
        part2(instructionList);
    }

    private static void part2(List<Instruction> instructionList) {
        instructionList.forEach(instruction -> {
            if (instruction.turn == null) {
                cubeMove(instruction.move);
            } else {
                rotate(instruction.turn);
            }
        });
        log.info("Part 2 Score: {}", 1000 * (player.position.down + 1) + 4 * (player.position.across + 1) + playerFacing.ordinal());
    }

    private static int getTileSize() {
        return (int) sqrt(map.size() / 6.0);
    }

    static void cubeMove(int move) {
        for (int step = 0; step < move; step++) {
            Point newPoint = new Point(player.position.down, player.position.across, false);
            Facing prevFacing = playerFacing;
            switch (playerFacing) {
                case RIGHT -> {
                    newPoint = new Point(player.position.down, player.position.across + 1, false);
                    if (!map.containsKey(newPoint)) {
                        // tile 2 right wraps to tile 5 left
                        if (player.isOnTile2()) {
                            newPoint = map.get(new Point(3 * getTileSize() - 1 - player.position.down, 2 * getTileSize() - 1, false));
                            playerFacing = LEFT;
                        }
                        // tile 3 right wraps to tile 2 up
                        else if (player.isOnTile3()) {
                            newPoint = map.get(new Point(getTileSize() - 1, getTileSize() + player.position.down, false));
                            playerFacing = UP;
                        }
                        // tile 5 right wraps to tile 2 left
                        else if (player.isOnTile5()) {
                            newPoint = map.get(new Point(3 * getTileSize() - 1 - player.position.down, 3 * getTileSize() - 1, false));
                            playerFacing = LEFT;
                        }
                        // tile 6 right wraps to tile 5 up
                        else if (player.isOnTile6()) {
                            newPoint = map.get(new Point(3 * getTileSize() - 1, player.position.down - 2 * getTileSize(), false));
                            playerFacing = UP;
                        } else {
                            log.error("Impossible State");
                        }
                    }
                }
                case DOWN -> {
                    newPoint = new Point(player.position.down + 1, player.position.across, false);
                    if (!map.containsKey(newPoint)) {
                        // tile 2 down wraps to tile 3 left
                        if (player.isOnTile2()) {
                            newPoint = map.get(new Point(player.position.across - getTileSize(), 2 * getTileSize() - 1, false));
                            playerFacing = LEFT;
                        }
                        // tile 5 down wraps to tile 6 left
                        else if (player.isOnTile5()) {
                            newPoint = map.get(new Point(player.position.across + 2 * getTileSize(), getTileSize() - 1, false));
                            playerFacing = LEFT;
                        }
                        // tile 6 down wraps to tile 2 down
                        else if (player.isOnTile6()) {
                            newPoint = map.get(new Point(0, player.position.across + 2 * getTileSize(), false));
                            playerFacing = DOWN;
                        } else {
                            log.error("Impossible State");
                        }
                    }
                }
                case LEFT -> {
                    newPoint = new Point(player.position.down, player.position.across - 1, false);
                    if (!map.containsKey(newPoint)) {
                        // tile 1 left wraps to tile 4 right
                        if (player.isOnTile1()) {
                            newPoint = map.get(new Point(3 * getTileSize() - 1 - player.position.down, 0, false));
                            playerFacing = RIGHT;
                        }
                        // tile 3 left wraps to tile 4 down
                        else if (player.isOnTile3()) {
                            newPoint = map.get(new Point(2 * getTileSize(), player.position.down - getTileSize(), false));
                            playerFacing = DOWN;
                        }
                        // tile 4 left wraps to tile 1 right
                        else if (player.isOnTile4()) {
                            newPoint = map.get(new Point(3 * getTileSize() - 1 - player.position.across, getTileSize(), false));
                            playerFacing = RIGHT;
                        }
                        // tile 6 left wraps to tile 1 down
                        else if (player.isOnTile6()) {
                            newPoint = map.get(new Point(0, player.position.down - 2 * getTileSize(), false));
                            playerFacing = DOWN;
                        } else {
                            log.error("Impossible State");
                        }
                    }
                }
                case UP -> {
                    newPoint = new Point(player.position.down - 1, player.position.across, false);
                    if (!map.containsKey(newPoint)) {
                        // tile 1 up wraps to tile 6 right
                        if (player.isOnTile1()) {
                            newPoint = map.get(new Point(player.position.across + 2 * getTileSize(), 0, false));
                            playerFacing = RIGHT;
                        }
                        // tile 2 up wraps to tile 6 up
                        else if (player.isOnTile2()) {
                            newPoint = map.get(new Point(4 * getTileSize() - 1, player.position.across - 2 * getTileSize(), false));
                            playerFacing = UP;
                        }
                        // tile 4 up wraps to tile 3 right
                        else if (player.isOnTile4()) {
                            newPoint = map.get(new Point(player.position.across + getTileSize(), getTileSize(), false));
                            playerFacing = RIGHT;
                        } else {
                            log.error("Impossible State");
                        }
                    }
                }
            }
            if (map.get(newPoint).isWall) {
                // return to previous facing since we didn't wrap due to wall
                playerFacing = prevFacing;
                return;
            }
            player.position = new Point(newPoint.down, newPoint.across, false);
        }
    }

    private static void part1(List<Instruction> instructionList) {
        instructionList.forEach(instruction -> {
            if (instruction.turn == null) {
                move(instruction.move);
            } else {
                rotate(instruction.turn);
            }
        });
        log.info("Part 1 Score: {}", 1000 * (player.position.down + 1) + 4 * (player.position.across + 1) + playerFacing.ordinal());
    }

    static void move(int move) {
        for (int step = 0; step < move; step++) {
            Point newPoint = new Point(player.position.down, player.position.across, false);
            switch (playerFacing) {
                case RIGHT -> {
                    newPoint = new Point(player.position.down, player.position.across + 1, false);
                    if (!map.containsKey(newPoint)) {
                        List<Point> sameRow = map.keySet().stream().filter(point -> point.down == player.position.down)
                                .sorted(Comparator.comparingInt(o -> o.across))
                                .collect(Collectors.toList());
                        newPoint = sameRow.get(0);
                    }
                }
                case DOWN -> {
                    newPoint = new Point(player.position.down + 1, player.position.across, false);
                    if (!map.containsKey(newPoint)) {
                        List<Point> sameCol = map.keySet().stream().filter(point -> point.across == player.position.across)
                                .sorted(Comparator.comparingInt(o -> o.down))
                                .collect(Collectors.toList());
                        newPoint = sameCol.get(0);
                    }
                }
                case LEFT -> {
                    newPoint = new Point(player.position.down, player.position.across - 1, false);
                    if (!map.containsKey(newPoint)) {
                        List<Point> sameRow = map.keySet().stream().filter(point -> point.down == player.position.down)
                                .sorted(Comparator.comparingInt(o -> o.across))
                                .collect(Collectors.toList());
                        newPoint = sameRow.get(sameRow.size() - 1);
                    }
                }
                case UP -> {
                    newPoint = new Point(player.position.down - 1, player.position.across, false);
                    if (!map.containsKey(newPoint)) {
                        List<Point> sameCol = map.keySet().stream().filter(point -> point.across == player.position.across)
                                .sorted(Comparator.comparingInt(o -> o.down))
                                .collect(Collectors.toList());
                        newPoint = sameCol.get(sameCol.size() - 1);
                    }
                }
            }
            if (map.get(newPoint).isWall) {
                return;
            }
            player.position = new Point(newPoint.down, newPoint.across, false);
        }
    }

    public static void rotate(Character turn) {
        int newFacing = playerFacing.ordinal() + (turn == 'L' ? -1 : 1);
        int wrapFacing = (newFacing < 0 ? Facing.values().length - 1 : newFacing) % Facing.values().length;
        playerFacing = Facing.values()[wrapFacing];
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

    private static void parseMapLines(List<String> mapLines) {
        for (int down = 0; down < mapLines.size(); down++) {
            for (int across = 0; across < mapLines.get(down).length(); across++) {
                char c = mapLines.get(down).charAt(across);
                switch (c) {
                    case '.' -> {
                        Point point = new Point(down, across, false);
                        map.put(point, point);
                        if (player.position == null) {
                            player.position = new Point(point.down, point.across, false);
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

    static class Player {
        Point position;
        Facing facing;

        boolean isOnTile1() {
            return player.isOnTile(0, 1);
        }

        boolean isOnTile2() {
            return player.isOnTile(0, 2);
        }

        boolean isOnTile3() {
            return player.isOnTile(1, 1);
        }

        boolean isOnTile4() {
            return player.isOnTile(2, 0);
        }

        boolean isOnTile5() {
            return player.isOnTile(2, 1);
        }

        boolean isOnTile6() {
            return player.isOnTile(3, 0);
        }

        boolean isOnTile(int downTile, int acrossTile) {
            return this.position.down >= downTile * getTileSize() &&
                    position.down < (downTile + 1) * getTileSize() &&
                    position.across >= acrossTile * getTileSize() &&
                    position.across < (acrossTile + 1) * getTileSize();
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