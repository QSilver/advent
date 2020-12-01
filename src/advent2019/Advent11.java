package advent2019;

import com.google.common.collect.Lists;
import util.Util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Sets.newHashSet;

// 500 - too low
// 2375 - too high

public class Advent11 {
    private static final int SIZE = 10000;
    private static final int DELTA_X = 50;
    private static final int DELTA_Y = 6;

    private static final int[][] ship = new int[SIZE][SIZE];
    private static final Point robot = new Point(SIZE / 2, SIZE / 2, 0);
    static Facing facing = Facing.UP;
    private static final String FACE_PAINT = "<^>v";

    private static final Set<Point> visited = newHashSet();

    public static void main(String[] args) {
        System.out.println(solve());
    }

    private static int solve() {
        Computer paintingRobot = new Computer(Util.splitLine(Util.fileStream("advent2019/advent11"))
                                                  .stream()
                                                  .map(Long::parseLong)
                                                  .collect(Collectors.toCollection(Lists::newArrayList)));

        ship[robot.y][robot.x] = 1;
        while (paintingRobot.isRunning()) {
            paintingRobot.addInput(ship[robot.y][robot.x]);
            paintingRobot.solve();

            ship[robot.y][robot.x] = (int) paintingRobot.getOutput();
            visited.add(new Point(robot.x, robot.y, 0));
            move(getFacing(paintingRobot.getOutput()));
            draw();
        }

        return visited.size();
    }

    static Facing getFacing(long newFacing) {
        int i = facing.ordinal() + (newFacing == 0 ? -1 : 1);
        int i1 = i < 0 ? Facing.values().length - 1 : i;
        int i2 = i1 % Facing.values().length;
        return Facing.values()[i2];
    }

    private static void move(Facing newFacing) {
        facing = newFacing;
        switch (newFacing) {
            case LEFT:
                robot.x -= 1;
                break;
            case UP:
                robot.y -= 1;
                break;
            case DOWN:
                robot.y += 1;
                break;
            case RIGHT:
                robot.x += 1;
                break;
        }
    }

    private static void draw() {
        for (int y = robot.y - DELTA_Y; y < robot.y + DELTA_Y; y++) {
            for (int x = robot.x - DELTA_X; x < robot.x + DELTA_X; x++) {
                if (robot.y == y && robot.x == x) {
                    System.out.print(FACE_PAINT.charAt(facing.ordinal()));
                } else {
                    System.out.print(ship[y][x] == 0 ? "." : "#");
                }
            }
            System.out.println();
        }
        System.out.println(IntStream.range(0, 2 * DELTA_X)
                                    .mapToObj(i -> "=")
                                    .collect(Collectors.joining()));
    }
}

enum Facing {
    LEFT, UP, RIGHT, DOWN
}
